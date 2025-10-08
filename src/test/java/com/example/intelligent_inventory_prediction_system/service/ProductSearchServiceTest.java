package com.example.intelligent_inventory_prediction_system.service;

import com.example.intelligent_inventory_prediction_system.dto.response.ProductResponseDTO;
import com.example.intelligent_inventory_prediction_system.dto.search.ProductSearchCriteria;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import com.example.intelligent_inventory_prediction_system.service.executor.ProductQueryExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductSearchService Tests")
class ProductSearchServiceTest {

    @Mock
    private ProductQueryExecutor productQueryExecutor;

    @InjectMocks
    private ProductSearchService productSearchService;

    private List<ProductResponseDTO> sampleProducts;

    @BeforeEach
    void setUp() {
        sampleProducts = createSampleProducts();
    }

    @Test
    @DisplayName("Should search products with multiple criteria")
    void testSearchProductsWithCriteria() {
        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                .category("Electronics")
                .minPrice(100.0)
                .maxPrice(2000.0)
                .availability(true)
                .build();

        when(productQueryExecutor.executeSpecificationQuery(any(Specification.class), anyString()))
                .thenReturn(sampleProducts);

        List<ProductResponseDTO> result = productSearchService.searchProducts(criteria);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(productQueryExecutor).executeSpecificationQuery(any(Specification.class), anyString());
    }

    @Test
    @DisplayName("Should return all products with empty criteria")
    void testSearchProductsWithEmptyCriteria() {
        ProductSearchCriteria emptyCriteria = ProductSearchCriteria.builder().build();

        when(productQueryExecutor.executeSpecificationQuery(any(Specification.class), anyString()))
                .thenReturn(sampleProducts);

        List<ProductResponseDTO> result = productSearchService.searchProducts(emptyCriteria);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("Should return empty list when no matches found")
    void testSearchProductsWithNoMatches() {
        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                .name("NonExistent")
                .build();

        when(productQueryExecutor.executeSpecificationQuery(any(Specification.class), anyString()))
                .thenReturn(new ArrayList<>());

        List<ProductResponseDTO> result = productSearchService.searchProducts(criteria);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find products by name")
    void testFindByName() {
        String productName = "Laptop";
        when(productQueryExecutor.executeSpecificationQuery(any(Specification.class), anyString()))
                .thenReturn(sampleProducts);

        List<ProductResponseDTO> result = productSearchService.findByName(productName);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        verify(productQueryExecutor).executeSpecificationQuery(any(Specification.class), anyString());
    }

    @Test
    @DisplayName("Should find products by category")
    void testFindByCategory() {
        String category = "Electronics";
        when(productQueryExecutor.executeSpecificationQuery(any(Specification.class), anyString()))
                .thenReturn(sampleProducts);

        List<ProductResponseDTO> result = productSearchService.findByCategory(category);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should find products by keyword")
    void testFindByKeyword() {
        String keyword = "gaming";
        when(productQueryExecutor.executeSpecificationQuery(any(Specification.class), anyString()))
                .thenReturn(List.of(sampleProducts.get(0)));

        List<ProductResponseDTO> result = productSearchService.findByKeyword(keyword);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should find only active products")
    void testFindActiveProducts() {
        when(productQueryExecutor.executeSpecificationQuery(any(Specification.class), anyString()))
                .thenReturn(sampleProducts);

        List<ProductResponseDTO> result = productSearchService.findActiveProducts();

        assertThat(result).isNotNull();
        assertThat(result).allMatch(p -> p.getStatus() == ProductStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Should find products within price range")
    void testFindByPriceRange() {
        Double minPrice = 100.0;
        Double maxPrice = 1500.0;
        when(productQueryExecutor.executeSpecificationQuery(any(Specification.class), anyString()))
                .thenReturn(sampleProducts);

        List<ProductResponseDTO> result = productSearchService.findByPriceRange(minPrice, maxPrice);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("Should find products above minimum price only")
    void testFindByMinPriceOnly() {
        Double minPrice = 500.0;
        when(productQueryExecutor.executeSpecificationQuery(any(Specification.class), anyString()))
                .thenReturn(List.of(sampleProducts.get(0)));

        List<ProductResponseDTO> result = productSearchService.findByPriceRange(minPrice, null);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should find available products above price")
    void testFindAvailableProductsAbovePrice() {
        Double minPrice = 500.0;
        when(productQueryExecutor.executeSpecificationQuery(any(Specification.class), anyString()))
                .thenReturn(List.of(sampleProducts.get(0)));

        List<ProductResponseDTO> result = productSearchService.findAvailableProductsAbovePrice(minPrice);

        assertThat(result).isNotNull();
        assertThat(result).allMatch(p -> p.getPrice() > minPrice);
    }

    @Test
    @DisplayName("Should find products by status")
    void testFindByStatus() {
        ProductStatus status = ProductStatus.AVAILABLE;
        when(productQueryExecutor.executeSpecificationQuery(any(Specification.class), anyString()))
                .thenReturn(sampleProducts);

        List<ProductResponseDTO> result = productSearchService.findByStatus(status);

        assertThat(result).isNotNull();
        assertThat(result).allMatch(p -> p.getStatus() == ProductStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Should find products by category and availability")
    void testFindByCategoryAndAvailability() {
        String category = "Electronics";
        Boolean availability = true;
        when(productQueryExecutor.executeSpecificationQuery(any(Specification.class), anyString()))
                .thenReturn(sampleProducts);

        List<ProductResponseDTO> result = productSearchService.findByCategoryAndAvailability(category, availability);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        verify(productQueryExecutor).executeSpecificationQuery(any(Specification.class), anyString());
    }

    private List<ProductResponseDTO> createSampleProducts() {
        ProductResponseDTO laptop = new ProductResponseDTO();
        laptop.setId(1L);
        laptop.setName("Laptop");
        laptop.setCategory("Electronics");
        laptop.setDescription("Gaming laptop");
        laptop.setPrice(1500.0);
        laptop.setStatus(ProductStatus.AVAILABLE);

        ProductResponseDTO smartphone = new ProductResponseDTO();
        smartphone.setId(2L);
        smartphone.setName("Smartphone");
        smartphone.setCategory("Electronics");
        smartphone.setDescription("Flagship smartphone");
        smartphone.setPrice(999.99);
        smartphone.setStatus(ProductStatus.AVAILABLE);

        return List.of(laptop, smartphone);
    }
}
