import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Payment, ValidatePaymentRequest } from '../models/payment.model';
import { ApiService } from './api.service';
import { HttpHeaders, HttpParams } from '@angular/common/http';
 
@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  constructor(private apiService: ApiService) {}
 
  createPayment(request: { userId: number; orderId: number }): Observable<Payment> {
    return this.apiService.post<Payment>('/payments', request);
  }

  getPaymentById(id: number): Observable<Payment> {
    return this.apiService.get<Payment>(`/payments/${id}`);
  }

  getPaymentByOrderId(orderId: number): Observable<Payment> {
    return this.apiService.get<Payment>(`/payments/order/${orderId}`);
  }

  updatePaymentStatus(paymentId: number, status: string): Observable<Payment> {
    return this.apiService.put<Payment>(`/payments/admin/${paymentId}/status?status=${status}`, {});
  }

  validatePayment(request: ValidatePaymentRequest, userId: number): Observable<Payment> {
    const headers = new HttpHeaders().set('userId', userId.toString());
    return this.apiService.post<Payment>('/payment/validatePayment', request, headers);
  }
}