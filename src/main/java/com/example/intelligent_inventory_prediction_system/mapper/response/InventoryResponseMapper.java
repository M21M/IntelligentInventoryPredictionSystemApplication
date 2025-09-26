package com.example.intelligent_inventory_prediction_system.mapper.response;

import com.example.intelligent_inventory_prediction_system.dto.response.InventoryResponseDTO;
import com.example.intelligent_inventory_prediction_system.model.Inventory;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface InventoryResponseMapper {
    Inventory toInventory(InventoryResponseDTO inventoryResponseDTO);
    InventoryResponseDTO toInventoryResponseDTO(Inventory inventory);
}
