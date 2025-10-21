import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalyticsService } from '../../../core/services/analytics.service';
import { AnalyticsData } from '../../../core/models/analytics';

@Component({
  selector: 'app-analytics-report',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './analytics-report.component.html',
  styleUrls: ['./analytics-report.component.scss']
})
export class AnalyticsReportComponent implements OnInit {
  analyticsData: AnalyticsData | null = null;
  loading = true;
  error: string | null = null;

  constructor(private analyticsService: AnalyticsService) { }

  ngOnInit(): void {
    this.fetchAnalyticsData();
  }

  fetchAnalyticsData(): void {
    this.loading = true;
    this.error = null;
    this.analyticsService.getAnalyticsData().subscribe({
      next: (data: AnalyticsData) => {
        this.analyticsData = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load analytics data. Please try again later.';
        this.loading = false;
        console.error('Error fetching analytics data:', err);
      }
    });
  }
}
