import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Order } from '../../shared/models/order.model';

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
    private userId = 212; // Hardcoded user ID

    constructor(private http: HttpClient) { }

    getCurrentOrder(): Observable<Order> {
        return this.http.get<Order[]>(`${this.apiUrl}/user/${this.userId}`).pipe(
            map(orders => {
                const order = orders.find(o => o.status !== 'COMPLETED' && o.status !== 'CANCELLED');
                if (!order) {
                    throw new Error('No current order found');
                }
                return order;
            })
        );
    }

    getPreviousOrders(): Observable<Order[]> {
        return this.http.get<Order[]>(`${this.apiUrl}/user/${this.userId}`).pipe(
            map(orders => orders.filter(o => o.status === 'COMPLETED' || o.status === 'CANCELLED'))
        );
    }

    getOrderDetails(orderId: number): Observable<Order> {
        return this.http.get<Order>(`${this.apiUrl}/${orderId}`);
    }

    getOrdersByPage(page: number, size: number): Observable<PaginatedOrderResponse> {
        return this.http.get<Order[]>(`${this.apiUrl}/user/${this.userId}`).pipe(
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
            })
        );
    }
}
