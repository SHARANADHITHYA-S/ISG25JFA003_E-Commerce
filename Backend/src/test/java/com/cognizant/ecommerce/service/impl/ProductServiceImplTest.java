package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.CategoryRepository;
import com.cognizant.ecommerce.dao.ProductRepository;
import com.cognizant.ecommerce.dto.product.ProductRequestDTO;
import com.cognizant.ecommerce.dto.product.ProductResponseDTO;
import com.cognizant.ecommerce.exception.DuplicateResourceException;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.Category;
import com.cognizant.ecommerce.model.Product;
import com.cognizant.ecommerce.service.ProductService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProductServiceImplTest {

    @Autowired private ProductService productService;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ModelMapper modelMapper;

    private Category testCategory;
    private Product existingProduct;

    @BeforeEach
    public void setup() {
        testCategory = categoryRepository.save(Category.builder()
                .name("E-Gadgets")
                .build());

        existingProduct = productRepository.save(Product.builder()
                .name("Old Product")
                .description("Old Description")
                .price(BigDecimal.valueOf(100))
                .image_url("old.jpg")
                .quantity(10L)
                .isActive(true)
                .category(testCategory)
                .build());
    }

    @Test
    public void testUpdateProduct_Success() {
        ProductRequestDTO requestDTO = ProductRequestDTO.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(BigDecimal.valueOf(150))
                .image_url("updated.jpg")
                .quantity(20L)
                .isActive(true)
                .categoryId(testCategory.getId())
                .build();

        ProductResponseDTO response = productService.updateProduct(existingProduct.getId(), requestDTO);

        assertNotNull(response);
        assertEquals("Updated Product", response.getName());
        assertEquals("Updated Description", response.getDescription());
        assertEquals(BigDecimal.valueOf(150), response.getPrice());
    }

    @Test
    public void testUpdateProduct_ProductNotFound() {
        ProductRequestDTO requestDTO = ProductRequestDTO.builder()
                .name("Nonexistent Product")
                .description("Desc")
                .price(BigDecimal.valueOf(100))
                .image_url("img.jpg")
                .quantity(5L)
                .isActive(true)
                .categoryId(testCategory.getId())
                .build();

        assertThrows(ResourceNotFoundException.class, () ->
                productService.updateProduct(999L, requestDTO));
    }

    @Test
    public void testUpdateProduct_DuplicateName() {
        productRepository.save(Product.builder()
                .name("Duplicate Name")
                .description("Another")
                .price(BigDecimal.valueOf(200))
                .image_url("dup.jpg")
                .quantity(5L)
                .isActive(true)
                .category(testCategory)
                .build());

        ProductRequestDTO requestDTO = ProductRequestDTO.builder()
                .name("Duplicate Name")
                .description("Updated")
                .price(BigDecimal.valueOf(150))
                .image_url("updated.jpg")
                .quantity(20L)
                .isActive(true)
                .categoryId(testCategory.getId())
                .build();

        assertThrows(DuplicateResourceException.class, () ->
                productService.updateProduct(existingProduct.getId(), requestDTO));
    }

    @Test
    public void testUpdateProduct_CategoryNotFound() {
        ProductRequestDTO requestDTO = ProductRequestDTO.builder()
                .name("New Name")
                .description("Updated")
                .price(BigDecimal.valueOf(150))
                .image_url("updated.jpg")
                .quantity(20L)
                .isActive(true)
                .categoryId(999L)
                .build();

        assertThrows(ResourceNotFoundException.class, () ->
                productService.updateProduct(existingProduct.getId(), requestDTO));
    }
}
