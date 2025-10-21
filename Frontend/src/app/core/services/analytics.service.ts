import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AnalyticsData } from '../models/analytics';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private apiUrl = 'http://localhost:8080/api/analytics'; // Adjust URL as needed

  constructor(private http: HttpClient) { }

  getAnalyticsData(): Observable<AnalyticsData> {
    return this.http.get<AnalyticsData>(this.apiUrl);
  }
}
