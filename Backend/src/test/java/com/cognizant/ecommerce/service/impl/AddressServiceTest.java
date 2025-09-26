package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.AddressRepository;
import com.cognizant.ecommerce.dao.OrderRepository;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.address.AddressRequestDTO;
import com.cognizant.ecommerce.dto.address.AddressResponseDTO;
import com.cognizant.ecommerce.exception.AddressNotFoundException;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.Address;
import com.cognizant.ecommerce.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    // Helper methods to create mock objects
    private Address createAddress(Long id) {
        Address address = new Address();
        address.setId(id);
        address.setAddress_line1("123 Test St");
        return address;
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private AddressRequestDTO createAddressRequestDTO() {
        AddressRequestDTO dto = new AddressRequestDTO();
        dto.setAddress_line1("123 Test St");
        return dto;
    }

    private AddressResponseDTO createAddressResponseDTO(Long id) {
        AddressResponseDTO dto = new AddressResponseDTO();
        dto.setId(id);
        dto.setAddress_line1("123 Test St");
        return dto;
    }

    @Test
    void testGetAddressById_Success() {
        Address address = createAddress(1L);
        AddressResponseDTO responseDTO = createAddressResponseDTO(1L);

        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(modelMapper.map(any(Address.class), any())).thenReturn(responseDTO);

        Optional<AddressResponseDTO> result = addressService.getAddressById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testGetAddressById_NotFound() {
        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> addressService.getAddressById(1L));
    }

    @Test
    void testCreateAddress_Success() {
        User user = createUser(1L);
        Address address = createAddress(1L);
        AddressRequestDTO requestDTO = createAddressRequestDTO();
        AddressResponseDTO responseDTO = createAddressResponseDTO(1L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(modelMapper.map(any(AddressRequestDTO.class), any())).thenReturn(address);
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(modelMapper.map(any(Address.class), any())).thenReturn(responseDTO);

        AddressResponseDTO result = addressService.createAddress(1L, requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void testCreateAddress_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressService.createAddress(1L, createAddressRequestDTO()));
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void testUpdateAddress_Success() {
        Address existingAddress = createAddress(1L);
        AddressRequestDTO requestDTO = createAddressRequestDTO();
        AddressResponseDTO responseDTO = createAddressResponseDTO(1L);

        when(addressRepository.findById(anyLong())).thenReturn(Optional.of(existingAddress));
        doNothing().when(modelMapper).map(any(AddressRequestDTO.class), any(Address.class));
        when(addressRepository.save(any(Address.class))).thenReturn(existingAddress);
        when(modelMapper.map(any(Address.class), any())).thenReturn(responseDTO);

        AddressResponseDTO result = addressService.updateAddress(1L, requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void testUpdateAddress_NotFound() {
        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> addressService.updateAddress(1L, createAddressRequestDTO()));
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void testDeleteAddress_Success() {
        Address address = new Address();
        address.setId(1L);

        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        doNothing().when(addressRepository).delete(address); // match actual service call

        addressService.deleteAddress(1L);

        verify(addressRepository, times(1)).delete(address);
    }

    @Test
    void testDeleteAddress_NotFound() {
        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> addressService.deleteAddress(1L));
        verify(addressRepository, never()).deleteById(anyLong());
    }
}