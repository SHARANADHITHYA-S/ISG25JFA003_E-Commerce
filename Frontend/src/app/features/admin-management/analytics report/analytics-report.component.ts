import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AnalyticsService } from '../../../core/services/analytics.service';
import { AnalyticsData } from '../../../core/models/analytics';

@Component({
  selector: 'app-analytics-report',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './analytics-report.component.html',
  styleUrls: ['./analytics-report.component.scss']
})
export class AnalyticsReportComponent implements OnInit {
  analyticsData: AnalyticsData[] = [];
  loading = true;
  error: string | null = null;
  newReportType = '';

  constructor(private analyticsService: AnalyticsService) { }

  ngOnInit(): void {
    this.fetchAnalyticsData();
  }

  fetchAnalyticsData(): void {
    this.loading = true;
    this.error = null;
    this.analyticsService.getAllReports().subscribe({
      next: (data: AnalyticsData[]) => {
        this.analyticsData = data;
        this.loading = false;
      },
      error: (err: any) => {
        this.error = 'Failed to load analytics data. Please try again later.';
        this.loading = false;
        console.error('Error fetching analytics data:', err);
      }
    });
  }

  generateReport(): void {
    if (!this.newReportType.trim()) {
      this.error = 'Please enter a report type.';
      return;
    }

    this.loading = true;
    this.error = null;
    this.analyticsService.generateReport(this.newReportType).subscribe({
      next: (report: AnalyticsData) => {
        this.analyticsData.push(report);
        this.newReportType = '';
        this.loading = false;
      },
      error: (err: any) => {
        this.error = 'Failed to generate report. Please try again.';
        this.loading = false;
        console.error('Error generating report:', err);
      }
    });
  }

  deleteReport(reportId: number): void {
    if (confirm('Are you sure you want to delete this report?')) {
      this.loading = true;
      this.analyticsService.deleteReport(reportId).subscribe({
        next: () => {
          this.analyticsData = this.analyticsData.filter(report => report.reportId !== reportId);
          this.loading = false;
        },
        error: (err: any) => {
          this.error = 'Failed to delete report. Please try again.';
          this.loading = false;
          console.error('Error deleting report:', err);
        }
      });
    }
  }
}
