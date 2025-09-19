package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.CategoryRepository;
import com.cognizant.ecommerce.dao.ProductRepository;
import com.cognizant.ecommerce.dto.product.ProductRequestDTO;
import com.cognizant.ecommerce.dto.product.ProductResponseDTO;
import com.cognizant.ecommerce.exception.DuplicateResourceException;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.Category;
import com.cognizant.ecommerce.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequestDTO requestDTO;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestDTO = new ProductRequestDTO();
        requestDTO.setName("Bluetooth Headphones");
        requestDTO.setDescription("Noise-cancelling over-ear headphones");
        requestDTO.setPrice(BigDecimal.valueOf(2999.99));
        requestDTO.setImage_url("https://example.com/images/headphones.jpg");
        requestDTO.setQuantity(10L);
        requestDTO.setCategoryId(1L);

        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
    }

    @Test
    void testCreateProduct_Success() {
        when(productRepository.findByName("Bluetooth Headphones")).thenReturn(Optional.empty());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDTO response = productService.createProduct(requestDTO);

        assertNotNull(response);
        assertEquals("Bluetooth Headphones", response.getName());
        assertEquals(10L, response.getQuantity());
        assertTrue(response.isActive());
        assertEquals(1L, response.getCategory_id());
    }

    @Test
    void testCreateProduct_DuplicateName_ThrowsException() {
        when(productRepository.findByName("Bluetooth Headphones")).thenReturn(Optional.of(new Product()));
        assertThrows(DuplicateResourceException.class, () -> productService.createProduct(requestDTO));
    }

    @Test
    void testCreateProduct_CategoryNotFound_ThrowsException() {
        when(productRepository.findByName("Bluetooth Headphones")).thenReturn(Optional.empty());
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(requestDTO));
    }

    @Test
    void testCreateProduct_ZeroQuantity_SetsInactive() {
        requestDTO.setQuantity(0L);
        when(productRepository.findByName("Bluetooth Headphones")).thenReturn(Optional.empty());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDTO response = productService.createProduct(requestDTO);
        assertFalse(response.isActive());
    }

    @Test
    void testUpdateProduct_Success() {
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Old Name");
        existingProduct.setQuantity(5L);
        existingProduct.setCategory(category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.findByName("Bluetooth Headphones")).thenReturn(Optional.empty());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDTO response = productService.updateProduct(1L, requestDTO);
        assertEquals("Bluetooth Headphones", response.getName());
        assertEquals(10L, response.getQuantity());
        assertTrue(response.isActive());
    }

    @Test
    void testUpdateProduct_DuplicateName_ThrowsException() {
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Old Name");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.findByName("Bluetooth Headphones")).thenReturn(Optional.of(new Product()));

        assertThrows(DuplicateResourceException.class, () -> productService.updateProduct(1L, requestDTO));
    }

    @Test
    void testUpdateProduct_CategoryNotFound_ThrowsException() {
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Bluetooth Headphones");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, requestDTO));
    }

    @Test
    void testDeleteProduct_Success() {
        when(productRepository.existsById(1L)).thenReturn(true);
        productService.deleteProduct(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void testDeleteProduct_NotFound_ThrowsException() {
        when(productRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
    }
}
