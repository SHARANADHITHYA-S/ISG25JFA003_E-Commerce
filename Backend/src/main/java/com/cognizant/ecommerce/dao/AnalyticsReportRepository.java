package com.cognizant.ecommerce.dao;

import com.cognizant.ecommerce.model.AnalyticsReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AnalyticsReportRepository extends JpaRepository<AnalyticsReport, Long> {
}
