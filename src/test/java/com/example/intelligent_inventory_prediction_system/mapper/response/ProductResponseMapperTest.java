package com.example.intelligent_inventory_prediction_system.mapper.response;

import com.example.intelligent_inventory_prediction_system.dto.response.ProductResponseDTO;
import com.example.intelligent_inventory_prediction_system.model.Product;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class ProductResponseMapperTest {
    private ProductResponseMapper productResponseMapper;
    @BeforeEach
    void setUp() {
        productResponseMapper = Mappers.getMapper(ProductResponseMapper.class);
    }

    private Product createSampleProduct() {
        Product product = new Product();
        product.setName("Sample Product");
        product.setDescription("Sample Description");
        product.setCategory("Sample Category");
        product.setPrice(10.5);
        product.setStatus(ProductStatus.AVAILABLE);
        return product;
    }


    private ProductResponseDTO createSampleProductResponseDTO() {
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setName("Sample Product");
        productResponseDTO.setId(1L);
        productResponseDTO.setPrice(10.5);
        productResponseDTO.setStatus(ProductStatus.AVAILABLE);
        return productResponseDTO;
    }

    @Test
    public void testProductResponseDTOToProduct() {
        ProductResponseDTO responseDTO = createSampleProductResponseDTO();
        Product mappedProduct = productResponseMapper.toProduct(responseDTO);
        assertAll(
                () -> assertNotNull(mappedProduct),
                () -> assertEquals(mappedProduct.getId(), responseDTO.getId()),
                () -> assertEquals(mappedProduct.getName(), responseDTO.getName()),
                () -> assertEquals(mappedProduct.getCategory(), responseDTO.getCategory()),
                () -> assertEquals(mappedProduct.getPrice(), responseDTO.getPrice()),
                () -> assertEquals(mappedProduct.getStatus(), responseDTO.getStatus())
        );
    }
    @Test
    public void testProductToProductResponseDTO(){
        Product sampleProduct = createSampleProduct();
        ProductResponseDTO mappedProductResponseDTO = productResponseMapper.toProductResponseDTO(sampleProduct);
        assertAll(
                () -> assertNotNull(mappedProductResponseDTO),
                () -> assertEquals(sampleProduct.getId(), mappedProductResponseDTO.getId()),
                () -> assertEquals(sampleProduct.getName(), mappedProductResponseDTO.getName()),
                () -> assertEquals(sampleProduct.getCategory(), mappedProductResponseDTO.getCategory()),
                () -> assertEquals(sampleProduct.getPrice(), mappedProductResponseDTO.getPrice()),
                () -> assertEquals(sampleProduct.getStatus(), mappedProductResponseDTO.getStatus())

        );
    }

}