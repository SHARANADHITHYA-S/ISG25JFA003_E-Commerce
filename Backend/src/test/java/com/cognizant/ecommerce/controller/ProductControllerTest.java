package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.product.ProductRequestDTO;
import com.cognizant.ecommerce.dto.product.ProductResponseDTO;
import com.cognizant.ecommerce.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_shouldReturnCreated() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Laptop");
        request.setDescription("High-end gaming laptop");
        request.setPrice(BigDecimal.valueOf(150000));
        request.setQuantity(10L);
        request.setCategoryId(1L);
        request.setImage_url("https://example.com/laptop.jpg"); // ✅ required field
        request.setIsActive(true); // optional, but good to include

        ProductResponseDTO response = new ProductResponseDTO();
        response.setId(1L);
        response.setName("Laptop");

        Mockito.when(productService.createProduct(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/products/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getProductById_shouldReturnOk() throws Exception {
        ProductResponseDTO response = new ProductResponseDTO();
        response.setId(1L);
        response.setName("Laptop");

        Mockito.when(productService.getProductById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void getAllProducts_shouldReturnOk() throws Exception {
        ProductResponseDTO response = new ProductResponseDTO();
        response.setId(1L);
        response.setName("Laptop");

        Mockito.when(productService.getAllProducts()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void getProductsByCategory_shouldReturnOk() throws Exception {
        ProductResponseDTO response = new ProductResponseDTO();
        response.setId(1L);
        response.setName("Laptop");

        Mockito.when(productService.getProductsByCategoryId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/products/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_shouldReturnOk() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Updated Laptop");
        request.setDescription("Updated description");
        request.setPrice(BigDecimal.valueOf(160000));
        request.setQuantity(5L);
        request.setCategoryId(1L);
        request.setImage_url("https://example.com/laptop-updated.jpg"); // ✅ required field
        request.setIsActive(true);

        ProductResponseDTO response = new ProductResponseDTO();
        response.setId(1L);
        response.setName("Updated Laptop");

        Mockito.when(productService.updateProduct(Mockito.eq(1L), Mockito.any())).thenReturn(response);

        mockMvc.perform(put("/api/products/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Laptop"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_shouldReturnOk() throws Exception {
        Mockito.doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/admin/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Product Deleted"));
    }
}
