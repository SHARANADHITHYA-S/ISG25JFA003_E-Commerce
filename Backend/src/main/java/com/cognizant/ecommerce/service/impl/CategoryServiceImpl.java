package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.ProductRepository;
import com.cognizant.ecommerce.dto.category.CategoryRequestDTO;
import com.cognizant.ecommerce.dto.category.CategoryResponseDTO;
import com.cognizant.ecommerce.dto.product.ProductResponseDTO;
import com.cognizant.ecommerce.exception.DuplicateResourceException;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.Category;
import com.cognizant.ecommerce.dao.CategoryRepository;
import com.cognizant.ecommerce.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) {
        // Business logic: check for a category with the same name before creating a new one.
        if (categoryRepository.findByName(categoryRequestDTO.getName()).isPresent()) {
            throw new DuplicateResourceException("Category with name '" + categoryRequestDTO.getName() + "' already exists.");
        }

        Category category = new Category();
        BeanUtils.copyProperties(categoryRequestDTO, category);
        Category savedCategory = categoryRepository.save(category);
        CategoryResponseDTO responseDTO = new CategoryResponseDTO();
        BeanUtils.copyProperties(savedCategory, responseDTO);
        return responseDTO;
    }

    @Override
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        CategoryResponseDTO dto = new CategoryResponseDTO();
        BeanUtils.copyProperties(category, dto);

        List<ProductResponseDTO> productDTOs = productRepository.findByCategoryId(id).stream()
                .map(product -> {
                    ProductResponseDTO pDto = new ProductResponseDTO();
                    BeanUtils.copyProperties(product, pDto);
                    pDto.setCategory_id(id);
                    return pDto;
                })
                .collect(Collectors.toList());

        dto.setProducts(productDTOs);
        return dto;
    }




    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> {
                    CategoryResponseDTO responseDTO = new CategoryResponseDTO();
                    BeanUtils.copyProperties(category, responseDTO);
                    return responseDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO categoryRequestDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Business logic: check for duplicate name if the name is being changed
        if (!existingCategory.getName().equals(categoryRequestDTO.getName())) {
            if (categoryRepository.findByName(categoryRequestDTO.getName()).isPresent()) {
                throw new DuplicateResourceException("Category with name '" + categoryRequestDTO.getName() + "' already exists.");
            }
        }

        BeanUtils.copyProperties(categoryRequestDTO, existingCategory);
        Category updatedCategory = categoryRepository.save(existingCategory);
        CategoryResponseDTO responseDTO = new CategoryResponseDTO();
        BeanUtils.copyProperties(updatedCategory, responseDTO);
        return responseDTO;
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }

        // Delete all products linked to this category
        productRepository.deleteByCategoryId(id);

        // Now delete the category
        categoryRepository.deleteById(id);
    }
}