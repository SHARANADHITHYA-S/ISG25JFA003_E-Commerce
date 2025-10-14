package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.address.AddressRequestDTO;
import com.cognizant.ecommerce.dto.address.AddressResponseDTO;
import com.cognizant.ecommerce.service.AddressService;
import com.cognizant.ecommerce.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private AddressRequestDTO sampleRequest;
    private AddressResponseDTO sampleResponse;

    @BeforeEach
    void setup() {
        sampleRequest = new AddressRequestDTO();
        sampleRequest.setStreet("Main Street");
        sampleRequest.setCity("Coimbatore");
        sampleRequest.setAddress_line1("123 Main St"); // ✅ valid
        sampleRequest.setState("TN");
        sampleRequest.setPostal_code("641001");
        sampleRequest.setCountry("India");
        sampleRequest.setPhone("9876543210");
        sampleRequest.setDefault(true);

        sampleResponse = new AddressResponseDTO();
        sampleResponse.setId(1L);
        sampleResponse.setStreet("Main Street");
        sampleResponse.setCity("Coimbatore");
        sampleRequest.setAddress_line1("123 Main St"); // ✅ valid
        sampleResponse.setState("TN");
        sampleResponse.setCountry("India");
        sampleResponse.setPhone("9876543210");
        sampleResponse.setDefault(true);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllAddresses_shouldReturnOk() throws Exception {
        Mockito.when(addressService.getAllAddresses()).thenReturn(List.of());

        mockMvc.perform(get("/api/addresses/admin"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAddressById_shouldReturnOk() throws Exception {
        Mockito.when(addressService.getAddressById(1L)).thenReturn(Optional.of(sampleResponse));

        mockMvc.perform(get("/api/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createAddress_shouldReturnCreated() throws Exception {
        Mockito.when(authService.isSelfOrAdmin(1L)).thenReturn(true);
        Mockito.when(addressService.createAddress(Mockito.eq(1L), Mockito.any())).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/addresses/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateAddress_shouldReturnOk() throws Exception {
        Mockito.when(authService.isSelfOrAdmin(1L)).thenReturn(true);
        Mockito.when(addressService.updateAddress(Mockito.eq(1L), Mockito.any())).thenReturn(sampleResponse);

        mockMvc.perform(put("/api/addresses/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteAddress_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Address Deleted"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAddressesByUserId_shouldReturnOk() throws Exception {
        Mockito.when(authService.isSelfOrAdmin(1L)).thenReturn(true);
        Mockito.when(addressService.getAddressesByUserId(1L)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/addresses/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
