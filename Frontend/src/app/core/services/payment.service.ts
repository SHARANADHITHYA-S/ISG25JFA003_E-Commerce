import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Payment, ValidatePaymentRequest } from '../models/payment.model';
import { ApiService } from './api.service';
import { HttpHeaders, HttpParams } from '@angular/common/http';
import { NotificationService } from './notification.service';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  constructor(private apiService: ApiService, private notificationService: NotificationService) {}

  createPayment(request: { userId: number; orderId: number }): Observable<Payment> {
    return this.apiService.post<Payment>('/payments', request).pipe(
      tap(() => this.notificationService.showSuccess('Payment initiated')),
      catchError(err => {
        this.notificationService.showError('Failed to initiate payment');
        return throwError(() => new Error(err));
      })
    );
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
    return this.apiService.post<Payment>('/payment/validatePayment', request, headers).pipe(
      tap(() => this.notificationService.showSuccess('Payment successful')),
      catchError(err => {
        this.notificationService.showError('Failed to validate payment');
        return throwError(() => new Error(err));
      })
    );
  }

  /**
   * Simulate payment verification for test mode
   * In production, this would call Razorpay's verification API
   */
  verifyPaymentTestMode(paymentId: number, razorpayData: any): Observable<Payment> {
    return new Observable<Payment>((observer) => {
      // Simulate verification delay (like real API call)
      setTimeout(() => {
        // For test mode, we'll assume success 95% of the time
        // This simulates real-world scenarios where payments might fail
        const isSuccess = Math.random() > 0.05;

        if (isSuccess) {
          // Simulate successful verification - update payment status
          this.updatePaymentStatus(paymentId, 'SUCCESS').subscribe({
            next: (payment) => {
              observer.next(payment);
              observer.complete();
            },
            error: (error) => {
              observer.error(error);
            }
          });
        } else {
          // Simulate verification failure
          observer.error(new Error('Payment verification failed - payment may not have been processed'));
        }
      }, 2000); // 2 second delay to simulate API call
    });
  }
}