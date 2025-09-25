package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.category.CategoryRequestDTO;
import com.cognizant.ecommerce.dto.category.CategoryResponseDTO;
import com.cognizant.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        logger.info("Creating category: {}", categoryRequestDTO.getName());
        CategoryResponseDTO createdCategory = categoryService.createCategory(categoryRequestDTO);
        logger.info("Category created with ID: {}", createdCategory.getId());
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        logger.info("Fetching category by ID: {}", id);
        CategoryResponseDTO category = categoryService.getCategoryById(id);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        logger.info("Fetching all categories");
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
        logger.info("Total categories fetched: {}", categories.size());
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        logger.info("Updating category ID: {}", id);
        CategoryResponseDTO updatedCategory = categoryService.updateCategory(id, categoryRequestDTO);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        logger.info("Deleting category ID: {}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category Deleted");
    }
}
