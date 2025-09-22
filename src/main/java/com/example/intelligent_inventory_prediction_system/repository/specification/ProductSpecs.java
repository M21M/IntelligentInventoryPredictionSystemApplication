package com.example.intelligent_inventory_prediction_system.repository.specification;

import com.example.intelligent_inventory_prediction_system.model.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class ProductSpecs {

    private ProductSpecs() {
        // Utility class - prevent instantiation
    }

    public static Specification<Product> hasName(String productName) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(productName)) {
                return criteriaBuilder.conjunction(); // Returns true (no filter)
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + productName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Product> hasExactName(String productName) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(productName)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("name")),
                    productName.toLowerCase()
            );
        };
    }

    public static Specification<Product> hasPriceGreaterThan(Double minPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThan(root.get("price"), minPrice);
        };
    }

    public static Specification<Product> hasPriceLessThan(Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (maxPrice == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThan(root.get("price"), maxPrice);
        };
    }

    public static Specification<Product> hasPriceBetween(Double minPrice, Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return criteriaBuilder.conjunction();
            }
            if (minPrice == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
            if (maxPrice == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            }
            return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
        };
    }

    public static Specification<Product> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(category)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("category")),
                    category.toLowerCase()
            );
        };
    }

    public static Specification<Product> isAvailable() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("availability"));
    }

    public static Specification<Product> hasAvailability(Boolean availability) {
        return (root, query, criteriaBuilder) -> {
            if (availability == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("availability"), availability);
        };
    }

    public static Specification<Product> isActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("active"));
    }

    public static Specification<Product> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(status)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    // Combined specifications for common use cases
    public static Specification<Product> isActiveAndAvailable() {
        return isActive().and(isAvailable());
    }

    public static Specification<Product> searchByKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(keyword)) {
                return criteriaBuilder.conjunction();
            }

            String likePattern = "%" + keyword.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("category")), likePattern)
            );
        };
    }
}
