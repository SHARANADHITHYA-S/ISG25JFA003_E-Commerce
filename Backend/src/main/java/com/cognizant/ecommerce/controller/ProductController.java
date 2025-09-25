package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.product.ProductRequestDTO;
import com.cognizant.ecommerce.dto.product.ProductResponseDTO;
import com.cognizant.ecommerce.service.ProductService;
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
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        logger.info("Creating product: {}", productRequestDTO.getName());
        ProductResponseDTO createdProduct = productService.createProduct(productRequestDTO);
        logger.info("Product created with ID: {}", createdProduct.getId());
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        logger.info("Fetching product by ID: {}", id);
        ProductResponseDTO product = productService.getProductById(id);
        logger.info("Product fetched: {}", product.getName());
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        logger.info("Fetching all products");
        List<ProductResponseDTO> products = productService.getAllProducts();
        logger.info("Total products fetched: {}", products.size());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        logger.info("Fetching products by category ID: {}", categoryId);
        List<ProductResponseDTO> products = productService.getProductsByCategoryId(categoryId);
        logger.info("Products fetched for category {}: {}", categoryId, products.size());
        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        logger.info("Updating product ID: {}", id);
        ProductResponseDTO updatedProduct = productService.updateProduct(id, productRequestDTO);
        logger.info("Product updated: {}", updatedProduct.getName());
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        logger.info("Deleting product ID: {}", id);
        productService.deleteProduct(id);
        logger.info("Product deleted: {}", id);
        return ResponseEntity.ok("Product Deleted");
    }
}
