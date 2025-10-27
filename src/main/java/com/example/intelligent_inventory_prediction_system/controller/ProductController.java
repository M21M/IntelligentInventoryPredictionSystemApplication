package com.example.intelligent_inventory_prediction_system.controller;

import com.example.intelligent_inventory_prediction_system.dto.request.ProductRequestDTO;
import com.example.intelligent_inventory_prediction_system.dto.response.ProductResponseDTO;
import com.example.intelligent_inventory_prediction_system.exception.ResourceNotFoundException;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import com.example.intelligent_inventory_prediction_system.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "APIs for managing product catalog and inventory")
public class ProductController {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private final ProductService productService;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get all products (paginated)",
            description = "Retrieve a paginated list of all products in the system"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved products",
            content = @Content(schema = @Schema(implementation = Page.class))
    )
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable
    ) {
        log.debug("getAllProducts page: {}", pageable.getPageNumber());
        Page<ProductResponseDTO> allProducts = productService.findAllProducts(pageable);
        log.info("getAllProducts returned {} items", allProducts.getContent().size());
        return ResponseEntity.ok(allProducts);
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get product by ID",
            description = "Retrieve detailed information about a specific product"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product found successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found with the given ID"
            )
    })
    public ResponseEntity<ProductResponseDTO> getProductById(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long id
    ) {
        log.debug("getProductById: {}", id);
        ProductResponseDTO responseDTO = productService.findProductById(id);

        if (responseDTO == null) {
            log.warn("Product with ID {} not found.", id);
            throw new ResourceNotFoundException("Product not found with id " + id);
        }

        log.info("getProductById returned: {}", responseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create new product",
            description = "Add a new product to the catalog"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or validation error"
            )
    })
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO productRequestDTO
    ) {
        log.debug("createProduct: {}", productRequestDTO);
        ProductResponseDTO responseDTO = productService.createProduct(productRequestDTO);
        log.info("createProduct success with id: {}", responseDTO.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Update product",
            description = "Update all fields of an existing product"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable long id,
            @Valid @RequestBody ProductRequestDTO productRequestDTO
    ) {
        log.debug("updateProduct id: {}, data: {}", id, productRequestDTO);
        ProductResponseDTO responseDTO = productService.updateProduct(id, productRequestDTO);
        log.info("updateProduct success for id: {}", id);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Update product status",
            description = "Partially update product by changing its status only"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product status updated successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    public ResponseEntity<ProductResponseDTO> updateProductStatus(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable long id,
            @Parameter(description = "New product status", required = true, example = "ACTIVE")
            @RequestParam ProductStatus status
    ) {
        log.debug("updateProductStatus id: {}, status: {}", id, status);
        ProductResponseDTO responseDTO = productService.updateProductStatus(id, status);
        log.info("updateProductStatus success for id: {}", id);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete product",
            description = "Remove a product from the catalog"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Product deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable Long id
    ) {
        log.info("Received request to delete product with id: {}", id);
        productService.deleteProduct(id);
        log.info("Successfully deleted product with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
