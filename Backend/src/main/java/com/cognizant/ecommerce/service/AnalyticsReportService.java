package com.cognizant.ecommerce.service;

import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportRequestDTO;
import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportResponseDTO;

import java.util.List;
import java.util.Optional;

public interface AnalyticsReportService {
    // Method for POST mapping
    AnalyticsReportResponseDTO generateReport(AnalyticsReportRequestDTO requestDTO);

    // Method for PUT mapping
    AnalyticsReportResponseDTO updateReport(Long reportId, AnalyticsReportRequestDTO requestDTO);

    Optional<AnalyticsReportResponseDTO> getReportById(Long reportId);

    List<AnalyticsReportResponseDTO> getAllReports();

    void deleteReport(Long reportId);
}