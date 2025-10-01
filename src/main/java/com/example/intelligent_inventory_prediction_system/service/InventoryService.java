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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.intelligent_inventory_prediction_system.constants.InventoryConstant.INVENTORY_NOT_FOUND_MESSAGE;
import static com.example.intelligent_inventory_prediction_system.constants.ProductConstants.PRODUCT_NOT_FOUND_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final InventoryRequestMapper inventoryRequestMapper;
    private final InventoryResponseMapper inventoryResponseMapper;
    private final InventoryValidator inventoryValidator;
    private final InventoryQueryExecutor queryExecutor;

    public List<InventoryResponseDTO> findAllInventories() {
        return queryExecutor.executeSimpleQuery("find all inventories");
    }

    public Page<InventoryResponseDTO> findAllInventories(Pageable pageable) {
        return queryExecutor.executePagedQuery(pageable, "find all inventories with pagination");
    }

    public InventoryResponseDTO findInventoryById(Long id) {
        log.debug("Finding inventory by id: {}", id);
        inventoryValidator.validateId(id);
        Inventory inventory = getInventoryByIdOrThrow(id);
        return inventoryResponseMapper.toInventoryResponseDTO(inventory);
    }

    @Transactional
    public InventoryResponseDTO createInventory(InventoryRequestDTO inventoryRequestDTO) {
        log.debug("Creating new inventory: {}", inventoryRequestDTO);
        inventoryValidator.validateCreateRequest(inventoryRequestDTO);
        verifyProductExists(inventoryRequestDTO.getProductId());

        Inventory inventory = inventoryRequestMapper.toInventory(inventoryRequestDTO);
        inventory.setLastUpdated(LocalDateTime.now());
        Inventory savedInventory = saveInventory(inventory);

        log.info("Inventory created successfully with id: {}", savedInventory.getId());
        return inventoryResponseMapper.toInventoryResponseDTO(savedInventory);
    }

    @Transactional
    public InventoryResponseDTO updateInventory(Long id, InventoryRequestDTO inventoryRequestDTO) {
        log.debug("Updating inventory with id: {}", id);
        inventoryValidator.validateId(id);
        inventoryValidator.validateUpdateRequest(inventoryRequestDTO);

        Inventory existingInventory = getInventoryByIdOrThrow(id);

        if (hasProductChanged(inventoryRequestDTO, existingInventory)) {
            verifyProductExists(inventoryRequestDTO.getProductId());
            Product product = new Product();
            product.setId(inventoryRequestDTO.getProductId());
            existingInventory.setProduct(product);
        }

        updateInventoryFields(existingInventory, inventoryRequestDTO);
        existingInventory.setLastUpdated(LocalDateTime.now());
        Inventory savedInventory = saveInventory(existingInventory);

        log.info("Inventory updated successfully with id: {}", id);
        return inventoryResponseMapper.toInventoryResponseDTO(savedInventory);
    }

    @Transactional
    public void deleteInventory(Long id) {
        log.debug("Deleting inventory with id: {}", id);
        inventoryValidator.validateId(id);
        Inventory inventory = getInventoryByIdOrThrow(id);

        inventoryRepository.deleteById(id);
        log.info("Inventory deleted successfully with id: {}", id);
    }

    @Transactional
    public InventoryResponseDTO updateStockLevel(Long id, Integer newStockLevel) {
        log.debug("Updating stock level for inventory id: {} to: {}", id, newStockLevel);
        inventoryValidator.validateId(id);

        if (newStockLevel == null) {
            throw new IllegalArgumentException("Stock level cannot be null");
        }

        Inventory inventory = getInventoryByIdOrThrow(id);
        inventory.setCurrentStock(newStockLevel);
        inventory.setLastUpdated(LocalDateTime.now());
        Inventory savedInventory = saveInventory(inventory);

        log.info("Stock level updated successfully for inventory id: {}", id);
        return inventoryResponseMapper.toInventoryResponseDTO(savedInventory);
    }

    private Inventory getInventoryByIdOrThrow(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(INVENTORY_NOT_FOUND_MESSAGE + id));
    }

    private void verifyProductExists(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException(PRODUCT_NOT_FOUND_MESSAGE + productId);
        }
    }

    private boolean hasProductChanged(InventoryRequestDTO request, Inventory inventory) {
        return request.getProductId() != null &&
                !request.getProductId().equals(inventory.getProduct().getId());
    }

    private void updateInventoryFields(Inventory inventory, InventoryRequestDTO request) {
        if (request.getCurrentStock() != null) {
            inventory.setCurrentStock(request.getCurrentStock());
        }
    }

    private Inventory saveInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }
}
