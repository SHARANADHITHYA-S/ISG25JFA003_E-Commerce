package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.category.CategoryRequestDTO;
import com.cognizant.ecommerce.dto.category.CategoryResponseDTO;
import com.cognizant.ecommerce.service.CategoryService;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCategory_shouldReturnCreated() throws Exception {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Electronics");
        request.setDescription("Devices and gadgets");

        CategoryResponseDTO response = new CategoryResponseDTO();
        response.setId(1L);
        response.setName("Electronics");

        Mockito.when(categoryService.createCategory(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/categories/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void getCategoryById_shouldReturnOk() throws Exception {
        CategoryResponseDTO response = new CategoryResponseDTO();
        response.setId(1L);
        response.setName("Books");

        Mockito.when(categoryService.getCategoryById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void getAllCategories_shouldReturnOk() throws Exception {
        CategoryResponseDTO response = new CategoryResponseDTO();
        response.setId(1L);
        response.setName("Books");

        Mockito.when(categoryService.getAllCategories()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCategory_shouldReturnOk() throws Exception {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Updated Name");
        request.setDescription("Updated Description");

        CategoryResponseDTO response = new CategoryResponseDTO();
        response.setId(1L);
        response.setName("Updated Name");

        Mockito.when(categoryService.updateCategory(Mockito.eq(1L), Mockito.any())).thenReturn(response);

        mockMvc.perform(put("/api/categories/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCategory_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/categories/admin/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Category Deleted"));
    }
}
