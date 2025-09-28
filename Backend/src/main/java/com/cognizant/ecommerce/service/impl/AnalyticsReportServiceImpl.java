package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.AnalyticsReportRepository;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportRequestDTO;
import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportResponseDTO;
import com.cognizant.ecommerce.exception.AnalyticsReportGenerationException;
import com.cognizant.ecommerce.exception.AnalyticsReportNotFoundException;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.AnalyticsReport;
import com.cognizant.ecommerce.model.User;
import com.cognizant.ecommerce.service.AnalyticsReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnalyticsReportServiceImpl implements AnalyticsReportService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsReportServiceImpl.class);

    private final AnalyticsReportRepository analyticsReportRepository;
    private final UserRepository userRepository;

    @Autowired
    public AnalyticsReportServiceImpl(AnalyticsReportRepository analyticsReportRepository, UserRepository userRepository) {
        this.analyticsReportRepository = analyticsReportRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<AnalyticsReportResponseDTO> getReportById(Long id) {
        AnalyticsReport report = analyticsReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analytics report not found with ID: " + id));
        return Optional.of(mapToResponseDTO(report));
    }

    @Override
    public List<AnalyticsReportResponseDTO> getAllReports() {
        return analyticsReportRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReport(Long reportId) {
        AnalyticsReport report = analyticsReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Analytics report not found with ID: " + reportId));
        analyticsReportRepository.delete(report);
    }

    @Override
    public AnalyticsReportResponseDTO generateReport(AnalyticsReportRequestDTO requestDTO) {
        if (requestDTO.getReportType() == null || requestDTO.getReportType().isBlank()) {
            throw new AnalyticsReportGenerationException("Report type must not be null or empty");
        }

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: 1"));

        AnalyticsReport report = new AnalyticsReport();
        report.setGeneratedBy(user);
        report.setReport_type(requestDTO.getReportType());
        report.setData("Generated report data for " + requestDTO.getReportType());
        report.setGenerated_at(new Date());

        AnalyticsReport savedReport = analyticsReportRepository.save(report);
        return mapToResponseDTO(savedReport);
    }

    @Override
    public AnalyticsReportResponseDTO updateReport(Long reportId, AnalyticsReportRequestDTO requestDTO) {
        AnalyticsReport existingReport = analyticsReportRepository.findById(reportId)
                .orElseThrow(() -> new AnalyticsReportNotFoundException("Report not found with ID: " + reportId));

        existingReport.setReport_type(requestDTO.getReportType());
        existingReport.setData("Updated report data for " + requestDTO.getReportType());

        AnalyticsReport updatedReport = analyticsReportRepository.save(existingReport);
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
