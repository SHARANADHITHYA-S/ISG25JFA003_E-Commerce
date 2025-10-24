import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of, throwError, forkJoin } from 'rxjs';
import { map, catchError, tap, switchMap } from 'rxjs/operators';
import { Order } from '../../shared/models/order.model';
import { AuthService } from './auth.service';
import { NotificationService } from './notification.service';
import { ProductService } from './product.service';

export interface PaginatedOrderResponse {
    content: Order[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}

// Define OrderResponseDTO interface
export interface OrderResponseDTO {
  id: number;
  userId: number;
  addressId: number;
  paymentMethodId: number;
  totalAmount: number;
  status: string;
  placed_at: string;
  orderItems: any[]; // Define a proper interface for OrderItem if needed
  deliveryDate?: string;
}

@Injectable({
    providedIn: 'root'
})
export class OrderService {
    private apiUrl = 'http://localhost:8080/api/orders';

    constructor(private http: HttpClient, private authService: AuthService, private notificationService: NotificationService, private productService: ProductService) { }

    private getUserIdFromAuth(): number {
        const user = this.authService.getCurrentUser();
        if (user && user.id) {
            return user.id;
        }
        throw new Error('User not logged in or user ID not available.');
    }

    createOrder(addressId: number, paymentMethodId: number): Observable<OrderResponseDTO> {
        let userId: number;
        try {
            userId = this.getUserIdFromAuth();
        } catch (error: any) {
            this.notificationService.showError('Authentication required to create order. Please log in.');
            return throwError(() => new Error('Authentication required to create order. Please log in.'));
        }

        const orderRequest = {
            userid: userId,
            addressId: addressId,
            paymentMethodId: paymentMethodId
        };

        return this.http.post<OrderResponseDTO>(this.apiUrl, orderRequest).pipe(
            tap(() => this.notificationService.showSuccess('Order placed successfully')),
            catchError(err => {
                this.notificationService.showError('Failed to place order');
                return this.handleError(err);
            })
        );
    }

    getCurrentOrder(): Observable<Order | null> {
        let userId: number;
        try {
            userId = this.getUserIdFromAuth();
        } catch (error: any) {
            return throwError(() => new Error('Authentication required to load current order. Please log in.'));
        }
    
        return this.http.get<Order[]>(`${this.apiUrl}/user/${userId}`).pipe(
            switchMap(orders => {
                if (orders && orders.length > 0) {
                    const sortedOrders = orders.sort((a, b) => new Date(b.placed_at).getTime() - new Date(a.placed_at).getTime());
                    const currentOrder = sortedOrders.find(o => o.status !== 'COMPLETED' && o.status !== 'CANCELLED');

                    if (currentOrder && currentOrder.orderItems && currentOrder.orderItems.length > 0) {
                        const productDetailsRequests = currentOrder.orderItems.map(item =>
                            this.productService.getProductById(item.productId).pipe(
                                map(product => ({ ...item, product }))
                            )
                        );
                        return forkJoin(productDetailsRequests).pipe(
                            map(itemsWithProduct => ({ ...currentOrder, orderItems: itemsWithProduct, deliveryDate: currentOrder.deliveryDate }))
                        );
                    } else {
                        return of(currentOrder || null);
                    }
                } else {
                    return of(null);
                }
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
            switchMap(orders => {
                const previousOrders = orders.filter(o => o.status === 'COMPLETED' || o.status === 'CANCELLED');
                if (previousOrders && previousOrders.length > 0) {
                    const productDetailsRequests = previousOrders.map(order => {
                        if (order.orderItems && order.orderItems.length > 0) {
                            const itemRequests = order.orderItems.map(item =>
                                this.productService.getProductById(item.productId).pipe(
                                    map(product => ({ ...item, product }))
                                )
                            );
                            return forkJoin(itemRequests).pipe(
                                map(itemsWithProduct => ({ ...order, orderItems: itemsWithProduct }))
                            );
                        } else {
                            return of(order);
                        }
                    });
                    return forkJoin(productDetailsRequests);
                } else {
                    return of([]);
                }
            }),
            catchError(this.handleError)
        );
    }

    getOrderDetails(orderId: number): Observable<Order> {
        return this.http.get<Order>(`${this.apiUrl}/${orderId}`).pipe(
            switchMap(order => {
                if (order && order.orderItems && order.orderItems.length > 0) {
                    const productDetailsRequests = order.orderItems.map(item =>
                        this.productService.getProductById(item.productId).pipe(
                            map(product => ({ ...item, product }))
                        )
                    );
                    return forkJoin(productDetailsRequests).pipe(
                        map(itemsWithProduct => ({ ...order, orderItems: itemsWithProduct }))
                    );
                } else {
                    return of(order);
                }
            }),
            catchError(this.handleError)
        );
    }

    updateOrderStatus(orderId: number, status: string, deliveryDate?: string): Observable<Order> {
        const body: any = { status };
        if (deliveryDate) {
            body.deliveryDate = deliveryDate;
        }
        return this.http.put<Order>(`${this.apiUrl}/admin/${orderId}/status`, body).pipe(
            catchError(this.handleError)
        );
    }

    cancelOrder(orderId: number, isPaid: boolean): Observable<Order> {
        const message = isPaid 
            ? 'Order cancelled and amount refunded.' 
            : 'Order cancelled successfully. Items have been returned to your cart.';

        return this.http.delete<Order>(`${this.apiUrl}/${orderId}`).pipe(
            tap(() => this.notificationService.showSuccess(message)),
            catchError(err => {
                this.notificationService.showError('Failed to cancel order');
                return this.handleError(err);
            })
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
            switchMap(orders => {
                if (orders && orders.length > 0) {
                    const ordersWithProductDetails = orders.map(order => {
                        if (order.orderItems && order.orderItems.length > 0) {
                            const itemRequests = order.orderItems.map(item =>
                                this.productService.getProductById(item.productId).pipe(
                                    map(product => ({ ...item, product }))
                                )
                            );
                            return forkJoin(itemRequests).pipe(
                                map(itemsWithProduct => ({ ...order, orderItems: itemsWithProduct, deliveryDate: order.deliveryDate }))
                            );
                        } else {
                            return of(order);
                        }
                    });
                    return forkJoin(ordersWithProductDetails).pipe(
                        map(resolvedOrders => {
                            const startIndex = page * size;
                            const endIndex = startIndex + size;
                            const content = resolvedOrders.slice(startIndex, endIndex);
                            return {
                                content,
                                totalElements: resolvedOrders.length,
                                totalPages: Math.ceil(resolvedOrders.length / size),
                                size,
                                number: page
                            };
                        })
                    );
                } else {
                    return of({
                        content: [],
                        totalElements: 0,
                        totalPages: 0,
                        size,
                        number: page
                    });
                }
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