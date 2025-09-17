package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.AddressRepository;
import com.cognizant.ecommerce.dto.address.AddressRequestDTO;
import com.cognizant.ecommerce.dto.address.AddressResponseDTO;
import com.cognizant.ecommerce.model.Address;
import com.cognizant.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    public List<Object> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AddressResponseDTO> getAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .map(this::mapToResponseDTO);
    }

    @Override
    public AddressResponseDTO createAddress(Long userId, AddressRequestDTO addressRequestDTO) {
        // Implementation for creating an address would go here
        return null;
    }

    @Override
    public AddressResponseDTO updateAddress(Long addressId, AddressRequestDTO addressRequestDTO) {
        // Implementation for updating an address would go here
        return null;
    }

    @Override
    public void deleteAddress(Long addressId) {
        // Implementation for deleting an address would go here
    }

    @Override
    public List<AddressResponseDTO> getAddressesByUserId(Long userId) {
        // Implementation for getting addresses by user ID would go here
        return List.of();
    }

    /**
     * Helper method to map Address entity to AddressResponseDTO.
     *
     * @param address The entity to map.
     * @return The mapped AddressResponseDTO.
     */
    private AddressResponseDTO mapToResponseDTO(Address address) {
        AddressResponseDTO responseDTO = new AddressResponseDTO();
        // Correctly populate the `id` and `addressId` fields.
        responseDTO.setId(address.getId());
        responseDTO.setAddressId(address.getId());
        responseDTO.setAddress_line1(address.getAddress_line1());
        responseDTO.setAddress_line2(address.getAddress_line2());
        responseDTO.setCity(address.getCity());
        responseDTO.setState(address.getState());
        responseDTO.setPostal_code((String) address.getPostal_code());
        responseDTO.setCountry(address.getCountry());
        responseDTO.setPhone(address.getPhone());
        responseDTO.setDefault((Boolean) address.isDefault());
        responseDTO.setCreated_at(address.getCreated_at());
        responseDTO.setUpdated_at(address.getUpdated_at());
        return responseDTO;
    }
}