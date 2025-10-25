package com.example.intelligent_inventory_prediction_system.controller;

import com.example.intelligent_inventory_prediction_system.dto.request.ProductRequestDTO;
import com.example.intelligent_inventory_prediction_system.dto.response.ProductResponseDTO;
import com.example.intelligent_inventory_prediction_system.exception.ResourceNotFoundException;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import com.example.intelligent_inventory_prediction_system.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
@WithMockUser
class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductResponseDTO productResponseDTO;
    private ProductRequestDTO productRequestDTO;
    private static final String BASE_URL = "/api/products";
    private static final Long TEST_ID = 1L;
    private static final Long NON_EXISTENT_ID = 99L;


    @BeforeEach
    void setUp() {
        productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setName("Test Product");
        productResponseDTO.setPrice(10.0);
        productResponseDTO.setId(TEST_ID);
        productResponseDTO.setStatus(ProductStatus.AVAILABLE);

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("Request Product");
        productRequestDTO.setPrice(20.0);
    }

    @Test
    void getAllProductsInPage_ShouldReturnPageAndStatus200() throws Exception {
        List<ProductResponseDTO> productList = Collections.singletonList(productResponseDTO);
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductResponseDTO> mockPage = new PageImpl<>(productList, pageable, 1L);
        when(productService.findAllProducts(any(Pageable.class))).thenReturn(mockPage);

        ResultActions result = mvc.perform(get(BASE_URL)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1L))
                .andExpect(jsonPath("$.content[0].name").value(productResponseDTO.getName()));

        verify(productService, times(1)).findAllProducts(any(Pageable.class));
    }

    @Test
    void getProductById_ShouldReturnProductAndStatus200() throws Exception {
        when(productService.findProductById(TEST_ID)).thenReturn(productResponseDTO);

        ResultActions result = mvc.perform(get(BASE_URL + "/{id}", TEST_ID)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.price").value(productResponseDTO.getPrice()));

        verify(productService, times(1)).findProductById(TEST_ID);
    }

    @Test
    void getProductById_WhenNotFound_ShouldReturnStatus404() throws Exception {
        when(productService.findProductById(NON_EXISTENT_ID)).thenReturn(null);

        mvc.perform(get(BASE_URL + "/{id}", NON_EXISTENT_ID))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).findProductById(NON_EXISTENT_ID);
    }

    @Test
    void createProduct_ShouldReturnProductAndStatus201() throws Exception {
        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(productResponseDTO);
        String requestJson = objectMapper.writeValueAsString(productRequestDTO);

        ResultActions result = mvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(productResponseDTO.getName()));

        verify(productService, times(1)).createProduct(any(ProductRequestDTO.class));
    }

    @Test
    void updateProduct_ShouldReturnProductAndStatus200() throws Exception {
        when(productService.updateProduct(eq(TEST_ID), any(ProductRequestDTO.class))).thenReturn(productResponseDTO);
        String requestJson = objectMapper.writeValueAsString(productRequestDTO);

        ResultActions result = mvc.perform(put(BASE_URL + "/{id}", TEST_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.price").value(productResponseDTO.getPrice()));

        verify(productService, times(1)).updateProduct(eq(TEST_ID), any(ProductRequestDTO.class));
    }

    @Test
    void updateProductStatus_ShouldReturnProductAndStatus200() throws Exception {
        ProductStatus newStatus = ProductStatus.NOT_AVAILABLE;
        ProductResponseDTO updatedDto = new ProductResponseDTO();
        updatedDto.setId(TEST_ID);
        updatedDto.setStatus(newStatus);

        when(productService.updateProductStatus(eq(TEST_ID), eq(newStatus))).thenReturn(updatedDto);

        ResultActions result = mvc.perform(patch(BASE_URL + "/{id}", TEST_ID)
                .param("status", newStatus.name())
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(newStatus.toString()));

        verify(productService, times(1)).updateProductStatus(TEST_ID, newStatus);
    }

    @Test
    void deleteProduct_ShouldReturnStatus204NoContent() throws Exception {
        doNothing().when(productService).deleteProduct(TEST_ID);

        ResultActions result = mvc.perform(delete(BASE_URL + "/{id}", TEST_ID));

        result.andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(TEST_ID);
    }

    @Test
    void deleteProduct_WhenNotFound_ShouldReturnStatus404() throws Exception {
        doThrow(new ResourceNotFoundException("Not found")).when(productService).deleteProduct(NON_EXISTENT_ID);

        mvc.perform(delete(BASE_URL + "/{id}", NON_EXISTENT_ID))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).deleteProduct(NON_EXISTENT_ID);
    }
}