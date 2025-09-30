package com.example.intelligent_inventory_prediction_system.service;

import com.example.intelligent_inventory_prediction_system.dto.request.ProductRequestDTO;
import com.example.intelligent_inventory_prediction_system.dto.response.ProductResponseDTO;
import com.example.intelligent_inventory_prediction_system.mapper.request.ProductRequestMapper;
import com.example.intelligent_inventory_prediction_system.mapper.response.ProductResponseMapper;
import com.example.intelligent_inventory_prediction_system.model.Product;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import com.example.intelligent_inventory_prediction_system.repository.ProductRepository;
import com.example.intelligent_inventory_prediction_system.service.executor.ProductQueryExecutor;
import com.example.intelligent_inventory_prediction_system.validator.ProductValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.intelligent_inventory_prediction_system.constants.ProductConstants.PRODUCT_NOT_FOUND_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductRequestMapper productRequestMapper;
    private final ProductResponseMapper productResponseMapper;
    private final ProductValidator productValidator;
    private final ProductQueryExecutor queryExecutor;

    public List<ProductResponseDTO> findAllProducts() {
        return (List<ProductResponseDTO>) queryExecutor.executeSimpleQuery("find all products");
    }

    public Page<ProductResponseDTO> findAllProducts(Pageable pageable) {
        return (Page<ProductResponseDTO>) queryExecutor.executePagedQuery(pageable, "find all products with pagination");
    }

    public ProductResponseDTO findProductById(Long id) {
        log.debug("Finding product by id: {}", id);
        productValidator.validateId(id);
        Product product = getProductByIdOrThrow(id);
        return productResponseMapper.toProductResponseDTO(product);
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        log.debug("Creating new product: {}", productRequestDTO);
        productValidator.validateCreateRequest(productRequestDTO);

        Product product = productRequestMapper.toProduct(productRequestDTO);
        Product savedProduct = saveProduct(product);

        log.info("Product created successfully with id: {}", savedProduct.getId());
        return productResponseMapper.toProductResponseDTO(savedProduct);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        log.debug("Updating product with id: {}", id);
        productValidator.validateId(id);
        productValidator.validateUpdateRequest(productRequestDTO);

        Product existingProduct = getProductByIdOrThrow(id);
        Product updatedProduct = productRequestMapper.updateProduct(productRequestDTO, existingProduct);
        Product savedProduct = saveProduct(updatedProduct);

        log.info("Product updated successfully with id: {}", id);
        return productResponseMapper.toProductResponseDTO(savedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.debug("Deleting product with id: {}", id);
        productValidator.validateId(id);
        Product product = getProductByIdOrThrow(id);

        productRepository.deleteById(id);
        log.info("Product deleted successfully with id: {}", id);
    }

    @Transactional
    public ProductResponseDTO updateProductStatus(Long id, ProductStatus status) {
        log.debug("Updating product status for id: {} to status: {}", id, status);
        productValidator.validateId(id);
        productValidator.validateStatus(status);

        Product product = getProductByIdOrThrow(id);
        product.setStatus(status);
        Product savedProduct = saveProduct(product);

        log.info("Product status updated successfully for id: {}", id);
        return productResponseMapper.toProductResponseDTO(savedProduct);
    }

    private Product getProductByIdOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(PRODUCT_NOT_FOUND_MESSAGE + id));
    }

    private Product saveProduct(Product product) {
        return productRepository.save(product);
    }
}
