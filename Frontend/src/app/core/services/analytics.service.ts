import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AnalyticsData } from '../models/analytics';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private apiUrl = 'http://localhost:8080/api/analytics-reports'; // Adjust URL as needed

  constructor(private http: HttpClient) { }

  getAnalyticsData(): Observable<AnalyticsData> {
    return this.http.get<AnalyticsData>(`${this.apiUrl}/admin/data`);
  }

  getAllReports(): Observable<AnalyticsData[]> {
    return this.http.get<AnalyticsData[]>(`${this.apiUrl}/admin`);
  }

  generateReport(reportType: string): Observable<AnalyticsData> {
    return this.http.post<AnalyticsData>(`${this.apiUrl}/admin/generate`, { reportType });
  }

  getReportById(reportId: number): Observable<AnalyticsData> {
    return this.http.get<AnalyticsData>(`${this.apiUrl}/admin/${reportId}`);
  }

  updateReport(reportId: number, reportType: string): Observable<AnalyticsData> {
    return this.http.put<AnalyticsData>(`${this.apiUrl}/admin/${reportId}`, { reportType });
  }

  deleteReport(reportId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/admin/${reportId}`);
  }
}
