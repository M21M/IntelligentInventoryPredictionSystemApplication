package com.example.intelligent_inventory_prediction_system.service.executor;

import com.example.intelligent_inventory_prediction_system.dto.response.ProductResponseDTO;
import com.example.intelligent_inventory_prediction_system.mapper.response.ProductResponseMapper;
import com.example.intelligent_inventory_prediction_system.model.Product;
import com.example.intelligent_inventory_prediction_system.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductQueryExecutor {

    private final ProductRepository productRepository;
    private final ProductResponseMapper productResponseMapper;

    public List<ProductResponseDTO> executeSpecificationQuery(Specification<Product> specification, String operationDescription) {
        log.debug("Executing query: {}", operationDescription);
        List<Product> products = productRepository.findAll(specification);
        log.debug("Found {} products for operation: {}", products.size(), operationDescription);
        return productResponseMapper.toProductResponseDTOList(products);
    }

    public Page<ProductResponseDTO> executePagedQuery(Pageable pageable, String operationDescription) {
        log.debug("Executing paged query: {} with pagination: {}", operationDescription, pageable);
        Page<Product> products = productRepository.findAll(pageable);
        log.debug("Found {} products for paged operation: {}", products.getTotalElements(), operationDescription);
        return products.map(productResponseMapper::toProductResponseDTO);
    }

    public List<ProductResponseDTO> executeSimpleQuery(String operationDescription) {
        log.debug("Executing simple query: {}", operationDescription);
        List<Product> products = productRepository.findAll();
        log.debug("Found {} products for operation: {}", products.size(), operationDescription);
        return productResponseMapper.toProductResponseDTOList(products);
    }
}
