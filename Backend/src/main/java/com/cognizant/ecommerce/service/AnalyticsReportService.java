package com.cognizant.ecommerce.service;

import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportRequestDTO;
import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportResponseDTO;
import java.util.Optional;

public interface AnalyticsReportService {
    AnalyticsReportResponseDTO generateReport(AnalyticsReportRequestDTO requestDTO);
    Optional<AnalyticsReportResponseDTO> getReportById(Long reportId);
}