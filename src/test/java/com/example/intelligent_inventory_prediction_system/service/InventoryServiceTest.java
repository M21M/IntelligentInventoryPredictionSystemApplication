package com.example.intelligent_inventory_prediction_system.service;

import com.example.intelligent_inventory_prediction_system.dto.request.InventoryRequestDTO;
import com.example.intelligent_inventory_prediction_system.dto.response.InventoryResponseDTO;
import com.example.intelligent_inventory_prediction_system.mapper.request.InventoryRequestMapper;
import com.example.intelligent_inventory_prediction_system.mapper.response.InventoryResponseMapper;
import com.example.intelligent_inventory_prediction_system.model.Inventory;
import com.example.intelligent_inventory_prediction_system.model.Product;
import com.example.intelligent_inventory_prediction_system.repository.InventoryRepository;
import com.example.intelligent_inventory_prediction_system.repository.ProductRepository;
import com.example.intelligent_inventory_prediction_system.service.executor.InventoryQueryExecutor;
import com.example.intelligent_inventory_prediction_system.validator.InventoryValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRequestMapper inventoryRequestMapper;

    @Mock
    private InventoryResponseMapper inventoryResponseMapper;

    @Mock
    private InventoryValidator inventoryValidator;

    @Mock
    private InventoryQueryExecutor queryExecutor;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inventory;
    private InventoryRequestDTO inventoryRequestDTO;
    private InventoryResponseDTO inventoryResponseDTO;
    private Product product;
    private static final Long INVENTORY_ID = 1L;
    private static final Long PRODUCT_ID = 100L;
    private static final Integer CURRENT_STOCK = 50;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(PRODUCT_ID);

        inventory = new Inventory();
        inventory.setId(INVENTORY_ID);
        inventory.setProduct(product);
        inventory.setCurrentStock(CURRENT_STOCK);
        inventory.setLastUpdated(LocalDateTime.now());

        inventoryRequestDTO = new InventoryRequestDTO();
        inventoryRequestDTO.setProductId(PRODUCT_ID);
        inventoryRequestDTO.setCurrentStock(CURRENT_STOCK);

        inventoryResponseDTO = new InventoryResponseDTO();
        inventoryResponseDTO.setId(INVENTORY_ID);
        inventoryResponseDTO.setProductId(PRODUCT_ID);
        inventoryResponseDTO.setCurrentStock(CURRENT_STOCK);
        inventoryResponseDTO.setLastUpdated(LocalDateTime.now());
    }

    @Test
    void testFindAllInventories_ShouldReturnListOfInventories() {
        List<InventoryResponseDTO> expectedInventories = Arrays.asList(inventoryResponseDTO);
        doReturn(expectedInventories).when(queryExecutor).executeSimpleQuery(anyString());

        List<InventoryResponseDTO> result = inventoryService.findAllInventories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(inventoryResponseDTO, result.get(0));
        verify(queryExecutor).executeSimpleQuery("find all inventories");
    }

    @Test
    void testFindAllInventoriesWithPageable_ShouldReturnPageOfInventories() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<InventoryResponseDTO> expectedPage = new PageImpl<>(Arrays.asList(inventoryResponseDTO));
        doReturn(expectedPage).when(queryExecutor).executePagedQuery(any(Pageable.class), anyString());

        Page<InventoryResponseDTO> result = inventoryService.findAllInventories(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(inventoryResponseDTO, result.getContent().get(0));
        verify(queryExecutor).executePagedQuery(eq(pageable), eq("find all inventories with pagination"));
    }

    @Test
    void testFindInventoryById_WithValidId_ShouldReturnInventory() {
        when(inventoryRepository.findById(INVENTORY_ID)).thenReturn(Optional.of(inventory));
        when(inventoryResponseMapper.toInventoryResponseDTO(inventory)).thenReturn(inventoryResponseDTO);

        InventoryResponseDTO result = inventoryService.findInventoryById(INVENTORY_ID);

        assertNotNull(result);
        assertEquals(INVENTORY_ID, result.getId());
        assertEquals(PRODUCT_ID, result.getProductId());
        assertEquals(CURRENT_STOCK, result.getCurrentStock());
        verify(inventoryValidator).validateId(INVENTORY_ID);
        verify(inventoryRepository).findById(INVENTORY_ID);
        verify(inventoryResponseMapper).toInventoryResponseDTO(inventory);
    }

    @Test
    void testFindInventoryById_WithNonExistentId_ShouldThrowException() {
        when(inventoryRepository.findById(INVENTORY_ID)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.findInventoryById(INVENTORY_ID)
        );

        assertTrue(exception.getMessage().contains("Inventory not found"));
        verify(inventoryValidator).validateId(INVENTORY_ID);
        verify(inventoryRepository).findById(INVENTORY_ID);
        verify(inventoryResponseMapper, never()).toInventoryResponseDTO(any());
    }

    @Test
    void testCreateInventory_WithValidRequest_ShouldCreateAndReturnInventory() {
        when(productRepository.existsById(PRODUCT_ID)).thenReturn(true);
        when(inventoryRequestMapper.toInventory(inventoryRequestDTO)).thenReturn(inventory);
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        when(inventoryResponseMapper.toInventoryResponseDTO(inventory)).thenReturn(inventoryResponseDTO);

        InventoryResponseDTO result = inventoryService.createInventory(inventoryRequestDTO);

        assertNotNull(result);
        assertEquals(inventoryResponseDTO, result);
        verify(inventoryValidator).validateCreateRequest(inventoryRequestDTO);
        verify(productRepository).existsById(PRODUCT_ID);
        verify(inventoryRequestMapper).toInventory(inventoryRequestDTO);
        verify(inventoryRepository).save(inventory);
        verify(inventoryResponseMapper).toInventoryResponseDTO(inventory);
    }

    @Test
    void testCreateInventory_WithNonExistentProduct_ShouldThrowException() {
        when(productRepository.existsById(PRODUCT_ID)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.createInventory(inventoryRequestDTO)
        );

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(inventoryValidator).validateCreateRequest(inventoryRequestDTO);
        verify(productRepository).existsById(PRODUCT_ID);
        verify(inventoryRequestMapper, never()).toInventory(any());
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void testUpdateInventory_WithValidIdAndRequest_ShouldUpdateAndReturnInventory() {
        InventoryRequestDTO updateRequest = new InventoryRequestDTO();
        updateRequest.setCurrentStock(75);

        when(inventoryRepository.findById(INVENTORY_ID)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        when(inventoryResponseMapper.toInventoryResponseDTO(inventory)).thenReturn(inventoryResponseDTO);

        InventoryResponseDTO result = inventoryService.updateInventory(INVENTORY_ID, updateRequest);

        assertNotNull(result);
        assertEquals(inventoryResponseDTO, result);
        verify(inventoryValidator).validateId(INVENTORY_ID);
        verify(inventoryValidator).validateUpdateRequest(updateRequest);
        verify(inventoryRepository).findById(INVENTORY_ID);
        verify(inventoryRepository).save(inventory);
        verify(inventoryResponseMapper).toInventoryResponseDTO(inventory);
    }

    @Test
    void testUpdateInventory_WithProductIdChange_ShouldVerifyNewProduct() {
        Long newProductId = 200L;
        InventoryRequestDTO updateRequest = new InventoryRequestDTO();
        updateRequest.setProductId(newProductId);
        updateRequest.setCurrentStock(75);

        when(inventoryRepository.findById(INVENTORY_ID)).thenReturn(Optional.of(inventory));
        when(productRepository.existsById(newProductId)).thenReturn(true);
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        when(inventoryResponseMapper.toInventoryResponseDTO(inventory)).thenReturn(inventoryResponseDTO);

        InventoryResponseDTO result = inventoryService.updateInventory(INVENTORY_ID, updateRequest);

        assertNotNull(result);
        verify(inventoryValidator).validateId(INVENTORY_ID);
        verify(inventoryValidator).validateUpdateRequest(updateRequest);
        verify(productRepository).existsById(newProductId);
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void testUpdateInventory_WithNonExistentId_ShouldThrowException() {
        when(inventoryRepository.findById(INVENTORY_ID)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.updateInventory(INVENTORY_ID, inventoryRequestDTO)
        );

        assertTrue(exception.getMessage().contains("Inventory not found"));
        verify(inventoryValidator).validateId(INVENTORY_ID);
        verify(inventoryValidator).validateUpdateRequest(inventoryRequestDTO);
        verify(inventoryRepository).findById(INVENTORY_ID);
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void testDeleteInventory_WithValidId_ShouldDeleteInventory() {
        when(inventoryRepository.findById(INVENTORY_ID)).thenReturn(Optional.of(inventory));

        assertDoesNotThrow(() -> inventoryService.deleteInventory(INVENTORY_ID));

        verify(inventoryValidator).validateId(INVENTORY_ID);
        verify(inventoryRepository).findById(INVENTORY_ID);
        verify(inventoryRepository).deleteById(INVENTORY_ID);
    }

    @Test
    void testDeleteInventory_WithNonExistentId_ShouldThrowException() {
        when(inventoryRepository.findById(INVENTORY_ID)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.deleteInventory(INVENTORY_ID)
        );

        assertTrue(exception.getMessage().contains("Inventory not found"));
        verify(inventoryValidator).validateId(INVENTORY_ID);
        verify(inventoryRepository).findById(INVENTORY_ID);
        verify(inventoryRepository, never()).deleteById(any());
    }

    @Test
    void testUpdateStockLevel_WithValidIdAndStock_ShouldUpdateAndReturnInventory() {
        Integer newStockLevel = 100;

        when(inventoryRepository.findById(INVENTORY_ID)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        when(inventoryResponseMapper.toInventoryResponseDTO(inventory)).thenReturn(inventoryResponseDTO);

        InventoryResponseDTO result = inventoryService.updateStockLevel(INVENTORY_ID, newStockLevel);

        assertNotNull(result);
        assertEquals(inventoryResponseDTO, result);
        verify(inventoryValidator).validateId(INVENTORY_ID);
        verify(inventoryRepository).findById(INVENTORY_ID);
        verify(inventoryRepository).save(inventory);
        verify(inventoryResponseMapper).toInventoryResponseDTO(inventory);
    }

    @Test
    void testUpdateStockLevel_WithNullStock_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.updateStockLevel(INVENTORY_ID, null)
        );

        assertTrue(exception.getMessage().contains("Stock level cannot be null"));
        verify(inventoryValidator).validateId(INVENTORY_ID);
    }

    @Test
    void testUpdateStockLevel_WithNonExistentId_ShouldThrowException() {
        Integer newStockLevel = 100;
        when(inventoryRepository.findById(INVENTORY_ID)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.updateStockLevel(INVENTORY_ID, newStockLevel)
        );

        assertTrue(exception.getMessage().contains("Inventory not found"));
        verify(inventoryValidator).validateId(INVENTORY_ID);
        verify(inventoryRepository).findById(INVENTORY_ID);
        verify(inventoryRepository, never()).save(any());
    }
}
