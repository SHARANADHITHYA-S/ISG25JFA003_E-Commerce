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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<AnalyticsReportResponseDTO> getAllReports() {
        return analyticsReportRepository.findAll().stream()
                .map(this::mapToResponseDTO) // or modelMapper.map(...)
                .collect(Collectors.toList());

    }

    @Override
    public void deleteReport(Long reportId) {

    }

    @Override
    public AnalyticsReportResponseDTO generateReport(AnalyticsReportRequestDTO requestDTO) {
        String reportData = "Generated report data for " + requestDTO.getReportType();

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

    @Override
    public AnalyticsReportResponseDTO updateReport(Long reportId, AnalyticsReportRequestDTO requestDTO) {
        // Find the existing report by ID
        AnalyticsReport existingReport = analyticsReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + reportId));

        // Update the report fields with new data
        existingReport.setReport_type(requestDTO.getReportType());
        existingReport.setData("Updated report data for " + requestDTO.getReportType());

        // Save the updated report entity
        AnalyticsReport updatedReport = analyticsReportRepository.save(existingReport);

        // Convert and return the updated entity as a DTO
        return mapToResponseDTO(updatedReport);
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