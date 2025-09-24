package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.address.AddressRequestDTO;
import com.cognizant.ecommerce.dto.address.AddressResponseDTO;
import com.cognizant.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    private final AddressService addressService;

    @GetMapping("/admin")
    public ResponseEntity<List<Object>> getAllAddresses() {
        logger.info("Fetching all addresses");
        List<Object> addresses = addressService.getAllAddresses();
        logger.info("Total addresses fetched: {}", addresses.size());
        return ok(addresses);
    }


    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> getAddressById(@PathVariable Long addressId) {
        logger.info("Fetching address by ID: {}", addressId);
        return addressService.getAddressById(addressId)
                .map(address -> {
                    logger.info("Address found for ID: {}", addressId);
                    return ResponseEntity.ok(address);
                })
                .orElseGet(() -> {
                    logger.warn("Address not found for ID: {}", addressId);
                    return ResponseEntity.notFound().build();
                });
    }

    @PreAuthorize("@authService.isSelfOrAdmin(#userId)")
    @PostMapping("/user/{userId}")
    public ResponseEntity<AddressResponseDTO> createAddress(@PathVariable Long userId, @RequestBody AddressRequestDTO addressRequestDTO) {
        logger.info("Creating address for user ID: {}", userId);
        AddressResponseDTO createdAddress = addressService.createAddress(userId, addressRequestDTO);
        logger.info("Address created with ID: {}", createdAddress.getId());
        return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
    }

    @PreAuthorize("@authService.isSelfOrAdmin(#addressRequestDTO.userId)")
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Long addressId, @RequestBody AddressRequestDTO addressRequestDTO) {
        logger.info("Updating address ID: {}", addressId);
        AddressResponseDTO updatedAddress = addressService.updateAddress(addressId, addressRequestDTO);
        logger.info("Address updated for ID: {}", addressId);
        return ok(updatedAddress);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        logger.info("Deleting address ID: {}", addressId);
        addressService.deleteAddress(addressId);
        logger.info("Address deleted for ID: {}", addressId);
        return ResponseEntity.ok("Address Deleted");
    }

    @PreAuthorize("@authService.isSelfOrAdmin(#userId)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddressResponseDTO>> getAddressesByUserId(@PathVariable Long userId) {
        logger.info("Fetching addresses for user ID: {}", userId);
        List<AddressResponseDTO> addresses = addressService.getAddressesByUserId(userId);
        logger.info("Total addresses fetched for user ID {}: {}", userId, addresses.size());
        return ok(addresses);
    }
}
