package com.example.intelligent_inventory_prediction_system.controller;

import com.example.intelligent_inventory_prediction_system.dto.request.ProductRequestDTO;
import com.example.intelligent_inventory_prediction_system.dto.response.ProductResponseDTO;
import com.example.intelligent_inventory_prediction_system.exception.ResourceNotFoundException;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import com.example.intelligent_inventory_prediction_system.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(
        controllers = ProductController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
@DisplayName("ProductController Unit Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductResponseDTO productResponseDTO;
    private ProductRequestDTO productRequestDTO;

    private static final String BASE_URL = "/api/products";
    private static final Long TEST_ID = 1L;
    private static final Long NON_EXISTENT_ID = 999L;
    private static final String TEST_PRODUCT_NAME = "Test Product";
    private static final String TEST_CATEGORY = "Electronics";
    private static final double TEST_PRICE = 99.99;

    @BeforeEach
    void setUp() {
        // Setup response DTO
        productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(TEST_ID);
        productResponseDTO.setName(TEST_PRODUCT_NAME);
        productResponseDTO.setPrice(TEST_PRICE);
        productResponseDTO.setCategory(TEST_CATEGORY);
        productResponseDTO.setDescription("Test description");
        productResponseDTO.setStatus(ProductStatus.AVAILABLE);

        // Setup request DTO
        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName(TEST_PRODUCT_NAME);
        productRequestDTO.setPrice(TEST_PRICE);
        productRequestDTO.setCategory(TEST_CATEGORY);
        productRequestDTO.setDescription("Request description");
        productRequestDTO.setStatus(ProductStatus.AVAILABLE); // ✅ اضافه کردن status
    }

    @Test
    @DisplayName("GET /api/products - Should return paginated products with status 200")
    void getAllProductsInPage_ShouldReturnPageAndStatus200() throws Exception {
        // Given
        List<ProductResponseDTO> productList = Collections.singletonList(productResponseDTO);
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductResponseDTO> mockPage = new PageImpl<>(productList, pageable, 1L);

        when(productService.findAllProducts(any(Pageable.class))).thenReturn(mockPage);

        // When & Then
        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(1L))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.content[0].id").value(TEST_ID))
                .andExpect(jsonPath("$.content[0].name").value(TEST_PRODUCT_NAME))
                .andExpect(jsonPath("$.content[0].price").value(TEST_PRICE))
                .andExpect(jsonPath("$.content[0].status").value(ProductStatus.AVAILABLE.toString()));

        verify(productService, times(1)).findAllProducts(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return product by ID with status 200")
    void getProductById_ShouldReturnProductAndStatus200() throws Exception {
        // Given
        when(productService.findProductById(TEST_ID)).thenReturn(productResponseDTO);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}", TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.name").value(TEST_PRODUCT_NAME))
                .andExpect(jsonPath("$.price").value(TEST_PRICE))
                .andExpect(jsonPath("$.category").value(TEST_CATEGORY))
                .andExpect(jsonPath("$.status").value(ProductStatus.AVAILABLE.toString()));

        verify(productService, times(1)).findProductById(TEST_ID);
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return 404 when product not found")
    void getProductById_WhenNotFound_ShouldReturnStatus404() throws Exception {
        // Given
        when(productService.findProductById(NON_EXISTENT_ID)).thenReturn(null);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/{id}", NON_EXISTENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(productService, times(1)).findProductById(NON_EXISTENT_ID);
    }

    @Test
    @DisplayName("POST /api/products - Should create product and return 201")
    void createProduct_ShouldReturnProductAndStatus201() throws Exception {
        // Given
        when(productService.createProduct(any(ProductRequestDTO.class)))
                .thenReturn(productResponseDTO);

        String requestJson = objectMapper.writeValueAsString(productRequestDTO);

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.name").value(TEST_PRODUCT_NAME))
                .andExpect(jsonPath("$.price").value(TEST_PRICE))
                .andExpect(jsonPath("$.status").value(ProductStatus.AVAILABLE.toString()));

        verify(productService, times(1)).createProduct(any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Should update product and return 200")
    void updateProduct_ShouldReturnProductAndStatus200() throws Exception {
        // Given
        ProductResponseDTO updatedProduct = new ProductResponseDTO();
        updatedProduct.setId(TEST_ID);
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(199.99);
        updatedProduct.setCategory("Updated Category");
        updatedProduct.setStatus(ProductStatus.AVAILABLE);

        when(productService.updateProduct(eq(TEST_ID), any(ProductRequestDTO.class)))
                .thenReturn(updatedProduct);

        String requestJson = objectMapper.writeValueAsString(productRequestDTO);

        // When & Then
        mockMvc.perform(put(BASE_URL + "/{id}", TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(199.99));

        verify(productService, times(1)).updateProduct(eq(TEST_ID), any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("PATCH /api/products/{id} - Should update product status and return 200")
    void updateProductStatus_ShouldReturnProductAndStatus200() throws Exception {
        // Given
        ProductStatus newStatus = ProductStatus.NOT_AVAILABLE;
        ProductResponseDTO updatedProduct = new ProductResponseDTO();
        updatedProduct.setId(TEST_ID);
        updatedProduct.setName(TEST_PRODUCT_NAME);
        updatedProduct.setPrice(TEST_PRICE);
        updatedProduct.setStatus(newStatus);

        when(productService.updateProductStatus(eq(TEST_ID), eq(newStatus)))
                .thenReturn(updatedProduct);

        // When & Then
        mockMvc.perform(patch(BASE_URL + "/{id}", TEST_ID)
                        .param("status", newStatus.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.status").value(newStatus.toString()));

        verify(productService, times(1)).updateProductStatus(TEST_ID, newStatus);
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Should delete product and return 204")
    void deleteProduct_ShouldReturnStatus204NoContent() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(TEST_ID);

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/{id}", TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string("")); // بدون محتوا

        verify(productService, times(1)).deleteProduct(TEST_ID);
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Should return 404 when product not found")
    void deleteProduct_WhenNotFound_ShouldReturnStatus404() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Product not found with id " + NON_EXISTENT_ID))
                .when(productService).deleteProduct(NON_EXISTENT_ID);

        // When & Then
        mockMvc.perform(delete(BASE_URL + "/{id}", NON_EXISTENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(productService, times(1)).deleteProduct(NON_EXISTENT_ID);
    }

    @Test
    @DisplayName("POST /api/products - Should return 400 when validation fails")
    void createProduct_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given - محصول نامعتبر (price منفی)
        ProductRequestDTO invalidRequest = new ProductRequestDTO();
        invalidRequest.setName(""); // نام خالی
        invalidRequest.setPrice(-10.0); // قیمت منفی

        String requestJson = objectMapper.writeValueAsString(invalidRequest);

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(ProductRequestDTO.class));
    }
}
