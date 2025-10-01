package com.example.intelligent_inventory_prediction_system.service.executor;

import com.example.intelligent_inventory_prediction_system.dto.response.InventoryResponseDTO;
import com.example.intelligent_inventory_prediction_system.mapper.response.InventoryResponseMapper;
import com.example.intelligent_inventory_prediction_system.model.Inventory;
import com.example.intelligent_inventory_prediction_system.model.Product;
import com.example.intelligent_inventory_prediction_system.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryQueryExecutor Tests")
class InventoryQueryExecutorTest {

    private static final int PAGE_SIZE = 10;
    private static final int SMALL_PAGE_SIZE = 2;
    private static final long EXPECTED_TOTAL_ELEMENTS = 3L;
    private static final String OPERATION_DESCRIPTION = "Test Operation";

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryResponseMapper inventoryResponseMapper;

    @InjectMocks
    private InventoryQueryExecutor inventoryQueryExecutor;

    private TestDataFactory testDataFactory;

    @BeforeEach
    void setUp() {
        testDataFactory = new TestDataFactory();
    }

    @Nested
    @DisplayName("Specification Query Tests")
    class SpecificationQueryTests {

        @Test
        @DisplayName("Should execute specification query and return mapped results")
        void executeSpecificationQuery_withValidSpecification_shouldReturnMappedDTOs() {
            Specification<Inventory> specification = givenAnySpecification();
            List<Inventory> inventories = testDataFactory.createInventoryList();
            List<InventoryResponseDTO> expectedDtos = testDataFactory.createInventoryResponseDTOList();

            givenRepositoryReturnsInventories(specification, inventories);
            givenMapperMapsInventoryList(inventories, expectedDtos);

            List<InventoryResponseDTO> result = inventoryQueryExecutor.executeSpecificationQuery(specification, OPERATION_DESCRIPTION);

            assertThat(result).isEqualTo(expectedDtos);
            verifyRepositoryCalledWithSpecification(specification);
            verifyMapperCalledWithInventoryList(inventories);
        }

        @Test
        @DisplayName("Should return empty list when no inventories match specification")
        void executeSpecificationQuery_withNoMatches_shouldReturnEmptyList() {
            Specification<Inventory> specification = givenAnySpecification();
            List<Inventory> emptyList = List.of();
            List<InventoryResponseDTO> emptyDtoList = List.of();

            givenRepositoryReturnsInventories(specification, emptyList);
            givenMapperMapsInventoryList(emptyList, emptyDtoList);

            List<InventoryResponseDTO> result = inventoryQueryExecutor.executeSpecificationQuery(specification, OPERATION_DESCRIPTION);

            assertThat(result).isEmpty();
            verifyRepositoryCalledWithSpecification(specification);
            verifyMapperCalledWithInventoryList(emptyList);
        }
    }

    @Nested
    @DisplayName("Paged Query Tests")
    class PagedQueryTests {

        @Test
        @DisplayName("Should execute paged query and return mapped page")
        void executePagedQuery_withValidPageable_shouldReturnMappedPage() {
            Pageable pageable = createPageable(0, PAGE_SIZE);
            List<Inventory> inventories = testDataFactory.createInventoryList();
            Page<Inventory> pagedInventories = createPagedInventories(inventories, pageable, inventories.size());
            List<InventoryResponseDTO> dtos = testDataFactory.createInventoryResponseDTOList();

            givenRepositoryReturnsPagedInventories(pageable, pagedInventories);
            givenMapperMapsIndividualInventories(inventories, dtos);

            Page<InventoryResponseDTO> result = inventoryQueryExecutor.executePagedQuery(pageable, OPERATION_DESCRIPTION);

            assertThat(result.getTotalElements()).isEqualTo(EXPECTED_TOTAL_ELEMENTS);
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getNumber()).isEqualTo(0);
            verifyRepositoryCalledWithPageable(pageable);
            verifyMapperCalledForEachInventory(3);
        }

