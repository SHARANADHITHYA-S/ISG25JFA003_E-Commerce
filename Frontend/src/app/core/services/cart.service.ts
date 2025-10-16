import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CartResponse, CartItemRequest, CartItemResponse } from '../models/cart';
import { AuthService } from './auth.service'; // Import AuthService

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient, private authService: AuthService) { }

  private getUserIdFromAuth(): number {
    const user = this.authService.getCurrentUser();
    if (user && user.id) {
      return user.id;
    }
    throw new Error('User not logged in or user ID not available.');
  }

  getCart(): Observable<CartResponse> {
    const userId = this.getUserIdFromAuth();
    return this.http.get<CartResponse>(`${this.apiUrl}/carts/user/${userId}`).pipe(
      catchError(this.handleError)
    );
  }

  addCartItem(productId: number, quantity: number): Observable<CartItemResponse> {
    const userId = this.getUserIdFromAuth();
    const itemRequest: CartItemRequest = { productId, quantity };
    return this.http.post<CartItemResponse>(`${this.apiUrl}/cart-items/${userId}`, itemRequest).pipe(
      catchError(this.handleError)
    );
  }

  updateCartItem(itemId: number, itemRequest: CartItemRequest): Observable<CartItemResponse> {
    return this.http.put<CartItemResponse>(`${this.apiUrl}/cart-items/${itemId}`, itemRequest).pipe(
      catchError(this.handleError)
    );
  }

  removeCartItem(itemId: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/cart-items/${itemId}`, { responseType: 'text' }).pipe(
      catchError(this.handleError)
    );
  }

  clearCart(): Observable<string> {
    const userId = this.getUserIdFromAuth();
    return this.http.delete(`${this.apiUrl}/carts/user/${userId}`, { responseType: 'text' }).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: any) {
    console.error('An API error occurred', error);
    return throwError(() => new Error('Something went wrong with the API call.'));
  }
}