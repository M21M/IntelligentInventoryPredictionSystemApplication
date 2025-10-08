package com.example.intelligent_inventory_prediction_system.service;

import com.example.intelligent_inventory_prediction_system.dto.response.InventoryResponseDTO;
import com.example.intelligent_inventory_prediction_system.dto.search.InventorySearchCriteria;
import com.example.intelligent_inventory_prediction_system.service.executor.InventoryQueryExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventorySearchService Tests")
class InventorySearchServiceTest {

    @Mock
    private InventoryQueryExecutor queryExecutor;

    @InjectMocks
    private InventorySearchService inventorySearchService;

    private TestDataFactory testDataFactory;

    @BeforeEach
    void setUp() {
        testDataFactory = new TestDataFactory();
    }

    @Nested
    @DisplayName("Advanced Search Tests")
    class AdvancedSearchTests {

        @Test
        @DisplayName("Should search inventories with multiple criteria and return matching results")
        void searchInventories_withMultipleCriteria_shouldReturnMatchingInventories() {
            InventorySearchCriteria searchCriteria = testDataFactory.createAdvancedSearchCriteria();
            List<InventoryResponseDTO> expectedInventories = testDataFactory.createLowStockInventories();

            givenQueryExecutorReturnsInventories(expectedInventories, "advanced search");

            List<InventoryResponseDTO> result = whenSearchingInventories(searchCriteria);

            thenResultShouldContainExpectedInventories(result, expectedInventories);
            thenQueryExecutorWasCalledWithOperation("advanced search");
        }

        @Test
        @DisplayName("Should return all inventories when criteria is empty")
        void searchInventories_withEmptyCriteria_shouldReturnAllInventories() {
            InventorySearchCriteria emptyCriteria = testDataFactory.createEmptySearchCriteria();
            List<InventoryResponseDTO> allInventories = testDataFactory.createAllInventories();

            givenQueryExecutorReturnsInventories(allInventories, "advanced search");

            List<InventoryResponseDTO> result = whenSearchingInventories(emptyCriteria);

            thenResultShouldContainExpectedInventories(result, allInventories);
            thenQueryExecutorWasCalledWithOperation("advanced search");
        }

        @Test
        @DisplayName("Should return empty list when no inventories match criteria")
        void searchInventories_withNoMatches_shouldReturnEmptyList() {
            InventorySearchCriteria searchCriteria = testDataFactory.createNoMatchSearchCriteria();
            List<InventoryResponseDTO> emptyList = testDataFactory.createEmptyInventoryList();

            givenQueryExecutorReturnsInventories(emptyList, "advanced search");

            List<InventoryResponseDTO> result = whenSearchingInventories(searchCriteria);

            thenResultShouldBeEmpty(result);
            thenQueryExecutorWasCalledWithOperation("advanced search");
        }

        private List<InventoryResponseDTO> whenSearchingInventories(InventorySearchCriteria criteria) {
            return inventorySearchService.searchInventories(criteria);
        }
    }

    @Nested
    @DisplayName("Simple Search Tests")
    class SimpleSearchTests {

