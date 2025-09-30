package com.example.intelligent_inventory_prediction_system.service;

import com.example.intelligent_inventory_prediction_system.dto.request.ProductRequestDTO;
import com.example.intelligent_inventory_prediction_system.dto.response.ProductResponseDTO;
import com.example.intelligent_inventory_prediction_system.mapper.request.ProductRequestMapper;
import com.example.intelligent_inventory_prediction_system.mapper.response.ProductResponseMapper;
import com.example.intelligent_inventory_prediction_system.model.Product;
import com.example.intelligent_inventory_prediction_system.model.ProductStatus;
import com.example.intelligent_inventory_prediction_system.repository.ProductRepository;
import com.example.intelligent_inventory_prediction_system.service.executor.ProductQueryExecutor;
import com.example.intelligent_inventory_prediction_system.validator.ProductValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductRequestMapper productRequestMapper;

    @Mock
    private ProductResponseMapper productResponseMapper;

    @Mock
    private ProductValidator productValidator;

    @Mock
    private ProductQueryExecutor productQueryExecutor;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequestDTO productRequestDTO;
    private ProductResponseDTO productResponseDTO;
    private static final Long PRODUCT_ID = 1L;
    private static final String PRODUCT_NAME = "Test Product";

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);
        product.setStatus(ProductStatus.AVAILABLE);

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName(PRODUCT_NAME);

        productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(PRODUCT_ID);
        productResponseDTO.setName(PRODUCT_NAME);
    }

    @Test
    void testFindAllProducts_ShouldReturnListOfProducts() {
        List<ProductResponseDTO> expectedProducts = Arrays.asList(productResponseDTO);
        doReturn(expectedProducts).when(productQueryExecutor).executeSimpleQuery(anyString());

        List<ProductResponseDTO> result = productService.findAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productResponseDTO, result.get(0));
        verify(productQueryExecutor).executeSimpleQuery("find all products");
    }

    @Test
    void testFindAllProductsWithPageable_ShouldReturnPageOfProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductResponseDTO> expectedPage = new PageImpl<>(Arrays.asList(productResponseDTO));
        doReturn(expectedPage).when(productQueryExecutor).executePagedQuery(any(Pageable.class), anyString());

        Page<ProductResponseDTO> result = productService.findAllProducts(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(productResponseDTO, result.getContent().get(0));
        verify(productQueryExecutor).executePagedQuery(eq(pageable), eq("find all products with pagination"));
    }

    @Test
    void testFindProductById_WithValidId_ShouldReturnProduct() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productResponseMapper.toProductResponseDTO(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.findProductById(PRODUCT_ID);

        assertNotNull(result);
        assertEquals(PRODUCT_ID, result.getId());
        assertEquals(PRODUCT_NAME, result.getName());
        verify(productValidator).validateId(PRODUCT_ID);
        verify(productRepository).findById(PRODUCT_ID);
        verify(productResponseMapper).toProductResponseDTO(product);
    }

    @Test
    void testFindProductById_WithNonExistentId_ShouldThrowException() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.findProductById(PRODUCT_ID)
        );

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(productValidator).validateId(PRODUCT_ID);
        verify(productRepository).findById(PRODUCT_ID);
        verify(productResponseMapper, never()).toProductResponseDTO(any());
    }

    @Test
    void testCreateProduct_WithValidRequest_ShouldCreateAndReturnProduct() {
        when(productRequestMapper.toProduct(productRequestDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productResponseMapper.toProductResponseDTO(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        assertNotNull(result);
        assertEquals(productResponseDTO, result);
        verify(productValidator).validateCreateRequest(productRequestDTO);
        verify(productRequestMapper).toProduct(productRequestDTO);
        verify(productRepository).save(product);
        verify(productResponseMapper).toProductResponseDTO(product);
    }

    @Test
    void testUpdateProduct_WithValidIdAndRequest_ShouldUpdateAndReturnProduct() {
        Product updatedProduct = new Product();
        updatedProduct.setId(PRODUCT_ID);
        updatedProduct.setName("Updated Product");

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productRequestMapper.updateProduct(productRequestDTO, product)).thenReturn(updatedProduct);
        when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
        when(productResponseMapper.toProductResponseDTO(updatedProduct)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.updateProduct(PRODUCT_ID, productRequestDTO);

        assertNotNull(result);
        assertEquals(productResponseDTO, result);
        verify(productValidator).validateId(PRODUCT_ID);
        verify(productValidator).validateUpdateRequest(productRequestDTO);
        verify(productRepository).findById(PRODUCT_ID);
        verify(productRequestMapper).updateProduct(productRequestDTO, product);
        verify(productRepository).save(updatedProduct);
        verify(productResponseMapper).toProductResponseDTO(updatedProduct);
    }

    @Test
    void testUpdateProduct_WithNonExistentId_ShouldThrowException() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateProduct(PRODUCT_ID, productRequestDTO)
        );

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(productValidator).validateId(PRODUCT_ID);
        verify(productValidator).validateUpdateRequest(productRequestDTO);
        verify(productRepository).findById(PRODUCT_ID);
        verify(productRequestMapper, never()).updateProduct(any(), any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void testDeleteProduct_WithValidId_ShouldDeleteProduct() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        assertDoesNotThrow(() -> productService.deleteProduct(PRODUCT_ID));

        verify(productValidator).validateId(PRODUCT_ID);
        verify(productRepository).findById(PRODUCT_ID);
        verify(productRepository).deleteById(PRODUCT_ID);
    }

    @Test
    void testDeleteProduct_WithNonExistentId_ShouldThrowException() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.deleteProduct(PRODUCT_ID)
        );

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(productValidator).validateId(PRODUCT_ID);
        verify(productRepository).findById(PRODUCT_ID);
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void testUpdateProductStatus_WithValidIdAndStatus_ShouldUpdateAndReturnProduct() {
        ProductStatus newStatus = ProductStatus.NOT_AVAILABLE;
        product.setStatus(newStatus);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productResponseMapper.toProductResponseDTO(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.updateProductStatus(PRODUCT_ID, newStatus);

        assertNotNull(result);
        assertEquals(productResponseDTO, result);
        verify(productValidator).validateId(PRODUCT_ID);
        verify(productValidator).validateStatus(newStatus);
        verify(productRepository).findById(PRODUCT_ID);
        verify(productRepository).save(product);
        verify(productResponseMapper).toProductResponseDTO(product);
    }

    @Test
    void testUpdateProductStatus_WithNonExistentId_ShouldThrowException() {
        ProductStatus newStatus = ProductStatus.NOT_AVAILABLE;
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateProductStatus(PRODUCT_ID, newStatus)
        );

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(productValidator).validateId(PRODUCT_ID);
        verify(productValidator).validateStatus(newStatus);
        verify(productRepository).findById(PRODUCT_ID);
        verify(productRepository, never()).save(any());
    }
}
