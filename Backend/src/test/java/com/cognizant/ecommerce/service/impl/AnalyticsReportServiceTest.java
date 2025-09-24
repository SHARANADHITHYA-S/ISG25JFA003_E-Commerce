package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.AnalyticsReportRepository;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportRequestDTO;
import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportResponseDTO;
import com.cognizant.ecommerce.model.AnalyticsReport;
import com.cognizant.ecommerce.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnalyticsReportServiceTest {

    @Mock
    private AnalyticsReportRepository analyticsReportRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AnalyticsReportServiceImpl analyticsReportService;

    // Helper method to create a sample User
    private User createUser() {
        User user = new User();
        user.setId(1L);
        return user;
    }

    // Helper method to create a sample AnalyticsReport entity
    private AnalyticsReport createReportEntity(Long id) {
        AnalyticsReport report = new AnalyticsReport();
        report.setId(id);
        report.setReport_type("TestType");
        report.setGeneratedBy(createUser());
        report.setGenerated_at(new Date());
        return report;
    }

    // Helper method to create a sample DTO for requests
    private AnalyticsReportRequestDTO createRequestDTO() {
        AnalyticsReportRequestDTO dto = new AnalyticsReportRequestDTO();
        dto.setReportType("TestType");
        return dto;
    }

    @Test
    void testGenerateReport_Success() {
        User user = createUser();
        AnalyticsReport savedReport = createReportEntity(1L);
        AnalyticsReportRequestDTO requestDTO = createRequestDTO();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(analyticsReportRepository.save(any(AnalyticsReport.class))).thenReturn(savedReport);

        AnalyticsReportResponseDTO result = analyticsReportService.generateReport(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getReportId());
        verify(analyticsReportRepository, times(1)).save(any(AnalyticsReport.class));
    }

    @Test
    void testGetReportById_Success() {
        AnalyticsReport report = createReportEntity(1L);

        when(analyticsReportRepository.findById(anyLong())).thenReturn(Optional.of(report));

        Optional<AnalyticsReportResponseDTO> result = analyticsReportService.getReportById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getReportId());
    }

    @Test
    void testGetAllReports_Success() {
        List<AnalyticsReport> reports = List.of(createReportEntity(1L), createReportEntity(2L));

        when(analyticsReportRepository.findAll()).thenReturn(reports);

        List<AnalyticsReportResponseDTO> result = analyticsReportService.getAllReports();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getReportId());
    }

    @Test
    void testUpdateReport_Success() {
        AnalyticsReport existingReport = createReportEntity(1L);
        AnalyticsReportRequestDTO requestDTO = createRequestDTO();

        when(analyticsReportRepository.findById(anyLong())).thenReturn(Optional.of(existingReport));
        when(analyticsReportRepository.save(any(AnalyticsReport.class))).thenReturn(existingReport);

        AnalyticsReportResponseDTO result = analyticsReportService.updateReport(1L, requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getReportId());
        verify(analyticsReportRepository, times(1)).save(any(AnalyticsReport.class));
    }

    @Test
    void testUpdateReport_NotFound() {
        when(analyticsReportRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> analyticsReportService.updateReport(1L, createRequestDTO()));
        verify(analyticsReportRepository, never()).save(any(AnalyticsReport.class));
    }

    @Test
    void testDeleteReport_Success() {
        // Mock a found report to allow the delete operation
        when(analyticsReportRepository.findById(anyLong())).thenReturn(Optional.of(createReportEntity(1L)));

        analyticsReportService.deleteReport(1L);

        // Verify the delete method was called on the repository
        verify(analyticsReportRepository, times(1)).deleteById(1L);
    }
}