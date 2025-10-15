import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Order } from '../../shared/models/order.model';
import { AuthService } from './auth.service';

export interface PaginatedOrderResponse {
    content: Order[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}

@Injectable({
    providedIn: 'root'
})
export class OrderService {
    private apiUrl = 'http://localhost:8080/api/orders';

    constructor(private http: HttpClient, private authService: AuthService) { }

    private getUserIdFromAuth(): number {
        const user = this.authService.getCurrentUser();
        if (user && user.id) {
            return user.id;
        }
        throw new Error('User not logged in or user ID not available.');
    }

    getCurrentOrder(): Observable<Order | null> {
        let userId: number;
        try {
            userId = this.getUserIdFromAuth();
        } catch (error: any) {
            return throwError(() => new Error('Authentication required to load current order. Please log in.'));
        }

        return this.http.get<Order[]>(`${this.apiUrl}/user/${userId}`).pipe(
            map(orders => {
                const order = orders.find(o => o.status !== 'COMPLETED' && o.status !== 'CANCELLED');
                if (!order) {
                    return null;
                }
                return order;
            }),
            catchError(this.handleError)
        );
    }

    getPreviousOrders(): Observable<Order[]> {
        let userId: number;
        try {
            userId = this.getUserIdFromAuth();
        } catch (error: any) {
            return throwError(() => new Error('Authentication required to load previous orders. Please log in.'));
        }

        return this.http.get<Order[]>(`${this.apiUrl}/user/${userId}`).pipe(
            map(orders => orders.filter(o => o.status === 'COMPLETED' || o.status === 'CANCELLED')),
            catchError(this.handleError)
        );
    }

    getOrderDetails(orderId: number): Observable<Order> {
        return this.http.get<Order>(`${this.apiUrl}/${orderId}`).pipe(
            catchError(this.handleError)
        );
    }

    getOrdersByPage(page: number, size: number): Observable<PaginatedOrderResponse> {
        let userId: number;
        try {
            userId = this.getUserIdFromAuth();
        } catch (error: any) {
            return throwError(() => new Error('Authentication required to load paginated orders. Please log in.'));
        }

        return this.http.get<Order[]>(`${this.apiUrl}/user/${userId}`).pipe(
            map(orders => {
                const startIndex = page * size;
                const endIndex = startIndex + size;
                const content = orders.slice(startIndex, endIndex);
                return {
                    content,
                    totalElements: orders.length,
                    totalPages: Math.ceil(orders.length / size),
                    size,
                    number: page
                };
            }),
            catchError(this.handleError)
        );
    }

    private handleError(error: any) {
        console.error('An API error occurred', error);
        let errorMessage = 'Something went wrong with the API call.';
        if (error.error instanceof ErrorEvent) {
            // Client-side error
            errorMessage = `Error: ${error.error.message}`;
        } else if (error.status) {
            // Backend returned an unsuccessful response code
            errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
        } else if (error.message) {
            // Custom error from getUserIdFromAuth
            errorMessage = error.message;
        }
        return throwError(() => new Error(errorMessage));
    }
}
