package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.address.AddressRequestDTO;
import com.cognizant.ecommerce.dto.address.AddressResponseDTO;
import com.cognizant.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/admin")
    public ResponseEntity<List<Object>> getAllAddresses() {
        List<Object> addresses = addressService.getAllAddresses();
        return ok(addresses);
    }


    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> getAddressById(@PathVariable Long addressId) {
        return addressService.getAddressById(addressId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("@authService.isSelfOrAdmin(#userId)")
    @PostMapping("/user/{userId}")
    public ResponseEntity<AddressResponseDTO> createAddress(@PathVariable Long userId, @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        AddressResponseDTO createdAddress = addressService.createAddress(userId, addressRequestDTO);
        return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
    }

    @PreAuthorize("@authService.isSelfOrAdmin(#userId)")
    @PutMapping("/{userId}/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@Valid @PathVariable Long userId, @PathVariable Long addressId, @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        AddressResponseDTO updatedAddress = addressService.updateAddress(addressId, addressRequestDTO);
        return ok(updatedAddress);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok("Address Deleted");
    }

    @PreAuthorize("@authService.isSelfOrAdmin(#userId)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddressResponseDTO>> getAddressesByUserId(@PathVariable Long userId) {
        List<AddressResponseDTO> addresses = addressService.getAddressesByUserId(userId);
        return ok(addresses);
    }
}