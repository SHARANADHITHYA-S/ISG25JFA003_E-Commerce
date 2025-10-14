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
            case 'COMPLETED':
                return 'bg-success';
            case 'CANCELLED':
                return 'bg-danger';
            default:
                return 'bg-secondary';
        }
    }
}
