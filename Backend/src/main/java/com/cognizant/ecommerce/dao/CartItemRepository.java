package com.cognizant.ecommerce.dao;

import com.cognizant.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    void deleteAllByCartUserId(Long userId);
    Optional<CartItem> findByCartUserId(Long userId);
    void deleteAllByCartId(Long cartId);
}