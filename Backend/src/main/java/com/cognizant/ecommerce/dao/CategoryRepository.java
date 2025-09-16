package com.cognizant.ecommerce.dao;

import com.cognizant.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // This method will automatically be implemented by Spring Data JPA
    Optional<Category> findByName(String name);
}