package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dto.category.CategoryResponseDTO;
import com.cognizant.ecommerce.dto.product.ProductRequestDTO;
import com.cognizant.ecommerce.dto.product.ProductResponseDTO;
import com.cognizant.ecommerce.exception.DuplicateResourceException;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.Category;
import com.cognizant.ecommerce.model.Product;
import com.cognizant.ecommerce.dao.CategoryRepository;
import com.cognizant.ecommerce.dao.ProductRepository;
import com.cognizant.ecommerce.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        if (productRepository.findByName(productRequestDTO.getName()).isPresent()) {
            throw new DuplicateResourceException("Product with name '" + productRequestDTO.getName() + "' already exists.");
        }

        Category category = categoryRepository.findById(productRequestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productRequestDTO.getCategoryId()));

        Product product = new Product();
        BeanUtils.copyProperties(productRequestDTO, product);
        product.setActive(productRequestDTO.getQuantity() > 0);
        product.setCategory(category);
        Product savedProduct = productRepository.save(product);
        return mapToProductResponseDTO(savedProduct);
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToProductResponseDTO(product);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToProductResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!existingProduct.getName().equals(productRequestDTO.getName())) {
            if (productRepository.findByName(productRequestDTO.getName()).isPresent()) {
                throw new DuplicateResourceException("Product with name '" + productRequestDTO.getName() + "' already exists.");
            }
        }

        Category category = categoryRepository.findById(productRequestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productRequestDTO.getCategoryId()));

        BeanUtils.copyProperties(productRequestDTO, existingProduct);
        existingProduct.setActive(productRequestDTO.getQuantity() > 0);
        existingProduct.setCategory(category);
        Product updatedProduct = productRepository.save(existingProduct);
        return mapToProductResponseDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public List<ProductResponseDTO> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(product -> {
                    ProductResponseDTO dto = new ProductResponseDTO();
                    BeanUtils.copyProperties(product, dto);// optional
                    dto.setCategory_id(categoryId);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private ProductResponseDTO mapToProductResponseDTO(Product product) {
        ProductResponseDTO responseDTO = new ProductResponseDTO();
        BeanUtils.copyProperties(product, responseDTO);

        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();
        BeanUtils.copyProperties(product.getCategory(), categoryResponseDTO);
        responseDTO.setCategory_id(categoryResponseDTO.getId());

        return responseDTO;
    }
}