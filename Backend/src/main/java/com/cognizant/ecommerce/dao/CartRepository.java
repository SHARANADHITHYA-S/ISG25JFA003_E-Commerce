package com.cognizant.ecommerce.dao;

import com.cognizant.ecommerce.model.Cart;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
    @Transactional
    void deleteByUserId(Long userId);

}