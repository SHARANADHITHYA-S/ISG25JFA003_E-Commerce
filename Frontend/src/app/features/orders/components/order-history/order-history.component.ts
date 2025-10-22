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
    styleUrl: './order-history.component.scss'
})
export class OrderHistoryComponent implements OnInit {
    allOrders: Order[] = [];
    loading = false;
    error: string | null = null;
    expandedOrderId: number | null = null;

    constructor(private orderService: OrderService) { }

    ngOnInit(): void {
        this.loadOrders();
    }

    loadOrders(): void {
        this.loading = true;
        this.error = null;
        this.orderService.getOrdersByPage(0, 100).subscribe({
            next: (response: PaginatedOrderResponse) => {
                this.allOrders = response.content;
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

    getProductImage(productName: string): string {
        const imageMap: { [key: string]: string } = {
            'Atomic Habits': '/atomichabitsbk.jpg',
            'The Alchemist': '/thealchemistbk.jpg',
            'Clean Code': '/cleancodebk.jpg',
            'Headset': '/headset.jpg',
            'Kettle': '/kettle.jpg',
            'LED TV': '/ledtv.jpg',
            'Lipstick': '/lipstick.jpg',
            'Pan': '/pan.jpg',
            'Pillow': '/pillow.jpg',
            'Serum': '/serum.jpg',
            'Shampoo': '/shampoo.jpg',
            'Smartwatch': '/smartwatch.jpg'
        };
        
        if (imageMap[productName]) {
            return imageMap[productName];
        }
        
        for (const key in imageMap) {
            if (productName.toLowerCase().includes(key.toLowerCase())) {
                return imageMap[key];
            }
        }
        
        return '/favicon.ico';
    }

    getBadgeClass(status: OrderStatus): string {
        switch (status) {
            case 'PENDING':
                return 'status-pending';
            case 'PROCESSING':
                return 'status-processing';
            case 'SHIPPED':
                return 'status-shipped';
            case 'DELIVERED':
                return 'status-delivered';
            case 'COMPLETED':
                return 'status-completed';
            case 'CANCELLED':
                return 'status-cancelled';
            default:
                return 'status-default';
        }
    }

    trackByOrderId(index: number, order: Order): number {
        return order.id;
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
