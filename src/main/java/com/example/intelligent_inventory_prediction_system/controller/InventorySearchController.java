package com.example.intelligent_inventory_prediction_system.controller;

import com.example.intelligent_inventory_prediction_system.dto.response.InventoryResponseDTO;
import com.example.intelligent_inventory_prediction_system.dto.search.InventorySearchCriteria;
import com.example.intelligent_inventory_prediction_system.service.InventorySearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/inventory/search")
@RequiredArgsConstructor
@Tag(name = "Inventory Search", description = "Advanced search operations for inventory management")
public class InventorySearchController {

    private final InventorySearchService inventorySearchService;

    @PostMapping("/advanced")
    @Operation(
            summary = "Advanced inventory search",
            description = "Search inventory using multiple criteria including product ID and stock levels"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved matching inventory records",
                    content = @Content(schema = @Schema(implementation = InventoryResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search criteria provided",
                    content = @Content
            )
    })
    public ResponseEntity<List<InventoryResponseDTO>> searchInventories(
            @RequestBody InventorySearchCriteria criteria) {
        log.debug("Received advanced inventory search request with criteria: {}", criteria);
        List<InventoryResponseDTO> inventoryList = inventorySearchService.searchInventories(criteria);
        log.info("Returned {} inventory records for advanced search.", inventoryList.size());
        return new ResponseEntity<>(inventoryList, HttpStatus.OK);
    }

    @GetMapping("/product/{productId}")
    @Operation(
            summary = "Find inventory by product ID",
            description = "Retrieve all inventory records for a specific product"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved inventory records for the product",
                    content = @Content(schema = @Schema(implementation = InventoryResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            )
    })
    public ResponseEntity<List<InventoryResponseDTO>> findByProductId(
            @Parameter(description = "Product ID to search for", required = true)
            @PathVariable Long productId) {
        log.debug("Received search by product ID request: {}", productId);
        List<InventoryResponseDTO> inventoryList = inventorySearchService.findByProductId(productId);
        log.info("Returned {} inventory records for product ID: {}", inventoryList.size(), productId);
        return new ResponseEntity<>(inventoryList, HttpStatus.OK);
    }

    @GetMapping("/stock-range")
    @Operation(
            summary = "Find inventory within stock range",
            description = "Retrieve inventory records where stock levels fall within the specified range (inclusive)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved inventory within stock range",
                    content = @Content(schema = @Schema(implementation = InventoryResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid range parameters",
                    content = @Content
            )
    })
    public ResponseEntity<List<InventoryResponseDTO>> findByStockRange(
            @Parameter(description = "Minimum stock level (optional)", example = "5")
            @RequestParam(required = false) Integer minStock,
            @Parameter(description = "Maximum stock level (optional)", example = "100")
            @RequestParam(required = false) Integer maxStock) {
        log.debug("Received stock range search request: minStock={}, maxStock={}", minStock, maxStock);
        List<InventoryResponseDTO> inventoryList = inventorySearchService.findByStockRange(minStock, maxStock);
        log.info("Returned {} inventory records for stock range {}-{}.", inventoryList.size(), minStock, maxStock);
        return new ResponseEntity<>(inventoryList, HttpStatus.OK);
    }

    @GetMapping("/minimum-stock")
    @Operation(
            summary = "Find inventory above minimum stock",
            description = "Retrieve inventory records where current stock is greater than the specified minimum (exclusive)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved inventory above minimum stock",
                    content = @Content(schema = @Schema(implementation = InventoryResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid minimum stock value",
                    content = @Content
            )
    })
    public ResponseEntity<List<InventoryResponseDTO>> findByMinimumStock(
            @Parameter(description = "Minimum stock threshold (exclusive)", required = true, example = "10")
            @RequestParam Integer minStock) {
        log.debug("Received minimum stock search request: minStock={}", minStock);
        List<InventoryResponseDTO> inventoryList = inventorySearchService.findByMinimumStock(minStock);
        log.info("Returned {} inventory records with stock greater than {}.", inventoryList.size(), minStock);
        return new ResponseEntity<>(inventoryList, HttpStatus.OK);
    }
}
