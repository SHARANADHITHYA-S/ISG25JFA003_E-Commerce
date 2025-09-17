package com.cognizant.ecommerce.dao;

import com.cognizant.ecommerce.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    /**
     * Finds all payment methods associated with a specific user.
     * Spring Data JPA automatically generates the query for this method
     * based on the 'user' field in the PaymentMethod entity.
     *
     * @param userId The ID of the user.
     * @return A list of payment methods for the given user.
     */
    List<PaymentMethod> findByUserId(Long userId);
}