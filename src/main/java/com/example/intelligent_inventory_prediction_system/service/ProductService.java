package com.example.intelligent_inventory_prediction_system.service;

import com.example.intelligent_inventory_prediction_system.dto.request.ProductRequestDTO;
import com.example.intelligent_inventory_prediction_system.dto.response.ProductResponseDTO;
import com.example.intelligent_inventory_prediction_system.mapper.request.ProductRequestMapper;
import com.example.intelligent_inventory_prediction_system.mapper.response.ProductResponseMapper;
import com.example.intelligent_inventory_prediction_system.model.Product;
import com.example.intelligent_inventory_prediction_system.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductRequestMapper productRequestMapper;

    @Autowired
    private ProductResponseMapper productResponseMapper;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void addProduct(ProductRequestDTO productRequestDTO) {
        Product product = productRequestMapper.toProduct(productRequestDTO);
        productRepository.save(product);
    }

    public void updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product with id " + id + " not found");
        }

        Product product = productRequestMapper.toProduct(productRequestDTO);
        product.setId(id);
        productRepository.save(product);
    }
    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

    public List<ProductResponseDTO> getAllProductRequests() {
        List<Product> products = productRepository.findAll();
        return productResponseMapper.toProductResponseDTOList(products);
    }

}
