package com.cognizant.ecommerce.dto.analyticsreport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO for generating an analytics report.
 * Contains the parameters needed to filter the report data.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsReportRequestDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String reportType; // e.g., "SALES", "USER_GROWTH"
    private String filterBy; // e.g., "category=electronics"

}
