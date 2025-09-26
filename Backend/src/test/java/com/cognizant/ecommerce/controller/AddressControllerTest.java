package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.config.JwtAuthFilter;
import com.cognizant.ecommerce.config.JwtUtil;
import com.cognizant.ecommerce.dto.address.AddressRequestDTO;
import com.cognizant.ecommerce.dto.address.AddressResponseDTO;
import com.cognizant.ecommerce.service.AddressService;
import com.cognizant.ecommerce.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtUtil jwtUtil;


    @Autowired
    private ObjectMapper objectMapper;

    AddressControllerTest(AuthService authService) {
        this.authService = authService;
    }

    @BeforeEach
    void setup() {
        Mockito.when(authService.isSelfOrAdmin(any())).thenReturn(true);
    }

    private AddressRequestDTO validRequest() {
        return new AddressRequestDTO(
                "Street 1",
                "Address Line 1",
                "Address Line 2",
                "City",
                "State",
                "123456",
                "Country",
                "9876543210",
                true
        );
    }

    @Test
    void testCreateAddress_ValidInput_ReturnsCreated() throws Exception {
        AddressResponseDTO responseDTO = new AddressResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setStreet("Street 1");

        Mockito.when(addressService.createAddress(eq(10L), any(AddressRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/addresses/user/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.street").value("Street 1"));
    }

    @Test
    void testCreateAddress_InvalidInput_ReturnsBadRequest() throws Exception {
        AddressRequestDTO invalidRequest = new AddressRequestDTO(
                "", "", "Line 2", "", "", "", "", "", true
        );

        mockMvc.perform(post("/api/addresses/user/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.street").value("Street cannot be null"))
                .andExpect(jsonPath("$.addressLine1").value("address cannot be null"))
                .andExpect(jsonPath("$.city").value("city cannot be null"))
                .andExpect(jsonPath("$.state").value("state cannot be null"))
                .andExpect(jsonPath("$.postalCode").value("postal code cannot be null"))
                .andExpect(jsonPath("$.country").value("country cannot be null"))
                .andExpect(jsonPath("$.phone").value("phone no. cannot be null"));
    }

    @Test
    void testGetAddressById_Found() throws Exception {
        AddressResponseDTO responseDTO = new AddressResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setStreet("Street 1");

        Mockito.when(addressService.getAddressById(1L)).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(get("/api/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value("Street 1"));
    }

    @Test
    void testDeleteAddress_ReturnsOk() throws Exception {
        Mockito.doNothing().when(addressService).deleteAddress(1L);

        mockMvc.perform(delete("/api/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Address Deleted"));
    }

    @Test
    void testGetAddressesByUserId_ReturnsList() throws Exception {
        AddressResponseDTO dto1 = new AddressResponseDTO();
        dto1.setId(1L);
        dto1.setStreet("Street A");

        AddressResponseDTO dto2 = new AddressResponseDTO();
        dto2.setId(2L);
        dto2.setStreet("Street B");

        Mockito.when(addressService.getAddressesByUserId(10L)).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/addresses/user/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].street").value("Street A"))
                .andExpect(jsonPath("$[1].street").value("Street B"));
    }
}
