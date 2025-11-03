package com.example.intelligent_inventory_prediction_system.repository.specification;

import com.example.intelligent_inventory_prediction_system.model.Inventory;
import com.example.intelligent_inventory_prediction_system.model.Product;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventorySpecs Unit Tests")
class InventorySpecsTest {

    @Mock
    private Root<Inventory> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Join<Inventory, Product> productJoin;

    @Mock
    private Path<Long> idPath;

    @Mock
    private Path<Integer> stockPath;

    @Mock
    private Predicate predicate;

    @BeforeEach
    void setUp() {
        lenient().when(root.<Integer>get("currentStock")).thenReturn(stockPath);
        lenient().when(root.<Inventory, Product>join("product")).thenReturn(productJoin);
        lenient().when(productJoin.<Long>get("id")).thenReturn(idPath);
    }

    @Test
    @DisplayName("hasProductId with valid ID should create equal predicate")
    void hasProductId_WithValidId_ShouldCreateEqualPredicate() {
        // Arrange
        when(criteriaBuilder.equal(idPath, 1L)).thenReturn(predicate);

        // Act
        Specification<Inventory> spec = InventorySpecs.hasProductId(1L);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Assert
        assertThat(result).isNotNull();
        verify(root).<Inventory, Product>join("product");
        verify(criteriaBuilder).equal(idPath, 1L);
    }

    @Test
    @DisplayName("hasProductId with null should return conjunction")
    void hasProductId_WithNull_ShouldReturnConjunction() {
        // Arrange
        when(criteriaBuilder.conjunction()).thenReturn(predicate);

        // Act
        Specification<Inventory> spec = InventorySpecs.hasProductId(null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Assert
        assertThat(result).isNotNull();
        verify(criteriaBuilder).conjunction();
        verify(root, never()).join(anyString());
    }

    @Test
    @DisplayName("hasStockBetween with both values should create between predicate")
    void hasStockBetween_WithBothValues_ShouldCreateBetweenPredicate() {
        // Arrange
        when(criteriaBuilder.between(stockPath, 10, 100)).thenReturn(predicate);

        // Act
        Specification<Inventory> spec = InventorySpecs.hasStockBetween(10, 100);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Assert
        assertThat(result).isNotNull();
        verify(criteriaBuilder).between(stockPath, 10, 100);
    }

    @Test
    @DisplayName("hasStockBetween with min only should create greaterThanOrEqualTo predicate")
    void hasStockBetween_WithMinOnly_ShouldCreateGreaterThanOrEqualTo() {
        // Arrange
        when(criteriaBuilder.greaterThanOrEqualTo(stockPath, 50)).thenReturn(predicate);

        // Act
        Specification<Inventory> spec = InventorySpecs.hasStockBetween(50, null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Assert
        assertThat(result).isNotNull();
        verify(criteriaBuilder).greaterThanOrEqualTo(stockPath, 50);
    }

    @Test
    @DisplayName("hasStockBetween with max only should create lessThanOrEqualTo predicate")
    void hasStockBetween_WithMaxOnly_ShouldCreateLessThanOrEqualTo() {
        // Arrange
        when(criteriaBuilder.lessThanOrEqualTo(stockPath, 200)).thenReturn(predicate);

        // Act
        Specification<Inventory> spec = InventorySpecs.hasStockBetween(null, 200);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Assert
        assertThat(result).isNotNull();
        verify(criteriaBuilder).lessThanOrEqualTo(stockPath, 200);
    }

    @Test
    @DisplayName("hasStockBetween with both null should return conjunction")
    void hasStockBetween_WithBothNull_ShouldReturnConjunction() {
        // Arrange
        when(criteriaBuilder.conjunction()).thenReturn(predicate);

        // Act
        Specification<Inventory> spec = InventorySpecs.hasStockBetween(null, null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Assert
        assertThat(result).isNotNull();
        verify(criteriaBuilder).conjunction();
    }

    @Test
    @DisplayName("hasStockGreaterThan with valid value should create greaterThan predicate")
    void hasStockGreaterThan_WithValidValue_ShouldCreateGreaterThanPredicate() {
        // Arrange
        when(criteriaBuilder.greaterThan(stockPath, 30)).thenReturn(predicate);

        // Act
        Specification<Inventory> spec = InventorySpecs.hasStockGreaterThan(30);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Assert
        assertThat(result).isNotNull();
        verify(criteriaBuilder).greaterThan(stockPath, 30);
    }

    @Test
    @DisplayName("hasStockGreaterThan with null should return conjunction")
    void hasStockGreaterThan_WithNull_ShouldReturnConjunction() {
        // Arrange
        when(criteriaBuilder.conjunction()).thenReturn(predicate);

        // Act
        Specification<Inventory> spec = InventorySpecs.hasStockGreaterThan(null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Assert
        assertThat(result).isNotNull();
        verify(criteriaBuilder).conjunction();
    }
}
