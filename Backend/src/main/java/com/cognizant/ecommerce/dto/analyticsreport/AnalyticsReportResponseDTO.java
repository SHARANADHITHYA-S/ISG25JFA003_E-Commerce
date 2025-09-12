package com.cognizant.ecommerce.dto.analyticsreport;

import java.util.Date;

/**
 * Response DTO for providing the generated analytics report.
 * Contains read-only metadata and the report content.
 */
public class AnalyticsReportResponseDTO {
    private Long reportId;
    private String reportName;
    private Date creationDate;
    private String reportData; // In a real app, this might be a complex data structure like a List<Map>

    // Getters and setters
    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getReportData() {
        return reportData;
    }

    public void setReportData(String reportData) {
        this.reportData = reportData;
    }
}
