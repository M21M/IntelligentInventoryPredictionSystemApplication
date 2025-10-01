package com.example.intelligent_inventory_prediction_system.mapper.response;

import com.example.intelligent_inventory_prediction_system.dto.response.InventoryResponseDTO;
import com.example.intelligent_inventory_prediction_system.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryResponseMapper {

    @Mapping(source = "product.id", target = "productId")
    InventoryResponseDTO toInventoryResponseDTO(Inventory inventory);

    @Mapping(source = "productId", target = "product.id")
    Inventory toInventory(InventoryResponseDTO inventoryResponseDTO);

    List<InventoryResponseDTO> toInventoryResponseDTOList(List<Inventory> inventories);
}
