package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.CategoryRepository;
import com.cognizant.ecommerce.dao.ProductRepository;
import com.cognizant.ecommerce.dto.category.CategoryRequestDTO;
import com.cognizant.ecommerce.dto.category.CategoryResponseDTO;
import com.cognizant.ecommerce.dto.product.ProductResponseDTO;
import com.cognizant.ecommerce.exception.DuplicateResourceException;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.Category;
import com.cognizant.ecommerce.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCategory_shouldSucceed_whenNameIsUnique() {
        CategoryRequestDTO request = new CategoryRequestDTO("Electronics", "Devices");
        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.empty());

        Category saved = new Category();
        saved.setId(1L);
        saved.setName("Electronics");
        saved.setDescription("Devices");

        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryResponseDTO result = categoryService.createCategory(request);

        assertEquals("Electronics", result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    void createCategory_shouldThrow_whenNameExists() {
        CategoryRequestDTO request = new CategoryRequestDTO("Books", "Reading");
        when(categoryRepository.findByName("Books")).thenReturn(Optional.of(new Category()));

        assertThrows(DuplicateResourceException.class, () -> categoryService.createCategory(request));
    }

    @Test
    void getCategoryById_shouldReturnCategoryWithProducts() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Fashion");
        category.setDescription("Clothing");

        Product product = new Product();
        product.setId(101L);
        product.setName("Shirt");
        product.setDescription("Cotton shirt");
        product.setPrice(BigDecimal.valueOf(499));
        product.setImage_url("img.jpg");
        product.setQuantity(10L);
        product.setActive(true);
        product.setCategory(category);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findByCategoryId(1L)).thenReturn(List.of(product));

        CategoryResponseDTO result = categoryService.getCategoryById(1L);

        assertEquals("Fashion", result.getName());
        assertEquals(1, result.getProducts().size());
        ProductResponseDTO p = result.getProducts().get(0);
        assertEquals("Shirt", p.getName());
        assertEquals(1L, p.getCategory_id());
    }

    @Test
    void getCategoryById_shouldThrow_whenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(99L));
    }

    @Test
    void getAllCategories_shouldReturnList() {
        Category c1 = new Category(); c1.setId(1L); c1.setName("Home");
        Category c2 = new Category(); c2.setId(2L); c2.setName("Garden");

        when(categoryRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CategoryResponseDTO> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Home", result.get(0).getName());
        assertEquals("Garden", result.get(1).getName());
    }

    @Test
    void updateCategory_shouldSucceed_whenNameIsChangedAndUnique() {
        Category existing = new Category(); existing.setId(1L); existing.setName("OldName");
        CategoryRequestDTO request = new CategoryRequestDTO("NewName", "Updated");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByName("NewName")).thenReturn(Optional.empty());

        Category updated = new Category(); updated.setId(1L); updated.setName("NewName"); updated.setDescription("Updated");
        when(categoryRepository.save(any(Category.class))).thenReturn(updated);

        CategoryResponseDTO result = categoryService.updateCategory(1L, request);

        assertEquals("NewName", result.getName());
        assertEquals("Updated", result.getDescription());
    }

    @Test
    void updateCategory_shouldThrow_whenNewNameIsDuplicate() {
        Category existing = new Category(); existing.setId(1L); existing.setName("OldName");
        CategoryRequestDTO request = new CategoryRequestDTO("ExistingName", "Updated");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByName("ExistingName")).thenReturn(Optional.of(new Category()));

        assertThrows(DuplicateResourceException.class, () -> categoryService.updateCategory(1L, request));
    }

    @Test
    void deleteCategory_shouldSucceed_whenExists() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.deleteCategory(1L);

        verify(productRepository).deleteByCategoryId(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void deleteCategory_shouldThrow_whenNotFound() {
        when(categoryRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(99L));
    }
}
