package com.example.intelligent_inventory_prediction_system.service.executor;

import com.example.intelligent_inventory_prediction_system.dto.response.ProductResponseDTO;
import com.example.intelligent_inventory_prediction_system.mapper.response.ProductResponseMapper;
import com.example.intelligent_inventory_prediction_system.model.Product;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import com.example.intelligent_inventory_prediction_system.repository.ProductRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductQueryExecutor Tests")
class ProductQueryExecutorTest {

    private static final String OPERATION_DESCRIPTION = "Test Operation";

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductResponseMapper productResponseMapper;

    @InjectMocks
    private ProductQueryExecutor productQueryExecutor;

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
            var specification = givenAnySpecification();
            var products = testDataFactory.createProductList();
            var expectedDtos = testDataFactory.createProductResponseDTOList();

            givenRepositoryFindsProductsWithSpecification(specification, products);
            givenMapperConvertsProductListToDTOList(products, expectedDtos);

            var result = whenExecutingSpecificationQuery(specification);

            thenResultShouldEqual(result, expectedDtos);
            thenRepositoryWasCalledWithSpecification(specification);
            thenMapperWasCalledWithProductList(products);
        }

        @Test
        @DisplayName("Should return empty list when no products match specification")
        void executeSpecificationQuery_withNoMatches_shouldReturnEmptyList() {
            var specification = givenAnySpecification();
            var emptyList = givenEmptyProductList();
            var emptyDtoList = givenEmptyDTOList();

            givenRepositoryFindsProductsWithSpecification(specification, emptyList);
            givenMapperConvertsProductListToDTOList(emptyList, emptyDtoList);

            var result = whenExecutingSpecificationQuery(specification);

            thenResultShouldBeEmpty(result);
            thenRepositoryWasCalledWithSpecification(specification);
            thenMapperWasCalledWithProductList(emptyList);
        }

        private List<ProductResponseDTO> whenExecutingSpecificationQuery(Specification<Product> specification) {
            return productQueryExecutor.executeSpecificationQuery(specification, OPERATION_DESCRIPTION);
        }
    }

    @Nested
    @DisplayName("Paged Query Tests")
    class PagedQueryTests {

        @Test
        @DisplayName("Should execute paged query and return correct page structure")
        void executePagedQuery_withValidPageable_shouldReturnCorrectPageStructure() {
            var pageable = givenPageable(0, 10);
            var products = testDataFactory.createProductList();
            var pagedProducts = givenPagedProducts(products, pageable, products.size());
            var dtos = testDataFactory.createProductResponseDTOList();

            givenRepositoryFindsPagedProducts(pageable, pagedProducts);
            givenMapperConvertsIndividualProducts(products, dtos);

            var result = whenExecutingPagedQuery(pageable);

            thenPageShouldHaveCorrectStructure(result, products.size(), 0, products.size());
            thenRepositoryWasCalledWithPageable(pageable);
            thenMapperWasCalledForEachProduct(products.size());
        }

        @Test
        @DisplayName("Should handle pagination correctly for second page")
        void executePagedQuery_withSecondPage_shouldReturnCorrectElements() {
            var pageable = givenPageable(1, 2);
            var allProducts = testDataFactory.createProductList();
            var pageProducts = givenSecondPageProducts(allProducts);
            var pagedProducts = givenPagedProducts(pageProducts, pageable, allProducts.size());
            var expectedDto = testDataFactory.createProductResponseDTOList().get(2);

            givenRepositoryFindsPagedProducts(pageable, pagedProducts);
            givenMapperConvertsProduct(pageProducts.get(0), expectedDto);

            var result = whenExecutingPagedQuery(pageable);

            thenPageShouldHaveCorrectStructure(result, allProducts.size(), 1, 1);
            thenRepositoryWasCalledWithPageable(pageable);
            thenMapperWasCalledOnce();
        }

        @Test
        @DisplayName("Should return empty page when no products exist")
        void executePagedQuery_withNoProducts_shouldReturnEmptyPage() {
            var pageable = givenPageable(0, 10);
            var emptyPage = givenEmptyPage(pageable);

            givenRepositoryFindsPagedProducts(pageable, emptyPage);

            var result = whenExecutingPagedQuery(pageable);

            thenPageShouldBeEmpty(result);
            thenRepositoryWasCalledWithPageable(pageable);
            thenMapperWasNotCalled();
        }

        private Page<ProductResponseDTO> whenExecutingPagedQuery(Pageable pageable) {
            return productQueryExecutor.executePagedQuery(pageable, OPERATION_DESCRIPTION);
        }

        private List<Product> givenSecondPageProducts(List<Product> allProducts) {
            return List.of(allProducts.get(2));
        }
    }

    @Nested
    @DisplayName("Simple Query Tests")
    class SimpleQueryTests {

        @Test
        @DisplayName("Should execute simple query and return all products")
        void executeSimpleQuery_shouldReturnAllProducts() {
            var products = testDataFactory.createProductList();
            var expectedDtos = testDataFactory.createProductResponseDTOList();

            givenRepositoryFindsAllProducts(products);
            givenMapperConvertsProductListToDTOList(products, expectedDtos);

            var result = whenExecutingSimpleQuery();

            thenResultShouldEqual(result, expectedDtos);
            thenRepositoryFindAllWasCalled();
            thenMapperWasCalledWithProductList(products);
        }

        @Test
        @DisplayName("Should return empty list when no products exist")
        void executeSimpleQuery_withNoProducts_shouldReturnEmptyList() {
            var emptyList = givenEmptyProductList();
            var emptyDtoList = givenEmptyDTOList();

            givenRepositoryFindsAllProducts(emptyList);
            givenMapperConvertsProductListToDTOList(emptyList, emptyDtoList);

            var result = whenExecutingSimpleQuery();

            thenResultShouldBeEmpty(result);
            thenRepositoryFindAllWasCalled();
            thenMapperWasCalledWithProductList(emptyList);
        }

        private List<ProductResponseDTO> whenExecutingSimpleQuery() {
            return productQueryExecutor.executeSimpleQuery(OPERATION_DESCRIPTION);
        }
    }

    // Given methods
    private Specification<Product> givenAnySpecification() {
        return mock(Specification.class);
    }

    private List<Product> givenEmptyProductList() {
        return List.of();
    }

    private List<ProductResponseDTO> givenEmptyDTOList() {
        return List.of();
    }

    private Pageable givenPageable(int page, int size) {
        return PageRequest.of(page, size);
    }

    private Page<Product> givenPagedProducts(List<Product> products, Pageable pageable, long totalElements) {
        return new PageImpl<>(products, pageable, totalElements);
    }

    private Page<Product> givenEmptyPage(Pageable pageable) {
        return Page.empty(pageable);
    }

    private void givenRepositoryFindsProductsWithSpecification(Specification<Product> specification, List<Product> products) {
        when(productRepository.findAll(specification)).thenReturn(products);
    }

    private void givenRepositoryFindsPagedProducts(Pageable pageable, Page<Product> pagedProducts) {
        when(productRepository.findAll(pageable)).thenReturn(pagedProducts);
    }

    private void givenRepositoryFindsAllProducts(List<Product> products) {
        when(productRepository.findAll()).thenReturn(products);
    }

    private void givenMapperConvertsProductListToDTOList(List<Product> products, List<ProductResponseDTO> dtos) {
        when(productResponseMapper.toProductResponseDTOList(products)).thenReturn(dtos);
    }

    private void givenMapperConvertsIndividualProducts(List<Product> products, List<ProductResponseDTO> dtos) {
        for (int i = 0; i < products.size(); i++) {
            when(productResponseMapper.toProductResponseDTO(products.get(i))).thenReturn(dtos.get(i));
        }
    }

    private void givenMapperConvertsProduct(Product product, ProductResponseDTO dto) {
        when(productResponseMapper.toProductResponseDTO(product)).thenReturn(dto);
    }

    // Then methods
    private void thenResultShouldEqual(List<ProductResponseDTO> result, List<ProductResponseDTO> expected) {
        assertThat(result).isEqualTo(expected);
    }

    private void thenResultShouldBeEmpty(List<ProductResponseDTO> result) {
        assertThat(result).isEmpty();
    }

    private void thenPageShouldHaveCorrectStructure(Page<ProductResponseDTO> result, long totalElements, int pageNumber, int contentSize) {
        assertThat(result.getTotalElements()).isEqualTo(totalElements);
        assertThat(result.getNumber()).isEqualTo(pageNumber);
        assertThat(result.getContent()).hasSize(contentSize);
    }

    private void thenPageShouldBeEmpty(Page<ProductResponseDTO> result) {
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    private void thenRepositoryWasCalledWithSpecification(Specification<Product> specification) {
        verify(productRepository).findAll(specification);
    }

    private void thenRepositoryWasCalledWithPageable(Pageable pageable) {
        verify(productRepository).findAll(pageable);
    }

    private void thenRepositoryFindAllWasCalled() {
        verify(productRepository).findAll();
    }

    private void thenMapperWasCalledWithProductList(List<Product> products) {
        verify(productResponseMapper).toProductResponseDTOList(products);
    }

    private void thenMapperWasCalledForEachProduct(int times) {
        verify(productResponseMapper, times(times)).toProductResponseDTO(any(Product.class));
    }

    private void thenMapperWasCalledOnce() {
        verify(productResponseMapper, times(1)).toProductResponseDTO(any(Product.class));
    }

    private void thenMapperWasNotCalled() {
        verifyNoInteractions(productResponseMapper);
    }

    private static class TestDataFactory {

        List<Product> createProductList() {
            return List.of(
                    createProduct(1L, "Laptop", "Electronics", "Gaming laptop", 1500.0, ProductStatus.AVAILABLE),
                    createProduct(2L, "Smartphone", "Electronics", "Flagship smartphone", 999.99, ProductStatus.AVAILABLE),
                    createProduct(3L, "Desk Chair", "Furniture", "Ergonomic chair", 199.99, ProductStatus.NOT_AVAILABLE)
            );
        }

        List<ProductResponseDTO> createProductResponseDTOList() {
            return List.of(
                    createDTO(1L, "Laptop", "Electronics", "Gaming laptop", 1500.0, ProductStatus.AVAILABLE),
                    createDTO(2L, "Smartphone", "Electronics", "Flagship smartphone", 999.99, ProductStatus.AVAILABLE),
                    createDTO(3L, "Desk Chair", "Furniture", "Ergonomic chair", 199.99, ProductStatus.NOT_AVAILABLE)
            );
        }

        private Product createProduct(Long id, String name, String category, String description, Double price, ProductStatus status) {
            var product = new Product();
            product.setId(id);
            product.setName(name);
            product.setCategory(category);
            product.setDescription(description);
            product.setPrice(price);
            product.setStatus(status);
            return product;
        }

        private ProductResponseDTO createDTO(Long id, String name, String category, String description, Double price, ProductStatus status) {
            var dto = new ProductResponseDTO();
            dto.setId(id);
            dto.setName(name);
            dto.setCategory(category);
            dto.setDescription(description);
            dto.setPrice(price);
            dto.setStatus(status);
            return dto;
        }
    }
}
