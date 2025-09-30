package com.example.intelligent_inventory_prediction_system.service;

import com.example.intelligent_inventory_prediction_system.dto.response.ProductResponseDTO;
import com.example.intelligent_inventory_prediction_system.dto.search.ProductSearchCriteria;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import com.example.intelligent_inventory_prediction_system.service.executor.ProductQueryExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductSearchService Tests")
class ProductSearchServiceTest {

    @Mock
    private ProductQueryExecutor productQueryExecutor;

    @InjectMocks
    private ProductSearchService productSearchService;

    private TestDataFactory testDataFactory;

    @BeforeEach
    void setUp() {
        testDataFactory = new TestDataFactory();
    }

    @Nested
    @DisplayName("Advanced Search Tests")
    class AdvancedSearchTests {

        @Test
        @DisplayName("Should search products with multiple criteria and return matching results")
        void searchProducts_withMultipleCriteria_shouldReturnMatchingProducts() {
            var searchCriteria = testDataFactory.createAdvancedSearchCriteria();
            var expectedProducts = testDataFactory.createElectronicsProducts();

            givenQueryExecutorReturnsProducts(expectedProducts, "advanced search");

            var result = whenSearchingProducts(searchCriteria);

            thenResultShouldContainExpectedProducts(result, expectedProducts);
            thenQueryExecutorWasCalledWithOperation("advanced search");
        }

        @Test
        @DisplayName("Should return all products when criteria is empty")
        void searchProducts_withEmptyCriteria_shouldReturnAllProducts() {
            var emptyCriteria = testDataFactory.createEmptySearchCriteria();
            var allProducts = testDataFactory.createAllProducts();

            givenQueryExecutorReturnsProducts(allProducts, "advanced search");

            var result = whenSearchingProducts(emptyCriteria);

            thenResultShouldContainExpectedProducts(result, allProducts);
            thenQueryExecutorWasCalledWithOperation("advanced search");
        }

        @Test
        @DisplayName("Should return empty list when no products match criteria")
        void searchProducts_withNoMatches_shouldReturnEmptyList() {
            var searchCriteria = testDataFactory.createNoMatchSearchCriteria();
            var emptyList = testDataFactory.createEmptyProductList();

            givenQueryExecutorReturnsProducts(emptyList, "advanced search");

            var result = whenSearchingProducts(searchCriteria);

            thenResultShouldBeEmpty(result);
            thenQueryExecutorWasCalledWithOperation("advanced search");
        }

        private List<ProductResponseDTO> whenSearchingProducts(ProductSearchCriteria criteria) {
            return productSearchService.searchProducts(criteria);
        }
    }

    @Nested
    @DisplayName("Simple Search Tests")
    class SimpleSearchTests {

