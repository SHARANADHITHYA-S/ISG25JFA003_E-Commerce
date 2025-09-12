package com.cognizant.ecommerce.dto.analyticsreport;

import java.util.Date;

/**
 * Request DTO for generating an analytics report.
 * Contains the parameters needed to filter the report data.
 */
public class AnalyticsReportRequestDTO {
    private Date startDate;
    private Date endDate;
    private String reportType; // e.g., "SALES", "USER_GROWTH"
    private String filterBy; // e.g., "category=electronics"

    // Getters and setters
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getFilterBy() {
        return filterBy;
    }

    public void setFilterBy(String filterBy) {
        this.filterBy = filterBy;
    }
}
