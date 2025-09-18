package com.cognizant.ecommerce.service;

import com.cognizant.ecommerce.dto.address.AddressRequestDTO;
import com.cognizant.ecommerce.dto.address.AddressResponseDTO;

import java.util.List;
import java.util.Optional;

public interface AddressService {

    List<Object> getAllAddresses();

    Optional<AddressResponseDTO> getAddressById(Long addressId);

    // POST: Method to create a new address
    AddressResponseDTO createAddress(Long userId, AddressRequestDTO addressRequestDTO);

    // PUT: Method to update an existing address
    AddressResponseDTO updateAddress(Long addressId, AddressRequestDTO addressRequestDTO);

    void deleteAddress(Long addressId);

    List<AddressResponseDTO> getAddressesByUserId(Long userId);
}