package com.example.intelligent_inventory_prediction_system.mapper.request;

import com.example.intelligent_inventory_prediction_system.dto.request.InventoryRequestDTO;
import com.example.intelligent_inventory_prediction_system.model.Inventory;
import com.example.intelligent_inventory_prediction_system.model.Product;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryRequestMapper {

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastUpdated", ignore = true)
    Inventory toInventory(InventoryRequestDTO inventoryRequestDTO);

    @Mapping(source = "product.id", target = "productId")
    InventoryRequestDTO toInventoryRequestDTO(Inventory inventory);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true)
    Inventory updateInventory(InventoryRequestDTO dto, @MappingTarget Inventory inventory);

    List<InventoryRequestDTO> toInventoryRequestDTOList(List<Inventory> inventories);

    @AfterMapping
    default void setProduct(@MappingTarget Inventory inventory, InventoryRequestDTO dto) {
        if (dto.getProductId() != null) {
            Product product = new Product();
            product.setId(dto.getProductId());
            inventory.setProduct(product);
        }
    }
}
