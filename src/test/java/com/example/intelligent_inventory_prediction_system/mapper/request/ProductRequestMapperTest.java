package com.example.intelligent_inventory_prediction_system.mapper.request;

import com.example.intelligent_inventory_prediction_system.dto.request.ProductRequestDTO;
import com.example.intelligent_inventory_prediction_system.model.Product;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class ProductRequestMapperTest {
    private ProductRequestMapper productRequestMapper;

    @BeforeEach
    void setUp() {
        productRequestMapper = Mappers.getMapper(ProductRequestMapper.class);
    }

    private ProductRequestDTO createSampleProductRequestDTO() {
        ProductRequestDTO productResponseDTO = new ProductRequestDTO();
        productResponseDTO.setName("test");
        productResponseDTO.setDescription("desc");
        productResponseDTO.setPrice(10.5);
        productResponseDTO.setCategory("category");
        productResponseDTO.setStatus(ProductStatus.AVAILABLE);
        return productResponseDTO;
    }

    private Product createSampleProduct() {
        Product product = new Product();
        product.setName("test");
        product.setDescription("desc");
        product.setPrice(10.5);
        product.setCategory("category");
        product.setStatus(ProductStatus.AVAILABLE);
        return product;
    }

    @Test
    void testMapProductRequestDTOToProduct() {
        ProductRequestDTO requestDTO = createSampleProductRequestDTO();

        Product mappedProduct = productRequestMapper.toProduct(requestDTO);

        assertAll(
                () -> assertNotNull(mappedProduct),
                () -> assertEquals(requestDTO.getName(), mappedProduct.getName()),
                () -> assertEquals(requestDTO.getDescription(), mappedProduct.getDescription()),
                () -> assertEquals(requestDTO.getPrice(), mappedProduct.getPrice()),
                () -> assertEquals(requestDTO.getCategory(), mappedProduct.getCategory()),
                () -> assertEquals(requestDTO.getStatus(), mappedProduct.getStatus()));
    }

    @Test
    void testMapProductToProductRequestDTO() {
        Product product = createSampleProduct();

        ProductRequestDTO mappedRequestDTO = productRequestMapper.toProductRequestDTO(product);

        assertAll(
                () -> assertNotNull(mappedRequestDTO),
                () -> assertEquals(product.getName(), mappedRequestDTO.getName()),
                () -> assertEquals(product.getDescription(), mappedRequestDTO.getDescription()),
                () -> assertEquals(product.getPrice(), mappedRequestDTO.getPrice()),
                () -> assertEquals(product.getCategory(), mappedRequestDTO.getCategory()),
                () -> assertEquals(product.getStatus(), mappedRequestDTO.getStatus()));

    }



}