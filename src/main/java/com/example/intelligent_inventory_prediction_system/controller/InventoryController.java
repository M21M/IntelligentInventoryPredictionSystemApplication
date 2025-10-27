package com.example.intelligent_inventory_prediction_system.controller;

import com.example.intelligent_inventory_prediction_system.dto.request.InventoryRequestDTO;
import com.example.intelligent_inventory_prediction_system.dto.response.InventoryResponseDTO;
import com.example.intelligent_inventory_prediction_system.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
@Tag(name = "Inventory Management", description = "APIs for managing inventory stock levels")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get all inventories",
            description = "Retrieve a list of all inventory records without pagination"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved inventory list",
            content = @Content(schema = @Schema(implementation = InventoryResponseDTO.class))
    )
    public ResponseEntity<List<InventoryResponseDTO>> getAllInventories() {
        log.info("Received request to fetch all inventories");
        List<InventoryResponseDTO> inventories = inventoryService.findAllInventories();
        log.debug("Retrieved {} inventories", inventories.size());
        return ResponseEntity.ok(inventories);
    }

    @GetMapping(value = "/paged", produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get paginated inventories",
            description = "Retrieve inventory records with pagination support"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved paginated inventory list"
    )
    public ResponseEntity<Page<InventoryResponseDTO>> getInventoriesPageable(
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        log.info("Received request to fetch inventories with pagination: {}", pageable);
        Page<InventoryResponseDTO> inventoryPage = inventoryService.findAllInventories(pageable);
        log.debug("Retrieved page {} of {} with {} items",
                inventoryPage.getNumber(),
                inventoryPage.getTotalPages(),
                inventoryPage.getNumberOfElements());
        return ResponseEntity.ok(inventoryPage);
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get inventory by ID",
            description = "Retrieve a specific inventory record by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved inventory",
                    content = @Content(schema = @Schema(implementation = InventoryResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Inventory not found"
            )
    })
    public ResponseEntity<InventoryResponseDTO> getInventoryById(
            @Parameter(description = "Inventory ID", required = true, example = "1")
            @PathVariable @Min(1) Long id
    ) {
        log.info("Received request to fetch inventory with id: {}", id);
        InventoryResponseDTO inventory = inventoryService.findInventoryById(id);
        return ResponseEntity.ok(inventory);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create new inventory",
            description = "Create a new inventory record for a product"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Inventory created successfully",
                    content = @Content(schema = @Schema(implementation = InventoryResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    public ResponseEntity<InventoryResponseDTO> createInventory(
            @Valid @RequestBody InventoryRequestDTO request
    ) {
        log.info("Received request to create inventory: {}", request);
        InventoryResponseDTO createdInventory = inventoryService.createInventory(request);
        log.info("Successfully created inventory with id: {}", createdInventory.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInventory);
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Update inventory",
            description = "Update an existing inventory record"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inventory updated successfully",
                    content = @Content(schema = @Schema(implementation = InventoryResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Inventory not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    public ResponseEntity<InventoryResponseDTO> updateInventory(
            @Parameter(description = "Inventory ID", required = true, example = "1")
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody InventoryRequestDTO request
    ) {
        log.info("Received request to update inventory with id: {}", id);
        InventoryResponseDTO updatedInventory = inventoryService.updateInventory(id, request);
        log.info("Successfully updated inventory with id: {}", id);
        return ResponseEntity.ok(updatedInventory);
    }

    @PatchMapping(value = "/{id}/stock", produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Update stock level",
            description = "Update only the stock level of an inventory record"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stock level updated successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Inventory not found"
            )
    })
    public ResponseEntity<InventoryResponseDTO> updateStockLevel(
            @Parameter(description = "Inventory ID", required = true, example = "1")
            @PathVariable @Min(1) Long id,
            @Parameter(description = "New stock level", required = true, example = "150")
            @RequestParam @Min(0) Integer stockLevel
    ) {
        log.info("Received request to update stock level for inventory id: {} to: {}", id, stockLevel);
        InventoryResponseDTO updatedInventory = inventoryService.updateStockLevel(id, stockLevel);
        log.info("Successfully updated stock level for inventory id: {}", id);
        return ResponseEntity.ok(updatedInventory);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete inventory",
            description = "Delete an inventory record by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Inventory deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Inventory not found"
            )
    })
    public ResponseEntity<Void> deleteInventory(
            @Parameter(description = "Inventory ID", required = true, example = "1")
            @PathVariable @Min(1) Long id
    ) {
        log.info("Received request to delete inventory with id: {}", id);
        inventoryService.deleteInventory(id);
        log.info("Successfully deleted inventory with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
