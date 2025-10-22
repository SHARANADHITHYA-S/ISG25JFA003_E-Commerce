import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { CartResponse, CartItemRequest, CartItemResponse } from '../models/cart';
import { AuthService } from './auth.service'; // Import AuthService
import { NotificationService } from './notification.service';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient, private authService: AuthService, private notificationService: NotificationService) { }

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
      tap(() => this.notificationService.showSuccess('Item added to cart')),
      catchError(err => {
        this.notificationService.showError('Failed to add item to cart');
        return this.handleError(err);
      })
    );
  }

  updateCartItem(itemId: number, itemRequest: CartItemRequest): Observable<CartItemResponse> {
    return this.http.put<CartItemResponse>(`${this.apiUrl}/cart-items/${itemId}`, itemRequest).pipe(
      catchError(this.handleError)
    );
  }

  removeCartItem(itemId: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/cart-items/${itemId}`, { responseType: 'text' }).pipe(
      tap(() => this.notificationService.showSuccess('Item removed from cart')),
      catchError(err => {
        this.notificationService.showError('Failed to remove item from cart');
        return this.handleError(err);
      })
    );
  }

  clearCart(): Observable<string> {
    const userId = this.getUserIdFromAuth();
    return this.http.delete(`${this.apiUrl}/carts/user/${userId}`, { responseType: 'text' }).pipe(
      tap(() => this.notificationService.showSuccess('Cart cleared')),
      catchError(err => {
        this.notificationService.showError('Failed to clear cart');
        return this.handleError(err);
      })
    );
  }

  private handleError(error: any) {
    console.error('An API error occurred', error);
    return throwError(() => new Error('Something went wrong with the API call.'));
  }
}