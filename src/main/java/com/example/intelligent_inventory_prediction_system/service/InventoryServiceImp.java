package com.example.intelligent_inventory_prediction_system.service;

import com.example.intelligent_inventory_prediction_system.dto.response.InventoryResponseDTO;
import com.example.intelligent_inventory_prediction_system.mapper.request.InventoryRequestMapper;
import com.example.intelligent_inventory_prediction_system.mapper.response.InventoryResponseMapper;
import com.example.intelligent_inventory_prediction_system.model.Inventory;
import com.example.intelligent_inventory_prediction_system.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImp {
    private InventoryRepository inventoryRepository;
    private InventoryRequestMapper inventoryRequestMapper;
    private InventoryResponseMapper inventoryResponseMapper;

    public List<Inventory> findAllInventory(){
        log.info("findAllInventory");
        return inventoryRepository.findAll();
    }

    public Page<InventoryResponseDTO> findAllInventoryPage(Pageable pageable){
        log.info("findAllInventoryPage");
        Page<Inventory> inventoryPage = inventoryRepository.findAll(pageable);
        return inventoryPage.map(inventoryResponseMapper::toInventoryResponseDTO);
    }


}
