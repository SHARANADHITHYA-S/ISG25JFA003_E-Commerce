package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportRequestDTO;
import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportResponseDTO;
import com.cognizant.ecommerce.service.AnalyticsReportService;
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
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AnalyticsReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsReportService analyticsReportService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void generateReport_shouldReturnCreated() throws Exception {
        AnalyticsReportRequestDTO request = new AnalyticsReportRequestDTO();
        AnalyticsReportResponseDTO response = new AnalyticsReportResponseDTO();

        Mockito.when(analyticsReportService.generateReport(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/analytics-reports/admin/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllReports_shouldReturnOk() throws Exception {
        Mockito.when(analyticsReportService.getAllReports()).thenReturn(List.of());

        mockMvc.perform(get("/api/analytics-reports/admin"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getReportById_shouldReturnOk() throws Exception {
        AnalyticsReportResponseDTO response = new AnalyticsReportResponseDTO();
        Mockito.when(analyticsReportService.getReportById(1L)).thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/analytics-reports/admin/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateReport_shouldReturnOk() throws Exception {
        AnalyticsReportRequestDTO request = new AnalyticsReportRequestDTO();
        AnalyticsReportResponseDTO response = new AnalyticsReportResponseDTO();

        Mockito.when(analyticsReportService.updateReport(Mockito.eq(1L), Mockito.any())).thenReturn(response);

        mockMvc.perform(put("/api/analytics-reports/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteReport_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/analytics-reports/admin/1"))
                .andExpect(status().isNoContent());
    }
}