        @Test
        @DisplayName("Should handle different page sizes correctly")
        void executePagedQuery_withSmallPageSize_shouldReturnCorrectPage() {
            Pageable pageable = createPageable(1, SMALL_PAGE_SIZE);
            List<Inventory> allInventories = testDataFactory.createInventoryList();
            List<Inventory> pageInventories = List.of(allInventories.get(2));
            Page<Inventory> pagedInventories = createPagedInventories(pageInventories, pageable, allInventories.size());
            List<InventoryResponseDTO> dtos = testDataFactory.createInventoryResponseDTOList();

            givenRepositoryReturnsPagedInventories(pageable, pagedInventories);
            when(inventoryResponseMapper.toInventoryResponseDTO(allInventories.get(2))).thenReturn(dtos.get(2));

            Page<InventoryResponseDTO> result = inventoryQueryExecutor.executePagedQuery(pageable, OPERATION_DESCRIPTION);

            assertThat(result.getTotalElements()).isEqualTo(EXPECTED_TOTAL_ELEMENTS);
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getNumber()).isEqualTo(1);
            verifyRepositoryCalledWithPageable(pageable);
            verify(inventoryResponseMapper, times(1)).toInventoryResponseDTO(any(Inventory.class));
        }

        @Test
        @DisplayName("Should return empty page when no inventories exist")
        void executePagedQuery_withNoInventories_shouldReturnEmptyPage() {
            Pageable pageable = createPageable(0, PAGE_SIZE);
            Page<Inventory> emptyPage = Page.empty(pageable);

            givenRepositoryReturnsPagedInventories(pageable, emptyPage);

            Page<InventoryResponseDTO> result = inventoryQueryExecutor.executePagedQuery(pageable, OPERATION_DESCRIPTION);

            assertThat(result.getTotalElements()).isEqualTo(0);
            assertThat(result.getContent()).isEmpty();
            verifyRepositoryCalledWithPageable(pageable);
            verifyNoMapperCalls();
        }
    }

    @Nested
    @DisplayName("Simple Query Tests")
    class SimpleQueryTests {

        @Test
        @DisplayName("Should execute simple query and return all inventories")
        void executeSimpleQuery_shouldReturnAllInventories() {
            List<Inventory> inventories = testDataFactory.createInventoryList();
            List<InventoryResponseDTO> expectedDtos = testDataFactory.createInventoryResponseDTOList();

            givenRepositoryReturnsAllInventories(inventories);
            givenMapperMapsInventoryList(inventories, expectedDtos);

            List<InventoryResponseDTO> result = inventoryQueryExecutor.executeSimpleQuery(OPERATION_DESCRIPTION);

            assertThat(result).isEqualTo(expectedDtos);
            verifyRepositoryCalledFindAll();
            verifyMapperCalledWithInventoryList(inventories);
        }

        @Test
        @DisplayName("Should return empty list when no inventories exist")
        void executeSimpleQuery_withNoInventories_shouldReturnEmptyList() {
            List<Inventory> emptyList = List.of();
            List<InventoryResponseDTO> emptyDtoList = List.of();

            givenRepositoryReturnsAllInventories(emptyList);
            givenMapperMapsInventoryList(emptyList, emptyDtoList);

            List<InventoryResponseDTO> result = inventoryQueryExecutor.executeSimpleQuery(OPERATION_DESCRIPTION);

            assertThat(result).isEmpty();
            verifyRepositoryCalledFindAll();
            verifyMapperCalledWithInventoryList(emptyList);
        }
    }

    @SuppressWarnings("unchecked")
    private Specification<Inventory> givenAnySpecification() {
        return mock(Specification.class);
    }

    private void givenRepositoryReturnsInventories(Specification<Inventory> specification, List<Inventory> inventories) {
        when(inventoryRepository.findAll(specification)).thenReturn(inventories);
    }

    private void givenRepositoryReturnsPagedInventories(Pageable pageable, Page<Inventory> pagedInventories) {
        when(inventoryRepository.findAll(pageable)).thenReturn(pagedInventories);
    }

    private void givenRepositoryReturnsAllInventories(List<Inventory> inventories) {
        when(inventoryRepository.findAll()).thenReturn(inventories);
    }

    private void givenMapperMapsInventoryList(List<Inventory> inventories, List<InventoryResponseDTO> dtos) {
        when(inventoryResponseMapper.toInventoryResponseDTOList(inventories)).thenReturn(dtos);
    }

    private void givenMapperMapsIndividualInventories(List<Inventory> inventories, List<InventoryResponseDTO> dtos) {
        for (int i = 0; i < inventories.size(); i++) {
            when(inventoryResponseMapper.toInventoryResponseDTO(inventories.get(i))).thenReturn(dtos.get(i));
        }
    }

    private Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size);
    }

    private Page<Inventory> createPagedInventories(List<Inventory> inventories, Pageable pageable, long totalElements) {
        return new PageImpl<>(inventories, pageable, totalElements);
    }

    private void verifyRepositoryCalledWithSpecification(Specification<Inventory> specification) {
        verify(inventoryRepository).findAll(specification);
    }

    private void verifyRepositoryCalledWithPageable(Pageable pageable) {
        verify(inventoryRepository).findAll(pageable);
    }

    private void verifyRepositoryCalledFindAll() {
        verify(inventoryRepository).findAll();
    }

    private void verifyMapperCalledWithInventoryList(List<Inventory> inventories) {
        verify(inventoryResponseMapper).toInventoryResponseDTOList(inventories);
    }

    private void verifyMapperCalledForEachInventory(int times) {
        verify(inventoryResponseMapper, times(times)).toInventoryResponseDTO(any(Inventory.class));
    }

    private void verifyNoMapperCalls() {
        verifyNoInteractions(inventoryResponseMapper);
    }

    private static class TestDataFactory {

        List<Inventory> createInventoryList() {
            Product product1 = createProduct(100L, "Laptop");
            Product product2 = createProduct(101L, "Smartphone");
            Product product3 = createProduct(102L, "Desk Chair");

            return List.of(
                    createInventory(1L, product1, 50, LocalDateTime.now()),
                    createInventory(2L, product2, 120, LocalDateTime.now()),
                    createInventory(3L, product3, 30, LocalDateTime.now())
            );
        }

        List<InventoryResponseDTO> createInventoryResponseDTOList() {
            return List.of(
                    createDTO(1L, 100L, 50),
                    createDTO(2L, 101L, 120),
                    createDTO(3L, 102L, 30)
            );
        }

        Product createProduct(Long id, String name) {
            Product product = new Product();
            product.setId(id);
            product.setName(name);
            return product;
        }

        Inventory createInventory(Long id, Product product, Integer currentStock, LocalDateTime lastUpdated) {
            Inventory inventory = new Inventory();
            inventory.setId(id);
            inventory.setProduct(product);
            inventory.setCurrentStock(currentStock);
            inventory.setLastUpdated(lastUpdated);
            return inventory;
        }

        InventoryResponseDTO createDTO(Long id, Long productId, Integer currentStock) {
            InventoryResponseDTO dto = new InventoryResponseDTO();
            dto.setId(id);
            dto.setProductId(productId);
            dto.setCurrentStock(currentStock);
            dto.setLastUpdated(LocalDateTime.now());
            return dto;
        }
    }
}
