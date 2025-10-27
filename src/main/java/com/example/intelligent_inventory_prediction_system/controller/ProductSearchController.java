package com.example.intelligent_inventory_prediction_system.controller;

import com.example.intelligent_inventory_prediction_system.dto.response.ProductResponseDTO;
import com.example.intelligent_inventory_prediction_system.dto.search.ProductSearchCriteria;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import com.example.intelligent_inventory_prediction_system.service.ProductSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api/products/search")
@RequiredArgsConstructor
@Tag(name = "Product Search", description = "Advanced search and filtering APIs for products")
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    @GetMapping(value = "/advanced", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Advanced product search",
            description = "Search products using complex criteria with multiple filters"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved matching products",
            content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))
    )
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
            @RequestBody ProductSearchCriteria criteria
    ) {
        log.debug("Received advanced search request with criteria: {}", criteria);
        List<ProductResponseDTO> products = productSearchService.searchProducts(criteria);
        log.info("Returned {} products for advanced search", products.size());
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/keyword", produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Search by keyword",
            description = "Search products by keyword in name, description, or category"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved matching products"
    )
    public ResponseEntity<List<ProductResponseDTO>> findByKeyword(
            @Parameter(description = "Search keyword", required = true, example = "laptop")
            @RequestParam("value") String keyword
    ) {
        log.debug("Received search by keyword request: {}", keyword);
        List<ProductResponseDTO> products = productSearchService.findByKeyword(keyword);
        log.info("Returned {} products for keyword '{}'", products.size(), keyword);
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/name", produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Search by product name",
            description = "Find products matching a specific name (case-insensitive)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved matching products"
    )
    public ResponseEntity<List<ProductResponseDTO>> findByName(
            @Parameter(description = "Product name", required = true, example = "iPhone 15")
            @RequestParam("value") String name
    ) {
        log.debug("Received search by name request: {}", name);
        List<ProductResponseDTO> products = productSearchService.findByName(name);
        log.info("Returned {} products for name '{}'", products.size(), name);
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/category", produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Search by category",
            description = "Retrieve all products in a specific category"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved products in category"
    )
    public ResponseEntity<List<ProductResponseDTO>> findByCategory(
            @Parameter(description = "Category name", required = true, example = "Electronics")
            @RequestParam("value") String category
    ) {
        log.debug("Received search by category request: {}", category);
        List<ProductResponseDTO> products = productSearchService.findByCategory(category);
        log.info("Returned {} products for category '{}'", products.size(), category);
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/price-range", produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Search by price range",
            description = "Find products within a specific price range (min and max inclusive)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved products in price range"
    )
    public ResponseEntity<List<ProductResponseDTO>> findByPriceRange(
            @Parameter(description = "Minimum price", example = "100.0")
            @RequestParam(required = false) Double min,
            @Parameter(description = "Maximum price", example = "500.0")
            @RequestParam(required = false) Double max
    ) {
        log.debug("Received search by price range request: min={}, max={}", min, max);
        List<ProductResponseDTO> products = productSearchService.findByPriceRange(min, max);
        log.info("Returned {} products for price range {}-{}", products.size(), min, max);
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/status", produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Search by product status",
            description = "Filter products by their current status (ACTIVE, INACTIVE, etc.)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved products with specified status"
    )
    public ResponseEntity<List<ProductResponseDTO>> findByStatus(
            @Parameter(description = "Product status", required = true, example = "ACTIVE")
            @RequestParam("value") ProductStatus status
    ) {
        log.debug("Received search by status request: {}", status);
        List<ProductResponseDTO> products = productSearchService.findByStatus(status);
        log.info("Returned {} products for status '{}'", products.size(), status);
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/available-above", produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Find available products above price",
            description = "Get all available (in-stock) products priced above a minimum value"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved available products"
    )
    public ResponseEntity<List<ProductResponseDTO>> findAvailableProductsAbovePrice(
            @Parameter(description = "Minimum price threshold", required = true, example = "200.0")
            @RequestParam("price") Double minPrice
    ) {
        log.debug("Received search for available products above price: {}", minPrice);
        List<ProductResponseDTO> products = productSearchService.findAvailableProductsAbovePrice(minPrice);
        log.info("Returned {} available products above price {}", products.size(), minPrice);
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "/active", produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get all active products",
            description = "Retrieve all products with ACTIVE status"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved active products"
    )
    public ResponseEntity<List<ProductResponseDTO>> findActiveProducts() {
        log.debug("Received search for active products");
        List<ProductResponseDTO> products = productSearchService.findActiveProducts();
        log.info("Returned {} active products", products.size());
        return ResponseEntity.ok(products);
    }
}
