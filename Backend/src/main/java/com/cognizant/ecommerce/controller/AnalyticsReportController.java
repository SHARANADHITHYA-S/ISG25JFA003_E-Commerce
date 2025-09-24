package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportRequestDTO;
import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportResponseDTO;
import com.cognizant.ecommerce.service.AnalyticsReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/admin/generate")
    public ResponseEntity<AnalyticsReportResponseDTO> generateReport(@RequestBody AnalyticsReportRequestDTO requestDTO) {
        AnalyticsReportResponseDTO report = analyticsReportService.generateReport(requestDTO);
        return new ResponseEntity<>(report, HttpStatus.CREATED);
    }

    // Endpoint for administrators to retrieve all reports
    @GetMapping("/admin")
    public ResponseEntity<List<AnalyticsReportResponseDTO>> getAllReports() {
        List<AnalyticsReportResponseDTO> reports = analyticsReportService.getAllReports();
        return ok(reports);
    }

    // Endpoint for administrators to get a specific report by ID
    @GetMapping("/admin/{reportId}")
    public ResponseEntity<Optional<AnalyticsReportResponseDTO>> getReportById(@PathVariable Long reportId) {
        // Service layer handles the 'not found' exception, which is caught by the GlobalExceptionHandler
        Optional<AnalyticsReportResponseDTO> report = analyticsReportService.getReportById(reportId);
        return ok(report);
    }

    // Endpoint for administrators to update a report
    @PutMapping("/admin/{reportId}")
    public ResponseEntity<AnalyticsReportResponseDTO> updateReport(@PathVariable Long reportId, @RequestBody AnalyticsReportRequestDTO requestDTO) {
        AnalyticsReportResponseDTO updatedReport = analyticsReportService.updateReport(reportId, requestDTO);
        return ok(updatedReport);
    }

    // Endpoint for administrators to delete a report
    @DeleteMapping("/admin/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long reportId) {
        analyticsReportService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }
}