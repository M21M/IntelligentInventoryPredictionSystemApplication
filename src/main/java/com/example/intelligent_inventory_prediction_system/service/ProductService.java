package com.example.intelligent_inventory_prediction_system.service;

import com.example.intelligent_inventory_prediction_system.dto.request.ProductRequestDTO;
import com.example.intelligent_inventory_prediction_system.dto.response.ProductResponseDTO;
import com.example.intelligent_inventory_prediction_system.mapper.request.ProductRequestMapper;
import com.example.intelligent_inventory_prediction_system.mapper.response.ProductResponseMapper;
import com.example.intelligent_inventory_prediction_system.model.Product;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import com.example.intelligent_inventory_prediction_system.repository.ProductRepository;
import com.example.intelligent_inventory_prediction_system.repository.specification.ProductSpecs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductRequestMapper productRequestMapper;
    private final ProductResponseMapper productResponseMapper;

    public List<ProductResponseDTO> findAllProducts() {
        log.debug("Finding all products");
        List<Product> products = productRepository.findAll();
        return productResponseMapper.toProductResponseDTOList(products);
    }

    public Page<ProductResponseDTO> findAllProducts(Pageable pageable) {
        log.debug("Finding all products with pagination: {}", pageable);
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(productResponseMapper::toProductResponseDTO);
    }

    public ProductResponseDTO findProductById(Long id) {
        log.debug("Finding product by id: {}", id);
        Product product = getProductByIdOrThrow(id);
        return productResponseMapper.toProductResponseDTO(product);
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        log.debug("Creating new product: {}", productRequestDTO);
        validateProductRequest(productRequestDTO);

        Product product = productRequestMapper.toProduct(productRequestDTO);
        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with id: {}", savedProduct.getId());
        return productResponseMapper.toProductResponseDTO(savedProduct);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        log.debug("Updating product with id: {}", id);
        validateProductRequest(productRequestDTO);

        Product existingProduct = getProductByIdOrThrow(id);
        Product updatedProduct = productRequestMapper.updateProduct(productRequestDTO, existingProduct);
        Product savedProduct = productRepository.save(updatedProduct);

        log.info("Product updated successfully with id: {}", id);
        return productResponseMapper.toProductResponseDTO(savedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.debug("Deleting product with id: {}", id);
        if (!productRepository.existsById(id)) {
            System.out.println ("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        log.info("Product deleted successfully with id: {}", id);
    }

    public List<ProductResponseDTO> findProductsByName(String name) {
        log.debug("Finding products by name: {}", name);
        Specification<Product> spec = ProductSpecs.hasName(name);
        List<Product> products = productRepository.findAll(spec);
        return productResponseMapper.toProductResponseDTOList(products);
    }

    public List<ProductResponseDTO> findProductsByCategory(String category) {
        log.debug("Finding products by category: {}", category);
        Specification<Product> spec = ProductSpecs.hasCategory(category);
        List<Product> products = productRepository.findAll(spec);
        return productResponseMapper.toProductResponseDTOList(products);
    }

    public List<ProductResponseDTO> getActiveProducts() {
        log.debug("Finding active products");
        Specification<Product> spec = ProductSpecs.isActiveAndAvailable();
        List<Product> products = productRepository.findAll(spec);
        return productResponseMapper.toProductResponseDTOList(products);
    }

    public List<ProductResponseDTO> searchProductsByKeyword(String keyword) {
        log.debug("Searching products by keyword: {}", keyword);
        Specification<Product> spec = ProductSpecs.searchByKeyword(keyword);
        List<Product> products = productRepository.findAll(spec);
        return productResponseMapper.toProductResponseDTOList(products);
    }

    public List<ProductResponseDTO> findProductsByCategoryAndAvailability(String category, Boolean availability) {
        log.debug("Finding products by category: {} and availability: {}", category, availability);
        Specification<Product> spec = ProductSpecs.hasCategory(category)
                .and(ProductSpecs.hasAvailability(availability));
        List<Product> products = productRepository.findAll(spec);
        return productResponseMapper.toProductResponseDTOList(products);
    }

    public List<ProductResponseDTO> findProductsByPriceRange(Double minPrice, Double maxPrice) {
        log.debug("Finding products by price range: {} - {}", minPrice, maxPrice);
        Specification<Product> spec = ProductSpecs.hasPriceBetween(minPrice, maxPrice);
        List<Product> products = productRepository.findAll(spec);
        return productResponseMapper.toProductResponseDTOList(products);
    }

    public List<ProductResponseDTO> findAvailableProductsAbovePrice(Double minPrice) {
        log.debug("Finding available products above price: {}", minPrice);
        Specification<Product> spec = ProductSpecs.isAvailable()
                .and(ProductSpecs.hasPriceGreaterThan(minPrice));
        List<Product> products = productRepository.findAll(spec);
        return productResponseMapper.toProductResponseDTOList(products);
    }

    @Transactional
    public ProductResponseDTO updateProductStatus(Long id, ProductStatus status) {
        log.debug("Updating product status for id: {} to status: {}", id, status);
        Product product = getProductByIdOrThrow(id);
        product.setStatus(status);
        Product savedProduct = productRepository.save(product);

        log.info("Product status updated successfully for id: {}", id);
        return productResponseMapper.toProductResponseDTO(savedProduct);
    }

    // Private helper methods
    private Product getProductByIdOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->  new IllegalArgumentException("Product not found with id: " + id));
    }

    private void validateProductRequest(ProductRequestDTO productRequestDTO) {
        if (productRequestDTO == null) {
            throw new IllegalArgumentException("Product request cannot be null");
        }
        if (!StringUtils.hasText(productRequestDTO.getName())) {
            throw new IllegalArgumentException("Product name is required");
        }
    }
}
