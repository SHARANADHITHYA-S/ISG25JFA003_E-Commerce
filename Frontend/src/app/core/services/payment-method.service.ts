import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface PaymentMethod {
  paymentMethodId: number; // Changed to match backend response
  cardType: string;
  lastFourDigits: string;
  cardholderName: string;
}

export interface PaymentMethodRequest {
  cardType: string;
  cardNumber: string;
  expirationDate: string;
  cardholderName: string;
  default: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class PaymentMethodService {
  private apiUrl = 'http://localhost:8080/api/payment-methods';

  constructor(private http: HttpClient) { }

  getPaymentMethodsByUserId(userId: number): Observable<PaymentMethod[]> {
    return this.http.get<PaymentMethod[]>(`${this.apiUrl}/user/${userId}`).pipe(
      catchError(this.handleError)
    );
  }

  addPaymentMethod(userId: number, paymentMethod: PaymentMethodRequest): Observable<PaymentMethod> {
    return this.http.post<PaymentMethod>(`${this.apiUrl}/users/${userId}`, paymentMethod).pipe(
      catchError(this.handleError)
    );
  }

  updatePaymentMethod(paymentMethodId: number, paymentMethod: PaymentMethodRequest): Observable<PaymentMethod> {
    return this.http.put<PaymentMethod>(`${this.apiUrl}/${paymentMethodId}`, paymentMethod).pipe(
      catchError(this.handleError)
    );
  }

  deletePaymentMethod(paymentMethodId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${paymentMethodId}`).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: any) {
    console.error('An API error occurred in PaymentMethodService', error);
    let errorMessage = 'Something went wrong with the API call.';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else if (error.status) {
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    } else if (error.message) {
      errorMessage = error.message;
    }
    return throwError(() => new Error(errorMessage));
  }
}
