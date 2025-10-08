package com.example.intelligent_inventory_prediction_system.service.executor;

import com.example.intelligent_inventory_prediction_system.dto.response.InventoryResponseDTO;
import com.example.intelligent_inventory_prediction_system.dto.search.InventorySearchCriteria;
import com.example.intelligent_inventory_prediction_system.mapper.response.InventoryResponseMapper;
import com.example.intelligent_inventory_prediction_system.model.Inventory;
import com.example.intelligent_inventory_prediction_system.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryQueryExecutor {

    private final InventoryRepository inventoryRepository;
    private final InventoryResponseMapper inventoryResponseMapper;

    public List<InventoryResponseDTO> executeSpecificationQuery(Specification<Inventory> specification, String operationDescription) {
        log.debug("Executing query: {}", operationDescription);
        List<Inventory> inventories = inventoryRepository.findAll(specification);
        log.debug("Found {} inventories for operation: {}", inventories.size(), operationDescription);
        return inventoryResponseMapper.toInventoryResponseDTOList(inventories);
    }

    public Page<InventoryResponseDTO> executePagedQuery(Pageable pageable, String operationDescription) {
        log.debug("Executing paged query: {} with pagination: {}", operationDescription, pageable);
        Page<Inventory> inventories = inventoryRepository.findAll(pageable);
        log.debug("Found {} inventories for paged operation: {}", inventories.getTotalElements(), operationDescription);
        return inventories.map(inventoryResponseMapper::toInventoryResponseDTO);
    }

    public List<InventoryResponseDTO> executeSimpleQuery(String operationDescription) {
        log.debug("Executing simple query: {}", operationDescription);
        List<Inventory> inventories = inventoryRepository.findAll();
        log.debug("Found {} inventories for operation: {}", inventories.size(), operationDescription);
        return inventoryResponseMapper.toInventoryResponseDTOList(inventories);
    }


}
