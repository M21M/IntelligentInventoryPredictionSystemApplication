package com.example.intelligent_inventory_prediction_system.service;

import com.example.intelligent_inventory_prediction_system.dto.response.ProductResponseDTO;
import com.example.intelligent_inventory_prediction_system.dto.search.ProductSearchCriteria;
import com.example.intelligent_inventory_prediction_system.model.Product;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import com.example.intelligent_inventory_prediction_system.repository.specification.ProductSpecs;
import com.example.intelligent_inventory_prediction_system.service.executor.ProductQueryExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductSearchService {

    private final ProductQueryExecutor queryExecutor;

    public List<ProductResponseDTO> searchProducts(ProductSearchCriteria criteria) {
        log.debug("Searching products with criteria: {}", criteria);
        Specification<Product> specification = buildSearchSpecification(criteria);
        return queryExecutor.executeSpecificationQuery(specification, "advanced search");
    }

    public List<ProductResponseDTO> findByName(String name) {
        Specification<Product> specification = ProductSpecs.hasName(name);
        return queryExecutor.executeSpecificationQuery(specification, "find by name: " + name);
    }

    public List<ProductResponseDTO> findByCategory(String category) {
        Specification<Product> specification = ProductSpecs.hasCategory(category);
        return queryExecutor.executeSpecificationQuery(specification, "find by category: " + category);
    }

    public List<ProductResponseDTO> findByKeyword(String keyword) {
        Specification<Product> specification = ProductSpecs.searchByKeyword(keyword);
        return queryExecutor.executeSpecificationQuery(specification, "search by keyword: " + keyword);
    }

    public List<ProductResponseDTO> findActiveProducts() {
        Specification<Product> specification = ProductSpecs.isActiveAndAvailable();
        return queryExecutor.executeSpecificationQuery(specification, "find active products");
    }

    public List<ProductResponseDTO> findByPriceRange(Double minPrice, Double maxPrice) {
        Specification<Product> specification = ProductSpecs.hasPriceBetween(minPrice, maxPrice);
        return queryExecutor.executeSpecificationQuery(specification,
                String.format("find by price range: %s - %s", minPrice, maxPrice));
    }

    public List<ProductResponseDTO> findByCategoryAndAvailability(String category, Boolean availability) {
        Specification<Product> specification = ProductSpecs.hasCategory(category)
                .and(ProductSpecs.hasAvailability(availability));
        return queryExecutor.executeSpecificationQuery(specification,
                String.format("find by category: %s and availability: %s", category, availability));
    }

    public List<ProductResponseDTO> findAvailableProductsAbovePrice(Double minPrice) {
        Specification<Product> specification = ProductSpecs.isAvailable()
                .and(ProductSpecs.hasPriceGreaterThan(minPrice));
        return queryExecutor.executeSpecificationQuery(specification,
                "find available products above price: " + minPrice);
    }

    public List<ProductResponseDTO> findByStatus(ProductStatus status) {
        Specification<Product> specification = ProductSpecs.hasStatus(status.name());
        return queryExecutor.executeSpecificationQuery(specification, "find by status: " + status);
    }

    private Specification<Product> buildSearchSpecification(ProductSearchCriteria criteria) {
        List<Specification<Product>> specifications = new ArrayList<>();

        addKeywordFilter(specifications, criteria.getKeyword());
        addNameFilter(specifications, criteria.getName());
        addCategoryFilter(specifications, criteria.getCategory());
        addStatusFilter(specifications, criteria.getStatus());
        addPriceRangeFilter(specifications, criteria.getMinPrice(), criteria.getMaxPrice());
        addAvailabilityFilter(specifications, criteria.getAvailability());
        addActiveFilter(specifications, criteria.getActive());

        return specifications.stream()
                .reduce(Specification.allOf(), Specification::and);
    }

    private void addKeywordFilter(List<Specification<Product>> specs, String keyword) {
        if (StringUtils.hasText(keyword)) {
            specs.add(ProductSpecs.searchByKeyword(keyword));
        }
    }

    private void addNameFilter(List<Specification<Product>> specs, String name) {
        if (StringUtils.hasText(name)) {
            specs.add(ProductSpecs.hasName(name));
        }
    }

    private void addCategoryFilter(List<Specification<Product>> specs, String category) {
        if (StringUtils.hasText(category)) {
            specs.add(ProductSpecs.hasCategory(category));
        }
    }

    private void addStatusFilter(List<Specification<Product>> specs, ProductStatus status) {
        if (status != null) {
            specs.add(ProductSpecs.hasStatus(status.name()));
        }
    }

    private void addPriceRangeFilter(List<Specification<Product>> specs, Double minPrice, Double maxPrice) {
        if (minPrice != null || maxPrice != null) {
            specs.add(ProductSpecs.hasPriceBetween(minPrice, maxPrice));
        }
    }

    private void addAvailabilityFilter(List<Specification<Product>> specs, Boolean availability) {
        if (availability != null) {
            specs.add(ProductSpecs.hasAvailability(availability));
        }
    }

    private void addActiveFilter(List<Specification<Product>> specs, Boolean active) {
        if (Boolean.TRUE.equals(active)) {
            specs.add(ProductSpecs.isActive());
        }
    }
}
