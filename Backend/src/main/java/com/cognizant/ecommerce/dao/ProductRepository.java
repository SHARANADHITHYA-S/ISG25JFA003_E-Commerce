package com.cognizant.ecommerce.dao;

import com.cognizant.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    void deleteByCategoryId(Long categoryId);
    List<Product> findByCategoryId(Long categoryId);
}