        @Test
        @DisplayName("Should find products by name")
        void findByName_withValidName_shouldReturnMatchingProducts() {
            var productName = "Laptop";
            var expectedProducts = testDataFactory.createLaptopProducts();
            var expectedOperation = "find by name: " + productName;

            givenQueryExecutorReturnsProducts(expectedProducts, expectedOperation);

            var result = whenFindingByName(productName);

            thenResultShouldContainExpectedProducts(result, expectedProducts);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        @Test
        @DisplayName("Should return empty list when name not found")
        void findByName_withNonExistentName_shouldReturnEmptyList() {
            var productName = "NonExistent";
            var emptyList = testDataFactory.createEmptyProductList();
            var expectedOperation = "find by name: " + productName;

            givenQueryExecutorReturnsProducts(emptyList, expectedOperation);

            var result = whenFindingByName(productName);

            thenResultShouldBeEmpty(result);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        @Test
        @DisplayName("Should find products by category")
        void findByCategory_withValidCategory_shouldReturnMatchingProducts() {
            var category = "Electronics";
            var expectedProducts = testDataFactory.createElectronicsProducts();
            var expectedOperation = "find by category: " + category;

            givenQueryExecutorReturnsProducts(expectedProducts, expectedOperation);

            var result = whenFindingByCategory(category);

            thenResultShouldContainExpectedProducts(result, expectedProducts);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        @Test
        @DisplayName("Should find products by keyword")
        void findByKeyword_withValidKeyword_shouldReturnMatchingProducts() {
            var keyword = "gaming";
            var expectedProducts = testDataFactory.createGamingProducts();
            var expectedOperation = "search by keyword: " + keyword;

            givenQueryExecutorReturnsProducts(expectedProducts, expectedOperation);

            var result = whenFindingByKeyword(keyword);

            thenResultShouldContainExpectedProducts(result, expectedProducts);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        @Test
        @DisplayName("Should find active products only")
        void findActiveProducts_shouldReturnOnlyActiveProducts() {
            var expectedProducts = testDataFactory.createActiveProducts();
            var expectedOperation = "find active products";

            givenQueryExecutorReturnsProducts(expectedProducts, expectedOperation);

            var result = whenFindingActiveProducts();

            thenResultShouldContainExpectedProducts(result, expectedProducts);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        private List<ProductResponseDTO> whenFindingByName(String name) {
            return productSearchService.findByName(name);
        }

        private List<ProductResponseDTO> whenFindingByCategory(String category) {
            return productSearchService.findByCategory(category);
        }

        private List<ProductResponseDTO> whenFindingByKeyword(String keyword) {
            return productSearchService.findByKeyword(keyword);
        }

        private List<ProductResponseDTO> whenFindingActiveProducts() {
            return productSearchService.findActiveProducts();
        }
    }

    @Nested
    @DisplayName("Price Range Search Tests")
    class PriceRangeSearchTests {

        @Test
        @DisplayName("Should find products within price range")
        void findByPriceRange_withBothMinAndMax_shouldReturnProductsInRange() {
            var minPrice = 100.0;
            var maxPrice = 1000.0;
            var expectedProducts = testDataFactory.createMidRangeProducts();
            var expectedOperation = String.format("find by price range: %s - %s", minPrice, maxPrice);

            givenQueryExecutorReturnsProducts(expectedProducts, expectedOperation);

            var result = whenFindingByPriceRange(minPrice, maxPrice);

            thenResultShouldContainExpectedProducts(result, expectedProducts);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        @Test
        @DisplayName("Should find products above minimum price")
        void findByPriceRange_withMinPriceOnly_shouldReturnProductsAboveMin() {
            var minPrice = 500.0;
            var maxPrice = (Double) null;
            var expectedProducts = testDataFactory.createExpensiveProducts();
            var expectedOperation = String.format("find by price range: %s - %s", minPrice, maxPrice);

            givenQueryExecutorReturnsProducts(expectedProducts, expectedOperation);

            var result = whenFindingByPriceRange(minPrice, maxPrice);

            thenResultShouldContainExpectedProducts(result, expectedProducts);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        @Test
        @DisplayName("Should find available products above price")
        void findAvailableProductsAbovePrice_shouldReturnMatchingProducts() {
            var minPrice = 500.0;
            var expectedProducts = testDataFactory.createExpensiveAvailableProducts();
            var expectedOperation = "find available products above price: " + minPrice;

            givenQueryExecutorReturnsProducts(expectedProducts, expectedOperation);

            var result = whenFindingAvailableProductsAbovePrice(minPrice);

            thenResultShouldContainExpectedProducts(result, expectedProducts);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        private List<ProductResponseDTO> whenFindingByPriceRange(Double minPrice, Double maxPrice) {
            return productSearchService.findByPriceRange(minPrice, maxPrice);
        }

        private List<ProductResponseDTO> whenFindingAvailableProductsAbovePrice(Double minPrice) {
            return productSearchService.findAvailableProductsAbovePrice(minPrice);
        }
    }

    @Nested
    @DisplayName("Status and Availability Search Tests")
    class StatusAndAvailabilitySearchTests {

        @Test
        @DisplayName("Should find products by status")
        void findByStatus_withAvailableStatus_shouldReturnAvailableProducts() {
            var status = ProductStatus.AVAILABLE;
            var expectedProducts = testDataFactory.createAvailableProducts();
            var expectedOperation = "find by status: " + status;

            givenQueryExecutorReturnsProducts(expectedProducts, expectedOperation);

            var result = whenFindingByStatus(status);

            thenResultShouldContainExpectedProducts(result, expectedProducts);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        @Test
        @DisplayName("Should find products by category and availability")
        void findByCategoryAndAvailability_shouldReturnMatchingProducts() {
            var category = "Electronics";
            var availability = true;
            var expectedProducts = testDataFactory.createAvailableElectronicsProducts();
            var expectedOperation = String.format("find by category: %s and availability: %s", category, availability);

            givenQueryExecutorReturnsProducts(expectedProducts, expectedOperation);

            var result = whenFindingByCategoryAndAvailability(category, availability);

            thenResultShouldContainExpectedProducts(result, expectedProducts);
            thenQueryExecutorWasCalledWithOperation(expectedOperation);
        }

        private List<ProductResponseDTO> whenFindingByStatus(ProductStatus status) {
            return productSearchService.findByStatus(status);
        }

        private List<ProductResponseDTO> whenFindingByCategoryAndAvailability(String category, Boolean availability) {
            return productSearchService.findByCategoryAndAvailability(category, availability);
        }
    }

    // Given methods
    private void givenQueryExecutorReturnsProducts(List<ProductResponseDTO> products, String operation) {
        when(productQueryExecutor.executeSpecificationQuery(any(Specification.class), eq(operation)))
                .thenReturn(products);
    }

    // Then methods
    private void thenResultShouldContainExpectedProducts(List<ProductResponseDTO> result, List<ProductResponseDTO> expected) {
        assertThat(result)
                .isNotNull()
                .hasSize(expected.size())
                .containsExactlyElementsOf(expected);
    }

    private void thenResultShouldBeEmpty(List<ProductResponseDTO> result) {
        assertThat(result)
                .isNotNull()
                .isEmpty();
    }

    private void thenQueryExecutorWasCalledWithOperation(String operation) {
        verify(productQueryExecutor).executeSpecificationQuery(any(Specification.class), eq(operation));
    }

    private static class TestDataFactory {

        ProductSearchCriteria createAdvancedSearchCriteria() {
            var criteria = ProductSearchCriteria.builder()
                    .category("Electronics")
                    .minPrice(500.0)
                    .availability(true)
                    .build();
            return criteria;
        }

        ProductSearchCriteria createEmptySearchCriteria() {
            return ProductSearchCriteria.builder().build();
        }

        ProductSearchCriteria createNoMatchSearchCriteria() {
            return ProductSearchCriteria.builder()
                    .name("NonExistentProduct")
                    .category("NonExistentCategory")
                    .build();
        }

        List<ProductResponseDTO> createElectronicsProducts() {
            return List.of(
                    createDTO(1L, "Laptop", "Electronics", "Gaming laptop", 1500.0, ProductStatus.AVAILABLE),
                    createDTO(2L, "Smartphone", "Electronics", "Flagship smartphone", 999.99, ProductStatus.AVAILABLE)
            );
        }

        List<ProductResponseDTO> createLaptopProducts() {
            return List.of(
                    createDTO(1L, "Laptop", "Electronics", "Gaming laptop", 1500.0, ProductStatus.AVAILABLE)
            );
        }

        List<ProductResponseDTO> createGamingProducts() {
            return List.of(
                    createDTO(1L, "Laptop", "Electronics", "Gaming laptop", 1500.0, ProductStatus.AVAILABLE)
            );
        }

        List<ProductResponseDTO> createActiveProducts() {
            return List.of(
                    createDTO(1L, "Laptop", "Electronics", "Gaming laptop", 1500.0, ProductStatus.AVAILABLE),
                    createDTO(4L, "Coffee Mug", "Kitchen", "Ceramic coffee mug", 15.50, ProductStatus.AVAILABLE)
            );
        }

        List<ProductResponseDTO> createAvailableProducts() {
            return List.of(
                    createDTO(1L, "Laptop", "Electronics", "Gaming laptop", 1500.0, ProductStatus.AVAILABLE),
                    createDTO(2L, "Smartphone", "Electronics", "Flagship smartphone", 999.99, ProductStatus.AVAILABLE),
                    createDTO(4L, "Coffee Mug", "Kitchen", "Ceramic coffee mug", 15.50, ProductStatus.AVAILABLE)
            );
        }

        List<ProductResponseDTO> createAvailableElectronicsProducts() {
            return List.of(
                    createDTO(1L, "Laptop", "Electronics", "Gaming laptop", 1500.0, ProductStatus.AVAILABLE),
                    createDTO(2L, "Smartphone", "Electronics", "Flagship smartphone", 999.99, ProductStatus.AVAILABLE)
            );
        }

        List<ProductResponseDTO> createMidRangeProducts() {
            return List.of(
                    createDTO(2L, "Smartphone", "Electronics", "Flagship smartphone", 999.99, ProductStatus.AVAILABLE),
                    createDTO(3L, "Desk Chair", "Furniture", "Ergonomic chair", 199.99, ProductStatus.NOT_AVAILABLE)
            );
        }

        List<ProductResponseDTO> createExpensiveProducts() {
            return List.of(
                    createDTO(1L, "Laptop", "Electronics", "Gaming laptop", 1500.0, ProductStatus.AVAILABLE),
                    createDTO(2L, "Smartphone", "Electronics", "Flagship smartphone", 999.99, ProductStatus.AVAILABLE)
            );
        }

        List<ProductResponseDTO> createExpensiveAvailableProducts() {
            return List.of(
                    createDTO(1L, "Laptop", "Electronics", "Gaming laptop", 1500.0, ProductStatus.AVAILABLE),
                    createDTO(2L, "Smartphone", "Electronics", "Flagship smartphone", 999.99, ProductStatus.AVAILABLE)
            );
        }

        List<ProductResponseDTO> createAllProducts() {
            return List.of(
                    createDTO(1L, "Laptop", "Electronics", "Gaming laptop", 1500.0, ProductStatus.AVAILABLE),
                    createDTO(2L, "Smartphone", "Electronics", "Flagship smartphone", 999.99, ProductStatus.AVAILABLE),
                    createDTO(3L, "Desk Chair", "Furniture", "Ergonomic chair", 199.99, ProductStatus.NOT_AVAILABLE),
                    createDTO(4L, "Coffee Mug", "Kitchen", "Ceramic coffee mug", 15.50, ProductStatus.AVAILABLE),
                    createDTO(5L, "Backpack", "Accessories", "Waterproof backpack", 59.99, ProductStatus.NOT_AVAILABLE)
            );
        }

        List<ProductResponseDTO> createEmptyProductList() {
            return List.of();
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
