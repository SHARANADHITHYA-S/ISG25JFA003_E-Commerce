import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { OrderService, PaginatedOrderResponse } from '../../../../core/services/order.service';
import { Order, OrderItem, OrderStatus } from '../../../../shared/models/order.model';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from '../../../../shared/components/loader/loader.component';
import { ErrorMessageComponent } from '../../../../shared/components/error-message/error-message.component';
import { ShippingTimelineComponent, TimelineEvent } from '../../../../shared/components/shipping-timeline/shipping-timeline.component';

@Component({
    selector: 'app-order-history',
    standalone: true,
    imports: [CommonModule, LoaderComponent, ErrorMessageComponent, ShippingTimelineComponent],
    templateUrl: './order-history.component.html',
    styleUrl: './order-history.component.scss'
})
export class OrderHistoryComponent implements OnInit {
    allOrders: Order[] = [];
    loading = false;
    error: string | null = null;
    expandedOrderId: number | null = null;

    @Output() completePayment = new EventEmitter<Order>();
    @Output() cancelOrder = new EventEmitter<Order>();

    constructor(private orderService: OrderService) { }

    ngOnInit(): void {
        this.loadOrders();
    }

    onCompletePayment(order: Order): void {
        this.completePayment.emit(order);
    }

    onCancelOrder(order: Order): void {
        this.cancelOrder.emit(order);
    }

    refreshOrders(): void {
        this.loadOrders();
    }

    loadOrders(): void {
        this.loading = true;
        this.error = null;
        this.orderService.getOrdersByPage(0, 100).subscribe({
            next: (response: PaginatedOrderResponse) => {
                this.allOrders = response.content.sort((a, b) => new Date(b.placed_at).getTime() - new Date(a.placed_at).getTime());
                this.loading = false;
            },
            error: (error: any) => {
                console.error('Error loading orders:', error);
                this.error = 'Failed to load order history. Please try again.';
                this.loading = false;
            }
        });
    }

    buildTimeline(order: Order): TimelineEvent[] {
        console.log('Building timeline for order in history:', order);
        if (!order) return [];

        if (order.status === 'CANCELLED') {
            return [{
                status: 'CANCELLED',
                date: order.placed_at.toString(),
                isCurrent: true,
                isCompleted: true
            }];
        }

        if (order.status === 'PENDING') return [];

        const statuses: OrderStatus[] = ['PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED'];
        const statusOrder = statuses.indexOf(order.status);
        const placedAt = new Date(order.placed_at);

        return statuses.map((status, index) => {
            let date: Date;
            if (order.deliveryDate) {
                // Calculate dates backwards from delivery date
                const deliveryDate = new Date(order.deliveryDate);
                const daysBeforeDelivery = statuses.length - 1 - index;
                date = new Date(deliveryDate);
                date.setDate(deliveryDate.getDate() - daysBeforeDelivery);
            } else {
                // Fallback to original logic
                date = new Date(placedAt);
                date.setDate(placedAt.getDate() + (index * 2));
            }

            const event: TimelineEvent = {
                status: status,
                date: date.toLocaleDateString(),
                isCurrent: order.status === status,
                isCompleted: index < statusOrder
            };
            return event;
        });
    }

    toggleOrderDetails(orderId: number): void {
        this.expandedOrderId = this.expandedOrderId === orderId ? null : orderId;
    }

    getProductImage(item: OrderItem): string {
        if (item && item.product && item.product.image_url) {
            return item.product.image_url;
        }

        // Fallback to hardcoded mapping
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

        if (imageMap[item.productName]) {
            return imageMap[item.productName];
        }

        for (const key in imageMap) {
            if (item.productName.toLowerCase().includes(key.toLowerCase())) {
                return imageMap[key];
            }
        }

        return '/favicon.ico';
    }

    getBadgeClass(status: OrderStatus): string {
        switch (status) {
            case 'PENDING':
                return 'status-pending';
            case 'PAID':
                return 'status-paid';
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

    getFormattedStatus(status: OrderStatus): string {
        switch (status) {
            case 'PENDING':
                return 'Payment Pending';
            case 'PAID':
                return 'Paid';
            case 'PROCESSING':
                return 'Processing';
            case 'SHIPPED':
                return 'Shipped';
            case 'DELIVERED':
                return 'Delivered';
            case 'COMPLETED':
                return 'Completed';
            case 'CANCELLED':
                return 'Cancelled';
            default:
                return status;
        }
    }

    trackByOrderId(index: number, order: Order): number {
        return order.id;
    }

    getShadowColor(status: OrderStatus): string {
        switch (status) {
            case 'PENDING':
                return 'rgba(255, 193, 7, 0.3)'; // Yellowish for pending
            case 'PAID':
                return 'rgba(0, 128, 0, 0.3)'; // Greenish for paid
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
