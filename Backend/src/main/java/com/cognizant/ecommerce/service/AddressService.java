package com.cognizant.ecommerce.service;

import com.cognizant.ecommerce.dto.address.AddressRequestDTO;
import com.cognizant.ecommerce.dto.address.AddressResponseDTO;

import java.util.List;
import java.util.Optional;

public interface AddressService {

    List<Object> getAllAddresses();

    Optional<AddressResponseDTO> getAddressById(Long addressId);

    AddressResponseDTO createAddress(Long userId, AddressRequestDTO addressRequestDTO);

    AddressResponseDTO updateAddress(Long addressId, AddressRequestDTO addressRequestDTO);

    void deleteAddress(Long addressId);

    List<AddressResponseDTO> getAddressesByUserId(Long userId);
}