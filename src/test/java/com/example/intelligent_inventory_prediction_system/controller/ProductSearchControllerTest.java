package com.example.intelligent_inventory_prediction_system.controller;

import com.example.intelligent_inventory_prediction_system.dto.response.ProductResponseDTO;
import com.example.intelligent_inventory_prediction_system.dto.search.ProductSearchCriteria;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import com.example.intelligent_inventory_prediction_system.service.ProductSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductSearchController.class)
@WithMockUser
class ProductSearchControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductSearchService productSearchService;

    private ProductResponseDTO productResponseDTO;
    private List<ProductResponseDTO> productList;
    private static final String BASE_URL = "/api/products/search";
    private static final String KEYWORD = "laptop";
    private static final String CATEGORY = "Electronics";
    private static final Double MIN_PRICE = 40.0;
    private static final Double MAX_PRICE = 60.0;

    @BeforeEach
    void setUp() {
        productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setName("Test Product");
        productResponseDTO.setPrice(50.0);
        productResponseDTO.setId(1L);
        productResponseDTO.setStatus(ProductStatus.AVAILABLE);

        productList = Collections.singletonList(productResponseDTO);
    }

    @Test
    void searchProducts_AdvancedSearch_ShouldReturnProductsAndStatus200() throws Exception {
        ProductSearchCriteria criteria = new ProductSearchCriteria();
        criteria.setKeyword(KEYWORD);

        when(productSearchService.searchProducts(any(ProductSearchCriteria.class))).thenReturn(productList);

        mvc.perform(get(BASE_URL + "/advanced")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criteria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value(productResponseDTO.getName()));
    }

    @Test
    void findByKeyword_ShouldReturnProductsAndStatus200() throws Exception {
        when(productSearchService.findByKeyword(KEYWORD)).thenReturn(productList);

        mvc.perform(get(BASE_URL + "/keyword")
                        .param("value", KEYWORD)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(productResponseDTO.getId()));
    }

    @Test
    void findByName_ShouldReturnProductsAndStatus200() throws Exception {
        String name = "Test";
        when(productSearchService.findByName(name)).thenReturn(productList);

        mvc.perform(get(BASE_URL + "/name")
                        .param("value", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(productResponseDTO.getName()));
    }

    @Test
    void findByPriceRange_ShouldReturnProductsAndStatus200() throws Exception {
        when(productSearchService.findByPriceRange(eq(MIN_PRICE), eq(MAX_PRICE))).thenReturn(productList);

        mvc.perform(get(BASE_URL + "/price-range")
                        .param("min", String.valueOf(MIN_PRICE))
                        .param("max", String.valueOf(MAX_PRICE))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(productResponseDTO.getPrice()));
    }

    @Test
    void findByStatus_ShouldReturnProductsAndStatus200() throws Exception {
        ProductStatus status = ProductStatus.AVAILABLE;
        when(productSearchService.findByStatus(status)).thenReturn(productList);

        mvc.perform(get(BASE_URL + "/status")
                        .param("value", status.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value(status.toString()));
    }

    @Test
    void findAvailableProductsAbovePrice_ShouldReturnProductsAndStatus200() throws Exception {
        when(productSearchService.findAvailableProductsAbovePrice(MIN_PRICE)).thenReturn(productList);

        mvc.perform(get(BASE_URL + "/available-above")
                        .param("price", String.valueOf(MIN_PRICE))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void findActiveProducts_ShouldReturnProductsAndStatus200() throws Exception {
        when(productSearchService.findActiveProducts()).thenReturn(productList);

        mvc.perform(get(BASE_URL + "/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}