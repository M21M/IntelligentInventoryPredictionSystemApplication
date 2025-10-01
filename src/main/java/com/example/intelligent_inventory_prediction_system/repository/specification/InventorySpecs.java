package com.example.intelligent_inventory_prediction_system.repository.specification;

import com.example.intelligent_inventory_prediction_system.model.Inventory;
import com.example.intelligent_inventory_prediction_system.model.Product;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class InventorySpecs {

    public static Specification<Inventory> hasProductId(Long productId) {
        return (root, query, cb) -> {
            if (productId == null) {
                return cb.conjunction();
            }
            Join<Inventory, Product> productJoin = root.join("product");
            return cb.equal(productJoin.get("id"), productId);
        };
    }

    public static Specification<Inventory> hasStockBetween(Integer minStock, Integer maxStock) {
        return (root, query, cb) -> {
            if (minStock == null && maxStock == null) {
                return cb.conjunction();
            }
            if (minStock != null && maxStock != null) {
                return cb.between(root.get("currentStock"), minStock, maxStock);
            }
            if (minStock != null) {
                return cb.greaterThanOrEqualTo(root.get("currentStock"), minStock);
            }
            return cb.lessThanOrEqualTo(root.get("currentStock"), maxStock);
        };
    }

    public static Specification<Inventory> hasStockGreaterThan(Integer minStock) {
        return (root, query, cb) -> {
            if (minStock == null) {
                return cb.conjunction();
            }
            return cb.greaterThan(root.get("currentStock"), minStock);
        };
    }
}
