package com.example.intelligent_inventory_prediction_system.repository.specification;

import com.example.intelligent_inventory_prediction_system.model.Product;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductSpecsTest {

    @Mock
    private Root<Product> root;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private Path<String> nameField;

    @Mock
    private Path<String> categoryField;

    @Mock
    private Path<String> descriptionField;

    @Mock
    private Path<ProductStatus> statusField;

    @Mock
    private Path<Double> priceField;

    @Mock
    private Path<Boolean> availabilityField;

    @Mock
    private Path<Boolean> activeField;

    @Mock
    private Expression<String> nameLowerExpression;

    @Mock
    private Expression<String> categoryLowerExpression;

    @Mock
    private Expression<String> descriptionLowerExpression;

    @Mock
    private Predicate predicate;

    @Mock
    private Predicate namePredicate;

    @Mock
    private Predicate categoryPredicate;

    @Mock
    private Predicate descriptionPredicate;

    @BeforeEach
    void setUp() {
        lenient().when(root.<String>get("name")).thenReturn(nameField);
        lenient().when(root.<String>get("category")).thenReturn(categoryField);
        lenient().when(root.<String>get("description")).thenReturn(descriptionField);
        lenient().when(root.<ProductStatus>get("status")).thenReturn(statusField);
        lenient().when(root.<Double>get("price")).thenReturn(priceField);
        lenient().when(root.<Boolean>get("availability")).thenReturn(availabilityField);
        lenient().when(root.<Boolean>get("active")).thenReturn(activeField);

        lenient().when(cb.lower(nameField)).thenReturn(nameLowerExpression);
        lenient().when(cb.lower(categoryField)).thenReturn(categoryLowerExpression);
        lenient().when(cb.lower(descriptionField)).thenReturn(descriptionLowerExpression);
    }

    @Test
    void testHasName_WithValidName_ShouldCreateLikeSpecification() {
        when(cb.like(nameLowerExpression, "%testproduct%")).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasName("TestProduct");
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(predicate, result);
        verify(cb).like(nameLowerExpression, "%testproduct%");
    }

    @Test
    void testHasName_WithEmptyName_ShouldReturnConjunction() {
        when(cb.conjunction()).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasName("");
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate, result);
        verify(cb).conjunction();
    }

    @Test
    void testHasName_WithNullName_ShouldReturnConjunction() {
        when(cb.conjunction()).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasName(null);
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate, result);
        verify(cb).conjunction();
    }

    @Test
    void testHasExactName_WithValidName_ShouldCreateEqualSpecification() {
        when(cb.equal(nameLowerExpression, "testproduct")).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasExactName("TestProduct");
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(predicate, result);
        verify(cb).equal(nameLowerExpression, "testproduct");
    }

    @Test
    void testHasExactName_WithNull_ShouldReturnConjunction() {
        when(cb.conjunction()).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasExactName(null);
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate, result);
        verify(cb).conjunction();
    }

    @Test
    void testHasPriceGreaterThan_WithValidPrice_ShouldCreateGreaterThanSpecification() {
        when(cb.greaterThan(priceField, 100.0)).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasPriceGreaterThan(100.0);
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(predicate, result);
        verify(cb).greaterThan(priceField, 100.0);
    }

    @Test
    void testHasPriceGreaterThan_WithNull_ShouldReturnConjunction() {
        when(cb.conjunction()).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasPriceGreaterThan(null);
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate, result);
        verify(cb).conjunction();
    }

    @Test
    void testHasPriceLessThan_WithValidPrice_ShouldCreateLessThanSpecification() {
        when(cb.lessThan(priceField, 500.0)).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasPriceLessThan(500.0);
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(predicate, result);
        verify(cb).lessThan(priceField, 500.0);
    }

    @Test
    void testHasPriceLessThan_WithNull_ShouldReturnConjunction() {
        when(cb.conjunction()).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasPriceLessThan(null);
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate, result);
        verify(cb).conjunction();
    }

    @Test
    void testHasPriceBetween_WithBothPrices_ShouldCreateBetweenSpecification() {
        when(cb.between(priceField, 100.0, 500.0)).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasPriceBetween(100.0, 500.0);
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(predicate, result);
        verify(cb).between(priceField, 100.0, 500.0);
    }

    @Test
    void testHasPriceBetween_WithOnlyMinPrice_ShouldCreateGreaterThanOrEqualSpecification() {
        when(cb.greaterThanOrEqualTo(priceField, 100.0)).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasPriceBetween(100.0, null);
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(predicate, result);
        verify(cb).greaterThanOrEqualTo(priceField, 100.0);
    }

    @Test
    void testHasPriceBetween_WithOnlyMaxPrice_ShouldCreateLessThanOrEqualSpecification() {
        when(cb.lessThanOrEqualTo(priceField, 500.0)).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasPriceBetween(null, 500.0);
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(predicate, result);
        verify(cb).lessThanOrEqualTo(priceField, 500.0);
    }

    @Test
    void testHasPriceBetween_WithBothNull_ShouldReturnConjunction() {
        when(cb.conjunction()).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasPriceBetween(null, null);
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate, result);
        verify(cb).conjunction();
    }

    @Test
    void testHasCategory_WithValidCategory_ShouldCreateEqualSpecification() {
        when(cb.equal(categoryLowerExpression, "electronics")).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasCategory("Electronics");
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(predicate, result);
        verify(cb).equal(categoryLowerExpression, "electronics");
    }

    @Test
    void testHasCategory_WithNull_ShouldReturnConjunction() {
        when(cb.conjunction()).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasCategory(null);
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate, result);
        verify(cb).conjunction();
    }

    @Test
    void testHasCategory_WithEmptyString_ShouldReturnConjunction() {
        when(cb.conjunction()).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasCategory("");
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate, result);
        verify(cb).conjunction();
    }

    @Test
    void testIsAvailable_ShouldCreateIsTrueSpecification() {
        when(cb.equal(statusField, ProductStatus.AVAILABLE)).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.isAvailable();
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(predicate, result);
        verify(cb).equal(statusField, ProductStatus.AVAILABLE);
    }

    @Test
    void testHasAvailability_WithTrue_ShouldCreateEqualSpecification() {
        when(cb.equal(statusField, ProductStatus.AVAILABLE)).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasAvailability(true);
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(predicate, result);
        verify(cb).equal(statusField, ProductStatus.AVAILABLE);
    }

    @Test
    void testHasAvailability_WithFalse_ShouldCreateEqualSpecification() {
        when(cb.equal(statusField, ProductStatus.NOT_AVAILABLE)).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasAvailability(false);
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(predicate, result);
        verify(cb).equal(statusField, ProductStatus.NOT_AVAILABLE);
    }

    @Test
    void testHasAvailability_WithNull_ShouldReturnConjunction() {
        when(cb.conjunction()).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasAvailability(null);
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate, result);
        verify(cb).conjunction();
    }

    @Test
    void testIsActive_ShouldCreateIsTrueSpecification() {
        when(cb.equal(statusField, ProductStatus.AVAILABLE)).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.isActive();
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(predicate, result);
        verify(cb).equal(statusField, ProductStatus.AVAILABLE);
    }

    @Test
    void testHasStatus_WithValidStatus_ShouldCreateEqualSpecification() {
        when(cb.equal(statusField, ProductStatus.AVAILABLE)).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasStatus(ProductStatus.AVAILABLE);
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(predicate, result);
        verify(cb).equal(statusField, ProductStatus.AVAILABLE);
    }

    @Test
    void testHasStatus_WithNull_ShouldReturnConjunction() {
        when(cb.conjunction()).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasStatus((ProductStatus) null);
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate, result);
        verify(cb).conjunction();
    }

    @Test
    void testHasStatus_WithEmptyString_ShouldReturnConjunction() {
        when(cb.conjunction()).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.hasStatus("");
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate, result);
        verify(cb).conjunction();
    }

    @Test
    void testSearchByKeyword_WithValidKeyword_ShouldCreateOrSpecification() {
        when(cb.like(nameLowerExpression, "%laptop%")).thenReturn(namePredicate);
        when(cb.like(descriptionLowerExpression, "%laptop%")).thenReturn(descriptionPredicate);
        when(cb.like(categoryLowerExpression, "%laptop%")).thenReturn(categoryPredicate);
        when(cb.or(namePredicate, descriptionPredicate, categoryPredicate)).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.searchByKeyword("laptop");
        Predicate result = spec.toPredicate(root, query, cb);

        assertEquals(predicate, result);
        verify(cb).or(namePredicate, descriptionPredicate, categoryPredicate);
    }

    @Test
    void testSearchByKeyword_WithNull_ShouldReturnConjunction() {
        when(cb.conjunction()).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.searchByKeyword(null);
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate, result);
        verify(cb).conjunction();
    }

    @Test
    void testSearchByKeyword_WithEmptyString_ShouldReturnConjunction() {
        when(cb.conjunction()).thenReturn(predicate);

        Specification<Product> spec = ProductSpecs.searchByKeyword("");
        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate, result);
        verify(cb).conjunction();
    }
}
