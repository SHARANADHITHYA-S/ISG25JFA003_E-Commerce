package com.cognizant.ecommerce.dao;

import com.cognizant.ecommerce.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {


    List<Address> findByUserId(Long userId);
}