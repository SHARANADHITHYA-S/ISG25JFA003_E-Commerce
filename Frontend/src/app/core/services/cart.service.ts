import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError, forkJoin, of, BehaviorSubject } from 'rxjs';
import { catchError, tap, switchMap, map } from 'rxjs/operators';
import { CartResponse, CartItemRequest, CartItemResponse } from '../models/cart';
import { AuthService } from './auth.service'; // Import AuthService
import { NotificationService } from './notification.service';
import { ProductService } from './product.service'; // Import ProductService

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = 'http://localhost:8080/api';
  private cartUpdated$ = new BehaviorSubject<void>(undefined);
  
  // Observable to notify components when cart is updated
  public cartChanges$ = this.cartUpdated$.asObservable();

  constructor(private http: HttpClient, private authService: AuthService, private notificationService: NotificationService, private productService: ProductService) { }

  private getUserIdFromAuth(): number {
    const user = this.authService.getCurrentUser();
    if (user && user.id) {
      return user.id;
    }
    throw new Error('User not logged in or user ID not available.');
  }

  // Public method to notify cart updates from external services (e.g., OrderService)
  public notifyCartUpdate(): void {
    this.cartUpdated$.next();
  }

  getCart(): Observable<CartResponse> {
    const userId = this.getUserIdFromAuth();
    return this.http.get<CartResponse>(`${this.apiUrl}/carts/user/${userId}`).pipe(
      switchMap(cart => {
        if (cart && cart.items && cart.items.length > 0) {
          const productDetailsRequests = cart.items.map(item =>
            this.productService.getProductById(item.productId).pipe(
              map(product => ({ ...item, product }))
            )
          );
          return forkJoin(productDetailsRequests).pipe(
            map(itemsWithProduct => ({ ...cart, items: itemsWithProduct }))
          );
        } else {
          return of(cart);
        }
      }),
      catchError(this.handleError)
    );
  }

  addCartItem(productId: number, quantity: number): Observable<CartItemResponse> {
    const userId = this.getUserIdFromAuth();
    const itemRequest: CartItemRequest = { productId, quantity };
    return this.http.post<CartItemResponse>(`${this.apiUrl}/cart-items/${userId}`, itemRequest).pipe(
      tap(() => {
        this.notificationService.showSuccess('🛒 Product added to cart successfully!');
        this.cartUpdated$.next();
      }),
      catchError(err => {
        this.notificationService.showError('❌ Failed to add Product to cart. Please try again.');
        return this.handleError(err);
      })
    );
  }

  updateCartItem(itemId: number, itemRequest: CartItemRequest): Observable<CartItemResponse> {
    return this.http.put<CartItemResponse>(`${this.apiUrl}/cart-items/${itemId}`, itemRequest).pipe(
      switchMap(updatedItem => {
        return this.productService.getProductById(updatedItem.productId).pipe(
          map(product => ({ ...updatedItem, product }))
        );
      }),
      tap(() => {
        this.notificationService.showSuccess('✅ Cart updated successfully!');
        this.cartUpdated$.next();
      }),
      catchError(this.handleError)
    );
  }

  removeCartItem(itemId: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/cart-items/${itemId}`, { responseType: 'text' }).pipe(
      tap(() => {
        this.notificationService.showSuccess('🗑️ Product removed from cart');
        this.cartUpdated$.next();
      }),
      catchError(err => {
        this.notificationService.showError('❌ Failed to remove Product from cart');
        return this.handleError(err);
      })
    );
  }

  clearCart(): Observable<string> {
    const userId = this.getUserIdFromAuth();
    return this.http.delete(`${this.apiUrl}/carts/user/${userId}`, { responseType: 'text' }).pipe(
      tap(() => {
        this.notificationService.showSuccess('🧹 Cart cleared successfully!');
        this.cartUpdated$.next();
      }),
      catchError(err => {
        this.notificationService.showError('❌ Failed to clear cart');
        return this.handleError(err);
      })
    );
  }

  private handleError(error: any) {
    console.error('An API error occurred', error);
    return throwError(() => new Error('Something went wrong with the API call.'));
  }
}