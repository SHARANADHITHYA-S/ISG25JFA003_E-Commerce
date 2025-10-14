package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportRequestDTO;
import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportResponseDTO;
import com.cognizant.ecommerce.exception.AnalyticsReportNotFoundException;
import com.cognizant.ecommerce.service.AnalyticsReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/analytics-reports")
@RequiredArgsConstructor
public class AnalyticsReportController {

    private final AnalyticsReportService analyticsReportService;

    // Endpoint for administrators to generate a new report
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/generate")
    public ResponseEntity<AnalyticsReportResponseDTO> generateReport(@Valid @RequestBody AnalyticsReportRequestDTO requestDTO) {
        AnalyticsReportResponseDTO report = analyticsReportService.generateReport(requestDTO);
        return new ResponseEntity<>(report, HttpStatus.CREATED);
    }

    // Endpoint for administrators to retrieve all reports
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<AnalyticsReportResponseDTO>> getAllReports() {
        List<AnalyticsReportResponseDTO> reports = analyticsReportService.getAllReports();
        return ok(reports);
    }

    // Endpoint for administrators to get a specific report by ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{reportId}")
    public ResponseEntity<AnalyticsReportResponseDTO> getReportById(@Valid @PathVariable Long reportId) {
        AnalyticsReportResponseDTO report = analyticsReportService.getReportById(reportId)
                .orElseThrow(() -> new AnalyticsReportNotFoundException("Report not found with ID: " + reportId));
        return ok(report);
    }

    // Endpoint for administrators to update a report
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{reportId}")
    public ResponseEntity<AnalyticsReportResponseDTO> updateReport(@Valid @PathVariable Long reportId,@Valid @RequestBody AnalyticsReportRequestDTO requestDTO) {
        AnalyticsReportResponseDTO updatedReport = analyticsReportService.updateReport(reportId, requestDTO);
        return ok(updatedReport);
    }

    // Endpoint for administrators to delete a report
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{reportId}")
    public ResponseEntity<Void> deleteReport(@Valid @PathVariable Long reportId) {
        analyticsReportService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }
}