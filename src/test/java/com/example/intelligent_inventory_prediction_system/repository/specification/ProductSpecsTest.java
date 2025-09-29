package com.example.intelligent_inventory_prediction_system.repository.specification;

import com.example.intelligent_inventory_prediction_system.model.Product;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;


import static com.example.intelligent_inventory_prediction_system.repository.specification.ProductSpecs.hasName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class ProductSpecsTest {
    @Mock
    private Root<Product> root;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Path<String> pathName;

    @Mock
    private  Expression<String> lowerExpression;

    @Mock
    private Predicate predicate;

    @Mock
    private CriteriaQuery<?> query;



    @Test
    void testHasName(){

        when(root.<String>get("name")).thenReturn((Path<String>) pathName);
        when(cb.lower(pathName)).thenReturn(lowerExpression);
        when(cb.like(lowerExpression, "%test%")).thenReturn(predicate);


        Specification<Product> specification = hasName("test");
        Predicate result = specification.toPredicate(root, query, cb);


        assertEquals(predicate, result);


        verify(cb).lower(pathName);
        verify(cb).like(lowerExpression, "%test%");
    }



    @Test
    void testWhenEmptyName(){

        when(cb.conjunction()).thenReturn(predicate);

        Specification<Product> specification = hasName("");
        Predicate result = specification.toPredicate(root, query, cb);

        assertSame(predicate, result);
        verify(cb).conjunction();
    }

    @Test
    void testWhenNullName(){
        when(cb.conjunction()).thenReturn(predicate);

        Specification<Product> specification = hasName(null);
        Predicate result = specification.toPredicate(root, query, cb);

        assertSame(predicate, result);
        verify(cb).conjunction();
    }
    

}