import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Order, OrderItem } from '../models/order.model';

@Injectable({
    providedIn: 'root'
})
export class MockOrderService {
    private sampleOrders: Order[] = [
        {
            id: 216,
            userId: 94,
            customerId: 218,
            orderDate: new Date('2025-09-27T20:21:44.641Z'),
            createdAt: new Date('2025-09-27T20:21:44.641Z'),
            updatedAt: new Date('2025-09-27T20:26:57.978Z'),
            status: 'COMPLETED',
            total: 50000.00,
            items: [
                {
                    id: 231,
                    productId: 123,
                    productName: 'High-End Laptop',
                    quantity: 2,
                    price: 25000.00,
                    subtotal: 50000.00
                }
            ]
        },
        {
            id: 217,
            userId: 94,
            customerId: 218,
            orderDate: new Date('2025-09-28T10:15:22.641Z'),
            createdAt: new Date('2025-09-28T10:15:22.641Z'),
            updatedAt: new Date('2025-09-28T10:20:35.978Z'),
            status: 'PENDING',
            total: 75000.00,
            items: [
                {
                    id: 232,
                    productId: 124,
                    productName: 'Gaming Desktop',
                    quantity: 1,
                    price: 45000.00,
                    subtotal: 45000.00
                },
                {
                    id: 233,
                    productId: 125,
                    productName: 'Gaming Monitor',
                    quantity: 2,
                    price: 15000.00,
                    subtotal: 30000.00
                }
            ]
        }
    ];

    getCurrentOrder(): Observable<Order> {
        return of(this.sampleOrders.find(order => order.status === 'PENDING')!);
    }

    getPreviousOrders(): Observable<Order[]> {
        return of(this.sampleOrders.filter(order => order.status === 'COMPLETED'));
    }

    getOrderDetails(orderId: number): Observable<Order> {
        return of(this.sampleOrders.find(order => order.id === orderId)!);
    }
}
