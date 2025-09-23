package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportRequestDTO;
import com.cognizant.ecommerce.dto.analyticsreport.AnalyticsReportResponseDTO;
import com.cognizant.ecommerce.service.AnalyticsReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics-reports/admin")
@RequiredArgsConstructor
public class AnalyticsReportController {

    private final AnalyticsReportService analyticsReportService;

    @PostMapping("/generate")
    public ResponseEntity<AnalyticsReportResponseDTO> generateReport(@RequestBody AnalyticsReportRequestDTO requestDTO) {
        AnalyticsReportResponseDTO report = analyticsReportService.generateReport(requestDTO);
        return new ResponseEntity<>(report, HttpStatus.CREATED);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<AnalyticsReportResponseDTO> getReportById(@PathVariable Long reportId) {
        return analyticsReportService.getReportById(reportId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}