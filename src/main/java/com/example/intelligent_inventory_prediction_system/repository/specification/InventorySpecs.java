package com.example.intelligent_inventory_prediction_system.repository.specification;

import com.example.intelligent_inventory_prediction_system.model.Inventory;
import com.example.intelligent_inventory_prediction_system.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class InventorySpecs {

    public static Specification<Inventory> hasProduct(Product product) {
        return (root, query, builder) ->
                product == null ? null : builder.equal(root.get("product"), product);
    }

    public static Specification<Inventory> hasProductId(Long productId) {
        return (root, query, builder) ->
                productId == null ? null : builder.equal(root.get("product").get("id"), productId);
    }

    public static Specification<Inventory> hasStockLevel(Integer stockLevel) {
        return (root, query, builder) ->
                stockLevel == null ? null : builder.equal(root.get("currentStock"), stockLevel);
    }

    public static Specification<Inventory> hasStockGreaterThan(Integer threshold) {
        return (root, query, builder) ->
                threshold == null ? null : builder.greaterThan(root.get("currentStock"), threshold);
    }

    public static Specification<Inventory> hasStockLessThan(Integer threshold) {
        return (root, query, builder) ->
                threshold == null ? null : builder.lessThan(root.get("currentStock"), threshold);
    }

    public static Specification<Inventory> hasStockBetween(Integer min, Integer max) {
        return (root, query, builder) -> {
            if (min == null && max == null) return null;
            if (min == null) return builder.lessThanOrEqualTo(root.get("currentStock"), max);
            if (max == null) return builder.greaterThanOrEqualTo(root.get("currentStock"), min);
            return builder.between(root.get("currentStock"), min, max);
        };
    }

    public static Specification<Inventory> hasLowStock(Integer threshold) {
        return (root, query, builder) ->
                threshold == null ? null : builder.lessThanOrEqualTo(root.get("currentStock"), threshold);
    }

    public static Specification<Inventory> updatedAfter(LocalDateTime date) {
        return (root, query, builder) ->
                date == null ? null : builder.greaterThanOrEqualTo(root.get("lastUpdated"), date);
    }

    public static Specification<Inventory> updatedBefore(LocalDateTime date) {
        return (root, query, builder) ->
                date == null ? null : builder.lessThanOrEqualTo(root.get("lastUpdated"), date);
    }

    public static Specification<Inventory> updatedBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, builder) -> {
            if (start == null && end == null) return null;
            if (start == null) return builder.lessThanOrEqualTo(root.get("lastUpdated"), end);
            if (end == null) return builder.greaterThanOrEqualTo(root.get("lastUpdated"), start);
            return builder.between(root.get("lastUpdated"), start, end);
        };
    }

    public static Specification<Inventory> hasZeroStock() {
        return (root, query, builder) ->
                builder.equal(root.get("currentStock"), 0);
    }

    public static Specification<Inventory> hasPositiveStock() {
        return (root, query, builder) ->
                builder.greaterThan(root.get("currentStock"), 0);
    }
}
