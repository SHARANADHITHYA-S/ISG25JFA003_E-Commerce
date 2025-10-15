import { Component, OnInit } from '@angular/core';
import { OrderService, PaginatedOrderResponse } from '../../../../core/services/order.service';
import { Order, OrderItem, OrderStatus } from '../../../../shared/models/order.model';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from '../../../../shared/components/loader/loader.component';
import { ErrorMessageComponent } from '../../../../shared/components/error-message/error-message.component';

@Component({
    selector: 'app-order-history',
    standalone: true,
    imports: [CommonModule, LoaderComponent, ErrorMessageComponent],
    templateUrl: './order-history.component.html',
    styleUrls: ['./order-history.component.scss']
})
export class OrderHistoryComponent implements OnInit {
    paginatedOrders: PaginatedOrderResponse | null = null;
    loading = false;
    error: string | null = null;
    expandedOrderId: number | null = null;

    constructor(private orderService: OrderService) { }

    ngOnInit(): void {
        this.loadOrders(0);
    }

    loadOrders(page: number): void {
        this.loading = true;
        this.error = null;
        this.orderService.getOrdersByPage(page, 5).subscribe({
            next: (response: PaginatedOrderResponse) => {
                this.paginatedOrders = response;
                this.loading = false;
            },
            error: (error: any) => {
                console.error('Error loading orders:', error);
                this.error = 'Failed to load order history. Please try again.';
                this.loading = false;
            }
        });
    }

    toggleOrderDetails(orderId: number): void {
        this.expandedOrderId = this.expandedOrderId === orderId ? null : orderId;
    }

    onPageChange(page: number): void {
        this.loadOrders(page);
    }

    get totalPages(): number {
        return this.paginatedOrders?.totalPages || 0;
    }

    getBadgeClass(status: OrderStatus): string {
        switch (status) {
            case 'PENDING':
                return 'bg-warning';
            case 'PROCESSING':
                return 'bg-info';
            case 'SHIPPED':
                return 'bg-primary'; // Example color for SHIPPED
            case 'DELIVERED':
                return 'bg-success'; // Example color for DELIVERED
            case 'COMPLETED':
                return 'bg-success';
            case 'CANCELLED':
                return 'bg-danger';
            default:
                return 'bg-secondary';
        }
    }

    getShadowColor(status: OrderStatus): string {
        switch (status) {
            case 'PENDING':
                return 'rgba(255, 193, 7, 0.3)'; // Yellowish for pending
            case 'PROCESSING':
                return 'rgba(23, 162, 184, 0.3)'; // Bluish for processing
            case 'SHIPPED':
                return 'rgba(0, 123, 255, 0.3)'; // Blue for shipped
            case 'DELIVERED':
                return 'rgba(40, 167, 69, 0.3)'; // Greenish for delivered
            case 'COMPLETED':
                return 'rgba(40, 167, 69, 0.3)'; // Greenish for completed
            case 'CANCELLED':
                return 'rgba(220, 53, 69, 0.3)'; // Reddish for cancelled
            default:
                return 'rgba(108, 117, 125, 0.3)'; // Grayish for unknown
        }
    }
}
