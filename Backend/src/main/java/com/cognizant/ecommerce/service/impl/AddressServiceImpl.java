package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.AddressRepository;
import com.cognizant.ecommerce.dao.UserRepository; // New import needed
import com.cognizant.ecommerce.dto.address.AddressRequestDTO;
import com.cognizant.ecommerce.dto.address.AddressResponseDTO;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.Address;
import com.cognizant.ecommerce.model.User; // New import needed
import com.cognizant.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository; // Added to support createAddress
    private final ModelMapper modelMapper;

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Address address = mapToEntity(addressRequestDTO);
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        return mapToResponseDTO(savedAddress);
    }

    @Override
    public AddressResponseDTO updateAddress(Long addressId, AddressRequestDTO addressRequestDTO) {
        // Find the existing address by ID; throw an exception if not found
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + addressId));

        // Update the fields from the DTO
        existingAddress.setAddress_line1(addressRequestDTO.getAddressLine1());
        existingAddress.setAddress_line2(addressRequestDTO.getAddressLine2());
        existingAddress.setCity(addressRequestDTO.getCity());
        existingAddress.setCountry(addressRequestDTO.getCountry());
        existingAddress.setPhone(addressRequestDTO.getPhone());
        existingAddress.setPostal_code(addressRequestDTO.getPostalCode());
        existingAddress.setState(addressRequestDTO.getState());
        existingAddress.setIs_default(addressRequestDTO.isDefault());

        // Save the updated address entity
        Address updatedAddress = addressRepository.save(existingAddress);

        // Convert and return the updated entity as a DTO
        return mapToResponseDTO(updatedAddress);
    }

    @Override
    public void deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        addressRepository.delete(address);
    }

    @Override
    public List<AddressResponseDTO> getAddressesByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<Address> addresses = addressRepository.findByUserId(userId);
        return addresses.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    private AddressResponseDTO mapToResponseDTO(Address address) {
        AddressResponseDTO responseDTO = new AddressResponseDTO();
        responseDTO.setId(address.getId());
        responseDTO.setAddressLine1(address.getAddress_line1());
        responseDTO.setAddressLine2(address.getAddress_line2());
        responseDTO.setCity(address.getCity());
        responseDTO.setState(address.getState());
        responseDTO.setPostalCode(address.getPostal_code());
        responseDTO.setCountry(address.getCountry());
        responseDTO.setPhone(address.getPhone());
        responseDTO.setDefault((Boolean) address.isDefault());
        responseDTO.setCreatedAt(address.getCreated_at());
        responseDTO.setUpdatedAt(address.getUpdated_at());
        return responseDTO;
    }


    private Address mapToEntity(AddressRequestDTO dto) {
        Address address = new Address();
        address.setAddress_line1(dto.getAddressLine1());
        address.setAddress_line2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setCountry(dto.getCountry());
        address.setPhone(dto.getPhone());
        address.setPostal_code(dto.getPostalCode());
        address.setState(dto.getState());
        address.setIs_default(dto.isDefault());
        return address;
    }
}