package com.example.intelligent_inventory_prediction_system.mapper.request;

import static org.junit.jupiter.api.Assertions.*;

import com.example.intelligent_inventory_prediction_system.dto.request.InventoryRequestDTO;
import com.example.intelligent_inventory_prediction_system.model.Inventory;
import com.example.intelligent_inventory_prediction_system.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;



class InventoryRequestMapperTest {
    private InventoryRequestMapper inventoryRequestMapper;

    @BeforeEach
    void setUp() {
        inventoryRequestMapper = Mappers.getMapper(InventoryRequestMapper.class);
    }

    private InventoryRequestDTO createSampleInventoryRequestDTO() {
        InventoryRequestDTO inventoryRequestDTO = new InventoryRequestDTO();
        inventoryRequestDTO.setProductId(1L);
        inventoryRequestDTO.setCurrentStock(100);
        inventoryRequestDTO.setLastUpdated(LocalDateTime.of(2023, 10, 1, 12, 0));
        return inventoryRequestDTO;
    }

    private Inventory createSampleInventory() {
        Inventory inventory = new Inventory();
        Product product = new Product();
        product.setId(1L);
        inventory.setProduct(product);
        inventory.setCurrentStock(100);
        inventory.setLastUpdated(LocalDateTime.of(2023, 10, 1, 12, 0));
        return inventory;
    }

    @Test
    void testMapInventoryRequestDTOToInventory() {
        InventoryRequestDTO requestDTO = createSampleInventoryRequestDTO();

        Inventory mappedInventory = inventoryRequestMapper.toInventory(requestDTO);

        assertAll(
                () -> assertNotNull(mappedInventory),
                () -> assertNull(mappedInventory.getId()),
                () -> assertNotNull(mappedInventory.getProduct()),
                () -> assertEquals(requestDTO.getProductId(), mappedInventory.getProduct().getId()),
                () -> assertEquals(requestDTO.getCurrentStock(), mappedInventory.getCurrentStock()),
                () -> assertEquals(requestDTO.getLastUpdated(), mappedInventory.getLastUpdated())
        );
    }

    @Test
    void testMapInventoryToInventoryRequestDTO() {
        Inventory inventory = createSampleInventory();

        InventoryRequestDTO mappedRequestDTO = inventoryRequestMapper.toInventoryRequestDTO(inventory);

        assertAll(
                () -> assertNotNull(mappedRequestDTO),
                () -> assertEquals(inventory.getProduct().getId(), mappedRequestDTO.getProductId()),
                () -> assertEquals(inventory.getCurrentStock(), mappedRequestDTO.getCurrentStock()),
                () -> assertEquals(inventory.getLastUpdated(), mappedRequestDTO.getLastUpdated())
        );
    }
}