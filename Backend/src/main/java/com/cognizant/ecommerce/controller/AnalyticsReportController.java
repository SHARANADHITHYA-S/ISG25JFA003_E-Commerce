package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportRequestDTO;
import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportResponseDTO;
import com.cognizant.ecommerce.service.AnalyticsReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics-reports/admin")
@RequiredArgsConstructor
public class AnalyticsReportController {

    private final AnalyticsReportService analyticsReportService;

    // GET all reports
    @GetMapping
    public ResponseEntity<List<AnalyticsReportResponseDTO>> getAllReports() {
        List<AnalyticsReportResponseDTO> reports = analyticsReportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    // GET a single report by ID
    @GetMapping("/{reportId}")
    public ResponseEntity<AnalyticsReportResponseDTO> getReportById(@PathVariable Long reportId) {
        return analyticsReportService.getReportById(reportId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST to create a new report (RESTful way)
    @PostMapping
    public ResponseEntity<AnalyticsReportResponseDTO> createReport(@RequestBody AnalyticsReportRequestDTO requestDTO) {
        AnalyticsReportResponseDTO newReport = analyticsReportService.createReport(requestDTO);
        return new ResponseEntity<>(newReport, HttpStatus.CREATED);
    }

    // POST to generate a report (from your original code)
    @PostMapping("/generate")
    public ResponseEntity<AnalyticsReportResponseDTO> generateReport(@RequestBody AnalyticsReportRequestDTO requestDTO) {
        AnalyticsReportResponseDTO report = analyticsReportService.generateReport(requestDTO);
        return new ResponseEntity<>(report, HttpStatus.CREATED);
    }

    // PUT to update an existing report
    @PutMapping("/{reportId}")
    public ResponseEntity<AnalyticsReportResponseDTO> updateReport(@PathVariable Long reportId, @RequestBody AnalyticsReportRequestDTO requestDTO) {
        AnalyticsReportResponseDTO updatedReport = analyticsReportService.updateReport(reportId, requestDTO);
        return ResponseEntity.ok(updatedReport);
    }

    // DELETE a report by ID
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long reportId) {
        analyticsReportService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }
}