        @Test
        @DisplayName("Should find inventory by product ID")
        void findByProductId_withValidId_shouldReturnMatchingInventory() {
            Long productId = 1L;
            List<InventoryResponseDTO> expectedInventories = testDataFactory.createInventoriesForProduct1();
            String expectedOperation = "find by product id: " + productId;

            givenQueryExecutorReturnsInventories(expectedInventories, expectedOperation);

            List<InventoryResponseDTO> result = whenFindingByProductId(productId);

            thenResultShouldContainExpectedInventories(result, expectedInventories);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        @Test
        @DisplayName("Should return empty list when product ID not found")
        void findByProductId_withNonExistentId_shouldReturnEmptyList() {
            Long productId = 999L;
            List<InventoryResponseDTO> emptyList = testDataFactory.createEmptyInventoryList();
            String expectedOperation = "find by product id: " + productId;

            givenQueryExecutorReturnsInventories(emptyList, expectedOperation);

            List<InventoryResponseDTO> result = whenFindingByProductId(productId);

            thenResultShouldBeEmpty(result);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        private List<InventoryResponseDTO> whenFindingByProductId(Long productId) {
            return inventorySearchService.findByProductId(productId);
        }
    }

    @Nested
    @DisplayName("Stock Range Search Tests")
    class StockRangeSearchTests {

        @Test
        @DisplayName("Should find inventories within stock range")
        void findByStockRange_withBothMinAndMax_shouldReturnInventoriesInRange() {
            Integer minStock = 50;
            Integer maxStock = 200;
            List<InventoryResponseDTO> expectedInventories = testDataFactory.createMediumStockInventories();
            String expectedOperation = String.format("find by stock range: %d - %d", minStock, maxStock);

            givenQueryExecutorReturnsInventories(expectedInventories, expectedOperation);

            List<InventoryResponseDTO> result = whenFindingByStockRange(minStock, maxStock);

            thenResultShouldContainExpectedInventories(result, expectedInventories);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        @Test
        @DisplayName("Should find inventories above minimum stock only")
        void findByStockRange_withMinStockOnly_shouldReturnInventoriesAboveMin() {
            Integer minStock = 100;
            List<InventoryResponseDTO> expectedInventories = testDataFactory.createHighStockInventories();
            String expectedOperation = String.format("find by stock range: %d - %s", minStock, (Integer) null);

            givenQueryExecutorReturnsInventories(expectedInventories, expectedOperation);

            List<InventoryResponseDTO> result = whenFindingByStockRange(minStock, null);

            thenResultShouldContainExpectedInventories(result, expectedInventories);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        @Test
        @DisplayName("Should find inventories below maximum stock only")
        void findByStockRange_withMaxStockOnly_shouldReturnInventoriesBelowMax() {
            Integer maxStock = 100;
            List<InventoryResponseDTO> expectedInventories = testDataFactory.createLowStockInventories();
            String expectedOperation = String.format("find by stock range: %s - %d", (Integer) null, maxStock);

            givenQueryExecutorReturnsInventories(expectedInventories, expectedOperation);

            List<InventoryResponseDTO> result = whenFindingByStockRange(null, maxStock);

            thenResultShouldContainExpectedInventories(result, expectedInventories);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        @Test
        @DisplayName("Should return empty list when no inventories in range")
        void findByStockRange_withNoMatches_shouldReturnEmptyList() {
            Integer minStock = 1000;
            Integer maxStock = 2000;
            List<InventoryResponseDTO> emptyList = testDataFactory.createEmptyInventoryList();
            String expectedOperation = String.format("find by stock range: %d - %d", minStock, maxStock);

            givenQueryExecutorReturnsInventories(emptyList, expectedOperation);

            List<InventoryResponseDTO> result = whenFindingByStockRange(minStock, maxStock);

            thenResultShouldBeEmpty(result);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        private List<InventoryResponseDTO> whenFindingByStockRange(Integer minStock, Integer maxStock) {
            return inventorySearchService.findByStockRange(minStock, maxStock);
        }
    }

    @Nested
    @DisplayName("Minimum Stock Search Tests")
    class MinimumStockSearchTests {

        @Test
        @DisplayName("Should find inventories with minimum stock")
        void findByMinimumStock_withThreshold_shouldReturnInventoriesAboveThreshold() {
            Integer minStock = 100;
            List<InventoryResponseDTO> expectedInventories = testDataFactory.createHighStockInventories();
            String expectedOperation = "find inventories with minimum stock: " + minStock;

            givenQueryExecutorReturnsInventories(expectedInventories, expectedOperation);

            List<InventoryResponseDTO> result = whenFindingByMinimumStock(minStock);

            thenResultShouldContainExpectedInventories(result, expectedInventories);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        @Test
        @DisplayName("Should return empty list when no inventories above threshold")
        void findByMinimumStock_withHighThreshold_shouldReturnEmptyList() {
            Integer minStock = 1000;
            List<InventoryResponseDTO> emptyList = testDataFactory.createEmptyInventoryList();
            String expectedOperation = "find inventories with minimum stock: " + minStock;

            givenQueryExecutorReturnsInventories(emptyList, expectedOperation);

            List<InventoryResponseDTO> result = whenFindingByMinimumStock(minStock);

            thenResultShouldBeEmpty(result);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        @Test
        @DisplayName("Should find all inventories with zero threshold")
        void findByMinimumStock_withZeroThreshold_shouldReturnAllInventories() {
            Integer minStock = 0;
            List<InventoryResponseDTO> allInventories = testDataFactory.createAllInventories();
            String expectedOperation = "find inventories with minimum stock: " + minStock;

            givenQueryExecutorReturnsInventories(allInventories, expectedOperation);

            List<InventoryResponseDTO> result = whenFindingByMinimumStock(minStock);

            thenResultShouldContainExpectedInventories(result, allInventories);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        private List<InventoryResponseDTO> whenFindingByMinimumStock(Integer minStock) {
            return inventorySearchService.findByMinimumStock(minStock);
        }
    }

    private void givenQueryExecutorReturnsInventories(List<InventoryResponseDTO> inventories, String operation) {
        when(queryExecutor.executeSpecificationQuery(any(Specification.class), eq(operation)))
                .thenReturn(inventories);
    }

    private void thenResultShouldContainExpectedInventories(List<InventoryResponseDTO> result, List<InventoryResponseDTO> expected) {
        assertThat(result).isNotNull();
        assertThat(result).hasSize(expected.size());
        assertThat(result).containsExactlyElementsOf(expected);
    }

    private void thenResultShouldBeEmpty(List<InventoryResponseDTO> result) {
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    private void thenQueryExecutorWasCalledWithOperation(String operation) {
        verify(queryExecutor).executeSpecificationQuery(any(Specification.class), eq(operation));
    }

    static class TestDataFactory {

        InventorySearchCriteria createAdvancedSearchCriteria() {
            return InventorySearchCriteria.builder()
                    .productId(1L)
                    .minStock(50)
                    .maxStock(100)
                    .build();
        }

        InventorySearchCriteria createEmptySearchCriteria() {
            return InventorySearchCriteria.builder().build();
        }

        InventorySearchCriteria createNoMatchSearchCriteria() {
            return InventorySearchCriteria.builder()
                    .productId(999L)
                    .minStock(10000)
                    .build();
        }

        List<InventoryResponseDTO> createInventoriesForProduct1() {
            return List.of(
                    createDTO(1L, 1L, 150)
            );
        }

        List<InventoryResponseDTO> createLowStockInventories() {
            return List.of(
                    createDTO(2L, 2L, 75),
                    createDTO(4L, 4L, 30)
            );
        }

        List<InventoryResponseDTO> createMediumStockInventories() {
            return List.of(
                    createDTO(2L, 2L, 75),
                    createDTO(1L, 1L, 150)
            );
        }

        List<InventoryResponseDTO> createHighStockInventories() {
            return List.of(
                    createDTO(1L, 1L, 150),
                    createDTO(3L, 3L, 250)
            );
        }

        List<InventoryResponseDTO> createAllInventories() {
            return List.of(
                    createDTO(1L, 1L, 150),
                    createDTO(2L, 2L, 75),
                    createDTO(3L, 3L, 250),
                    createDTO(4L, 4L, 30),
                    createDTO(5L, 5L, 0)
            );
        }

        List<InventoryResponseDTO> createEmptyInventoryList() {
            return List.of();
        }

        private InventoryResponseDTO createDTO(Long id, Long productId, Integer currentStock) {
            InventoryResponseDTO dto = new InventoryResponseDTO();
            dto.setId(id);
            dto.setProductId(productId);
            dto.setCurrentStock(currentStock);
            dto.setLastUpdated(LocalDateTime.now());
            return dto;
        }
    }
}
