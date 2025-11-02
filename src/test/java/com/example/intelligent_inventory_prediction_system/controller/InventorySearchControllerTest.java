package com.example.intelligent_inventory_prediction_system.controller;

import com.example.intelligent_inventory_prediction_system.dto.response.InventoryResponseDTO;
import com.example.intelligent_inventory_prediction_system.dto.search.InventorySearchCriteria;
import com.example.intelligent_inventory_prediction_system.service.InventorySearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InventorySearchController.class)
@WithMockUser
@DisplayName("InventorySearchController Unit Tests")
class InventorySearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InventorySearchService inventorySearchService;

    private InventoryResponseDTO firstInventoryDTO;
    private InventoryResponseDTO secondInventoryDTO;
    private InventorySearchCriteria searchCriteria;

    private static final String BASE_URL = "/api/inventory/search";
    private static final Long TEST_PRODUCT_ID = 1L;
    private static final Long SECOND_PRODUCT_ID = 2L;
    private static final Integer MIN_STOCK = 10;
    private static final Integer MAX_STOCK = 100;
    private static final Integer CRITICAL_STOCK = 5;

    @BeforeEach
    void setUp() {
        firstInventoryDTO = createInventoryDTO(1L, TEST_PRODUCT_ID, 50);
        secondInventoryDTO = createInventoryDTO(2L, SECOND_PRODUCT_ID, 75);

        searchCriteria = new InventorySearchCriteria();
        searchCriteria.setProductId(TEST_PRODUCT_ID);
        searchCriteria.setMinStock(MIN_STOCK);
        searchCriteria.setMaxStock(MAX_STOCK);
    }

    private InventoryResponseDTO createInventoryDTO(Long id, Long productId, Integer currentStock) {
        InventoryResponseDTO dto = new InventoryResponseDTO();
        dto.setId(id);
        dto.setProductId(productId);
        dto.setCurrentStock(currentStock);
        dto.setLastUpdated(LocalDateTime.now());
        return dto;
    }

    @Nested
    @DisplayName("Advanced Search Tests")
    class AdvancedSearchTests {

        @Test
        @DisplayName("Should return single inventory when criteria matches one record")
        void searchInventories_WithValidCriteria_ShouldReturnSingleInventory() throws Exception {
            // Given
            List<InventoryResponseDTO> expectedList = List.of(firstInventoryDTO);
            when(inventorySearchService.searchInventories(any(InventorySearchCriteria.class)))
                    .thenReturn(expectedList);
            String requestJson = objectMapper.writeValueAsString(searchCriteria);

            // When
            ResultActions result = mockMvc.perform(post(BASE_URL + "/advanced")
                    .with(csrf())  // ✅ FIX: Add CSRF token
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(firstInventoryDTO.getId()))
                    .andExpect(jsonPath("$[0].productId").value(TEST_PRODUCT_ID))
                    .andExpect(jsonPath("$[0].currentStock").value(50))
                    .andExpect(jsonPath("$[0].lastUpdated").exists());

            verify(inventorySearchService, times(1)).searchInventories(any(InventorySearchCriteria.class));
        }

        @Test
        @DisplayName("Should return multiple inventories when criteria matches multiple records")
        void searchInventories_WithBroadCriteria_ShouldReturnMultipleInventories() throws Exception {
            // Given
            List<InventoryResponseDTO> expectedList = List.of(firstInventoryDTO, secondInventoryDTO);
            InventorySearchCriteria broadCriteria = new InventorySearchCriteria();
            broadCriteria.setMinStock(MIN_STOCK);

            when(inventorySearchService.searchInventories(any(InventorySearchCriteria.class)))
                    .thenReturn(expectedList);
            String requestJson = objectMapper.writeValueAsString(broadCriteria);

            // When
            ResultActions result = mockMvc.perform(post(BASE_URL + "/advanced")
                    .with(csrf())  // ✅ FIX
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].productId").value(TEST_PRODUCT_ID))
                    .andExpect(jsonPath("$[0].currentStock").value(50))
                    .andExpect(jsonPath("$[1].productId").value(SECOND_PRODUCT_ID))
                    .andExpect(jsonPath("$[1].currentStock").value(75));

            verify(inventorySearchService, times(1)).searchInventories(any(InventorySearchCriteria.class));
        }

        @Test
        @DisplayName("Should return empty list when no inventory matches criteria")
        void searchInventories_WithNoMatches_ShouldReturnEmptyList() throws Exception {
            // Given
            when(inventorySearchService.searchInventories(any(InventorySearchCriteria.class)))
                    .thenReturn(Collections.emptyList());
            String requestJson = objectMapper.writeValueAsString(searchCriteria);

            // When
            ResultActions result = mockMvc.perform(post(BASE_URL + "/advanced")
                    .with(csrf())  // ✅ FIX
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)))
                    .andExpect(jsonPath("$", empty()));

            verify(inventorySearchService, times(1)).searchInventories(any(InventorySearchCriteria.class));
        }

        @Test
        @DisplayName("Should return 400 when request body is invalid JSON")
        void searchInventories_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
            // Given: Malformed JSON
            String invalidJson = "{invalid json}";

            // When
            ResultActions result = mockMvc.perform(post(BASE_URL + "/advanced")
                    .with(csrf())  // ✅ FIX
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson));

            // Then
            result.andExpect(status().isBadRequest());

            verify(inventorySearchService, never()).searchInventories(any());
        }

        @Test
        @DisplayName("Should handle null criteria fields gracefully")
        void searchInventories_WithNullFields_ShouldProcessCorrectly() throws Exception {
            // Given: Criteria with null fields
            InventorySearchCriteria nullCriteria = new InventorySearchCriteria();
            when(inventorySearchService.searchInventories(any(InventorySearchCriteria.class)))
                    .thenReturn(List.of(firstInventoryDTO));
            String requestJson = objectMapper.writeValueAsString(nullCriteria);

            // When
            ResultActions result = mockMvc.perform(post(BASE_URL + "/advanced")
                    .with(csrf())  // ✅ FIX
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));

            verify(inventorySearchService, times(1)).searchInventories(any(InventorySearchCriteria.class));
        }
    }

    @Nested
    @DisplayName("Find By Product ID Tests")
    class FindByProductIdTests {

        @Test
        @DisplayName("Should return inventory when valid product ID exists")
        void findByProductId_WithValidId_ShouldReturnInventory() throws Exception {
            // Given
            List<InventoryResponseDTO> expectedList = List.of(firstInventoryDTO);
            when(inventorySearchService.findByProductId(TEST_PRODUCT_ID))
                    .thenReturn(expectedList);

            // When
            ResultActions result = mockMvc.perform(get(BASE_URL + "/product/{productId}", TEST_PRODUCT_ID)
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].productId").value(TEST_PRODUCT_ID))
                    .andExpect(jsonPath("$[0].currentStock").value(50))
                    .andExpect(jsonPath("$[0].lastUpdated").isNotEmpty());

            verify(inventorySearchService, times(1)).findByProductId(TEST_PRODUCT_ID);
        }

        @Test
        @DisplayName("Should return empty list when product has no inventory records")
        void findByProductId_WithNoInventory_ShouldReturnEmptyList() throws Exception {
            // Given
            Long nonExistentProductId = 999L;
            when(inventorySearchService.findByProductId(nonExistentProductId))
                    .thenReturn(Collections.emptyList());

            // When
            ResultActions result = mockMvc.perform(get(BASE_URL + "/product/{productId}", nonExistentProductId)
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(inventorySearchService, times(1)).findByProductId(nonExistentProductId);
        }

        @Test
        @DisplayName("Should return multiple inventory records for same product")
        void findByProductId_WithMultipleRecords_ShouldReturnAll() throws Exception {
            // Given: Same product, different inventory records
            InventoryResponseDTO record1 = createInventoryDTO(1L, TEST_PRODUCT_ID, 50);
            InventoryResponseDTO record2 = createInventoryDTO(2L, TEST_PRODUCT_ID, 30);

            when(inventorySearchService.findByProductId(TEST_PRODUCT_ID))
                    .thenReturn(List.of(record1, record2));

            // When
            ResultActions result = mockMvc.perform(get(BASE_URL + "/product/{productId}", TEST_PRODUCT_ID)
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].productId").value(TEST_PRODUCT_ID))
                    .andExpect(jsonPath("$[1].productId").value(TEST_PRODUCT_ID))
                    .andExpect(jsonPath("$[0].currentStock").value(50))
                    .andExpect(jsonPath("$[1].currentStock").value(30));

            verify(inventorySearchService, times(1)).findByProductId(TEST_PRODUCT_ID);
        }
    }

    @Nested
    @DisplayName("Find By Stock Range Tests")
    class FindByStockRangeTests {

        @Test
        @DisplayName("Should return inventories within specified range")
        void findByStockRange_WithValidRange_ShouldReturnMatchingInventories() throws Exception {
            // Given
            List<InventoryResponseDTO> expectedList = List.of(firstInventoryDTO, secondInventoryDTO);
            when(inventorySearchService.findByStockRange(MIN_STOCK, MAX_STOCK))
                    .thenReturn(expectedList);

            // When
            ResultActions result = mockMvc.perform(get(BASE_URL + "/stock-range")
                    .param("minStock", MIN_STOCK.toString())
                    .param("maxStock", MAX_STOCK.toString())
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].currentStock").value(50))
                    .andExpect(jsonPath("$[1].currentStock").value(75));

            verify(inventorySearchService, times(1)).findByStockRange(MIN_STOCK, MAX_STOCK);
        }

        @Test
        @DisplayName("Should return inventories above minimum when only minStock provided")
        void findByStockRange_WithOnlyMinStock_ShouldReturnAboveMinimum() throws Exception {
            // Given
            when(inventorySearchService.findByStockRange(eq(MIN_STOCK), isNull()))
                    .thenReturn(List.of(firstInventoryDTO, secondInventoryDTO));

            // When
            ResultActions result = mockMvc.perform(get(BASE_URL + "/stock-range")
                    .param("minStock", MIN_STOCK.toString())
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[*].currentStock", everyItem(greaterThanOrEqualTo(MIN_STOCK))));

            verify(inventorySearchService, times(1)).findByStockRange(MIN_STOCK, null);
        }

        @Test
        @DisplayName("Should return inventories below maximum when only maxStock provided")
        void findByStockRange_WithOnlyMaxStock_ShouldReturnBelowMaximum() throws Exception {
            // Given
            InventoryResponseDTO lowStockDTO = createInventoryDTO(3L, 3L, 8);
            when(inventorySearchService.findByStockRange(isNull(), eq(MAX_STOCK)))
                    .thenReturn(List.of(lowStockDTO, firstInventoryDTO));

            // When
            ResultActions result = mockMvc.perform(get(BASE_URL + "/stock-range")
                    .param("maxStock", MAX_STOCK.toString())
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].currentStock").value(8))
                    .andExpect(jsonPath("$[1].currentStock").value(50));

            verify(inventorySearchService, times(1)).findByStockRange(null, MAX_STOCK);
        }

        @Test
        @DisplayName("Should return empty list when no inventory in range")
        void findByStockRange_WithNoMatchingRange_ShouldReturnEmpty() throws Exception {
            // Given: Unrealistic range
            Integer veryHighMin = 1000;
            Integer veryHighMax = 2000;
            when(inventorySearchService.findByStockRange(veryHighMin, veryHighMax))
                    .thenReturn(Collections.emptyList());

            // When
            ResultActions result = mockMvc.perform(get(BASE_URL + "/stock-range")
                    .param("minStock", veryHighMin.toString())
                    .param("maxStock", veryHighMax.toString())
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(inventorySearchService, times(1)).findByStockRange(veryHighMin, veryHighMax);
        }
    }

    @Nested
    @DisplayName("Find By Minimum Stock Tests")
    class FindByMinimumStockTests {

        @Test
        @DisplayName("Should return inventories above minimum stock threshold")
        void findByMinimumStock_WithValidThreshold_ShouldReturnAboveThreshold() throws Exception {
            // Given
            List<InventoryResponseDTO> expectedList = List.of(firstInventoryDTO, secondInventoryDTO);
            when(inventorySearchService.findByMinimumStock(MIN_STOCK))
                    .thenReturn(expectedList);

            // When
            ResultActions result = mockMvc.perform(get(BASE_URL + "/minimum-stock")
                    .param("minStock", MIN_STOCK.toString())
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].currentStock").value(50))
                    .andExpect(jsonPath("$[1].currentStock").value(75));

            verify(inventorySearchService, times(1)).findByMinimumStock(MIN_STOCK);
        }

        @Test
        @DisplayName("Should identify critical stock levels")
        void findByMinimumStock_WithCriticalLevel_ShouldReturnLowStockItems() throws Exception {
            // Given: Items with critical stock
            InventoryResponseDTO criticalStock = createInventoryDTO(3L, 3L, 3);
            when(inventorySearchService.findByMinimumStock(CRITICAL_STOCK))
                    .thenReturn(List.of(criticalStock));

            // When
            ResultActions result = mockMvc.perform(get(BASE_URL + "/minimum-stock")
                    .param("minStock", CRITICAL_STOCK.toString())
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].currentStock").value(3))
                    .andExpect(jsonPath("$[0].productId").value(3L));

            verify(inventorySearchService, times(1)).findByMinimumStock(CRITICAL_STOCK);
        }

        @Test
        @DisplayName("Should return empty when all inventories below threshold")
        void findByMinimumStock_WithHighThreshold_ShouldReturnEmpty() throws Exception {
            // Given
            Integer veryHighThreshold = 1000;
            when(inventorySearchService.findByMinimumStock(veryHighThreshold))
                    .thenReturn(Collections.emptyList());

            // When
            ResultActions result = mockMvc.perform(get(BASE_URL + "/minimum-stock")
                    .param("minStock", veryHighThreshold.toString())
                    .contentType(MediaType.APPLICATION_JSON));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(inventorySearchService, times(1)).findByMinimumStock(veryHighThreshold);
        }
    }

    @Nested
    @DisplayName("Response Format Validation Tests")
    class ResponseFormatValidationTests {

        @Test
        @DisplayName("Should include all required fields in response")
        void allEndpoints_ShouldReturnCompleteDTO() throws Exception {
            // Given
            when(inventorySearchService.findByProductId(TEST_PRODUCT_ID))
                    .thenReturn(List.of(firstInventoryDTO));

            // When
            ResultActions result = mockMvc.perform(get(BASE_URL + "/product/{productId}", TEST_PRODUCT_ID));

            // Then: Verify all DTO fields are present
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").exists())
                    .andExpect(jsonPath("$[0].productId").exists())
                    .andExpect(jsonPath("$[0].currentStock").exists())
                    .andExpect(jsonPath("$[0].lastUpdated").exists());
        }

        @Test
        @DisplayName("Should return consistent date format for lastUpdated")
        void responseDTO_ShouldHaveConsistentDateFormat() throws Exception {
            // Given
            when(inventorySearchService.findByProductId(TEST_PRODUCT_ID))
                    .thenReturn(List.of(firstInventoryDTO));

            // When
            ResultActions result = mockMvc.perform(get(BASE_URL + "/product/{productId}", TEST_PRODUCT_ID));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].lastUpdated").isNotEmpty())
                    .andExpect(jsonPath("$[0].lastUpdated").isString());
        }

        @Test
        @DisplayName("Should return valid numeric types for stock levels")
        void responseDTO_ShouldHaveValidNumericTypes() throws Exception {
            // Given
            when(inventorySearchService.findByStockRange(MIN_STOCK, MAX_STOCK))
                    .thenReturn(List.of(firstInventoryDTO));

            // When
            ResultActions result = mockMvc.perform(get(BASE_URL + "/stock-range")
                    .param("minStock", MIN_STOCK.toString())
                    .param("maxStock", MAX_STOCK.toString()));

            // Then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].currentStock").isNumber())
                    .andExpect(jsonPath("$[0].id").isNumber())
                    .andExpect(jsonPath("$[0].productId").isNumber());
        }
    }
}
