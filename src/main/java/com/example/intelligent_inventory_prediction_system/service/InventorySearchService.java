package com.example.intelligent_inventory_prediction_system.service;

import com.example.intelligent_inventory_prediction_system.dto.response.InventoryResponseDTO;
import com.example.intelligent_inventory_prediction_system.dto.search.InventorySearchCriteria;
import com.example.intelligent_inventory_prediction_system.model.Inventory;
import com.example.intelligent_inventory_prediction_system.repository.specification.InventorySpecs;
import com.example.intelligent_inventory_prediction_system.service.executor.InventoryQueryExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventorySearchService {

    private final InventoryQueryExecutor queryExecutor;

    public List<InventoryResponseDTO> searchInventories(InventorySearchCriteria criteria) {
        log.debug("Searching inventories with criteria: {}", criteria);
        Specification<Inventory> specification = buildSearchSpecification(criteria);
        return queryExecutor.executeSpecificationQuery(specification, "advanced search");
    }

    public List<InventoryResponseDTO> findByProductId(Long productId) {
        Specification<Inventory> specification = InventorySpecs.hasProductId(productId);
        return queryExecutor.executeSpecificationQuery(specification, "find by product id: " + productId);
    }

    public List<InventoryResponseDTO> findByStockRange(Integer minStock, Integer maxStock) {
        Specification<Inventory> specification = InventorySpecs.hasStockBetween(minStock, maxStock);
        return queryExecutor.executeSpecificationQuery(specification,
                String.format("find by stock range: %d - %d", minStock, maxStock));
    }

    public List<InventoryResponseDTO> findByMinimumStock(Integer minStock) {
        Specification<Inventory> specification = InventorySpecs.hasStockGreaterThan(minStock);
        return queryExecutor.executeSpecificationQuery(specification,
                "find inventories with minimum stock: " + minStock);
    }

    private Specification<Inventory> buildSearchSpecification(InventorySearchCriteria criteria) {
        List<Specification<Inventory>> specifications = new ArrayList<>();

        addProductIdFilter(specifications, criteria.getProductId());
        addStockRangeFilter(specifications, criteria.getMinStock(), criteria.getMaxStock());

        return specifications.stream()
                .reduce(Specification.allOf(), Specification::and);
    }

    private void addProductIdFilter(List<Specification<Inventory>> specs, Long productId) {
        if (productId != null) {
            specs.add(InventorySpecs.hasProductId(productId));
        }
    }

    private void addStockRangeFilter(List<Specification<Inventory>> specs, Integer minStock, Integer maxStock) {
        if (minStock != null || maxStock != null) {
            specs.add(InventorySpecs.hasStockBetween(minStock, maxStock));
        }
    }
}
