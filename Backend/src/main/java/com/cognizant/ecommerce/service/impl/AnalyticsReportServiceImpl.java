package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.AnalyticsReportRepository;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportRequestDTO;
import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportResponseDTO;
import com.cognizant.ecommerce.model.AnalyticsReport;
import com.cognizant.ecommerce.model.User;
import com.cognizant.ecommerce.service.AnalyticsReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AnalyticsReportServiceImpl implements AnalyticsReportService {

    private final AnalyticsReportRepository analyticsReportRepository;
    private final UserRepository userRepository;

    @Autowired
    public AnalyticsReportServiceImpl(AnalyticsReportRepository analyticsReportRepository, UserRepository userRepository) {
        this.analyticsReportRepository = analyticsReportRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<AnalyticsReportResponseDTO> getReportById(Long id) {
        return analyticsReportRepository.findById(id).map(this::mapToResponseDTO);
    }

    @Override
    public AnalyticsReportResponseDTO generateReport(AnalyticsReportRequestDTO requestDTO) {
        // Here, you would have the complex logic to generate the report data
        // For this example, we'll use a placeholder.
        String reportData = "Generated report data for " + requestDTO.getReportType();

        // Assuming a user with ID 1 exists to link the report to.
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AnalyticsReport report = new AnalyticsReport();
        report.setGeneratedBy(user);
        report.setReport_type(requestDTO.getReportType());
        report.setData(reportData);
        report.setGenerated_at(new Date());

        AnalyticsReport savedReport = analyticsReportRepository.save(report);
        return mapToResponseDTO(savedReport);
    }

    private AnalyticsReportResponseDTO mapToResponseDTO(AnalyticsReport report) {
        AnalyticsReportResponseDTO responseDTO = new AnalyticsReportResponseDTO();
        responseDTO.setReportId(report.getId());
        responseDTO.setReportName(report.getReport_type() + " Report");
        responseDTO.setCreationDate(report.getGenerated_at());
        responseDTO.setReportData(report.getData());
        return responseDTO;
    }
}