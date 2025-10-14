package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.AddressRepository;
import com.cognizant.ecommerce.dao.OrderRepository;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.address.AddressRequestDTO;
import com.cognizant.ecommerce.dto.address.AddressResponseDTO;
import com.cognizant.ecommerce.exception.AddressNotFoundException;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.Address;
import com.cognizant.ecommerce.model.Order;
import com.cognizant.ecommerce.model.User;
import com.cognizant.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional // Good practice for service methods
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;

    @Override
    public List<Object> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

    }

    @Override
    public Optional<AddressResponseDTO> getAddressById(Long addressId) {
        return Optional.ofNullable(addressRepository.findById(addressId)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with ID: " + addressId)));
    }

    // This is the new method for throwing exceptions, as discussed
    public AddressResponseDTO getAddressByIdOrThrow(Long addressId) {
        return addressRepository.findById(addressId)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with ID: " + addressId));
    }

    @Override
    public AddressResponseDTO createAddress(Long userId, AddressRequestDTO addressRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Use ModelMapper to simplify mapping
        Address address = modelMapper.map(addressRequestDTO, Address.class);
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressResponseDTO.class);
    }

    @Override
    public AddressResponseDTO updateAddress(Long addressId, AddressRequestDTO addressRequestDTO) {
        // Corrected: Use specific AddressNotFoundException
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException("Address not found with ID: " + addressId));

        // Use ModelMapper to simplify mapping
        modelMapper.map(addressRequestDTO, existingAddress);

        Address updatedAddress = addressRepository.save(existingAddress);
        return modelMapper.map(updatedAddress, AddressResponseDTO.class);
    }

    @Override
    public void deleteAddress(Long addressId) {
        // Corrected: Use specific AddressNotFoundException
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        List<Order> linkedOrders = orderRepository.findByAddressId(addressId);
        for (Order order : linkedOrders) {
            order.setAddress(null);
        }
        orderRepository.saveAll(linkedOrders);

        addressRepository.delete(address); // Use deleteById for clarity
    }

    @Override
    public List<AddressResponseDTO> getAddressesByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<Address> addresses = addressRepository.findByUserId(userId);
        return addresses.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Consolidated and simplified mapping method
    private AddressResponseDTO mapToResponseDTO(Address address) {
        return modelMapper.map(address, AddressResponseDTO.class);
    }
}