import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { OrderService } from '../../../core/services/order.service';
import { Order, OrderItem } from '../../../shared/models/order.model';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';
import { ErrorMessageComponent } from '../../../shared/components/error-message/error-message.component';

@Component({
    selector: 'app-current-order',
    standalone: true,
    imports: [CommonModule, LoaderComponent, ErrorMessageComponent],
    templateUrl: './current-order.component.html',
    styleUrl: './current-order.component.scss'
})
export class CurrentOrderComponent implements OnInit {
    currentOrder: Order | null = null;
    loading = false;
    error: string | null = null;

    @Output() makePayment = new EventEmitter<{ orderId: number; amount: number }>();

    constructor(private orderService: OrderService) { }

    ngOnInit(): void {
        this.loadCurrentOrder();
    }

    loadCurrentOrder(): void {
        this.loading = true;
        this.error = null;
        this.orderService.getCurrentOrder().subscribe({
            next: (order: Order | null) => {
                this.currentOrder = order;
                this.loading = false;
            },
            error: (err: any) => {
                console.error('Error loading current order:', err);
                this.error = 'Failed to load current order. Please try again.';
                this.loading = false;
            }
        });
    }

    refreshOrder(): void {
        this.loadCurrentOrder();
    }

    onMakePayment(): void {
        if (this.currentOrder && this.currentOrder.id && this.currentOrder.totalAmount) {
            this.makePayment.emit({ orderId: this.currentOrder.id, amount: this.currentOrder.totalAmount });
        }
    }

    onCancelOrder(): void {
        if (this.currentOrder && this.currentOrder.id) {
            // TODO: Implement cancel order logic
            console.log('Cancel order:', this.currentOrder.id);
            // You can emit an event or call a service method here
        }
    }

    getStatusClass(status: string): string {
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

    getEstimatedShippingDate(): Date {
        // Mock: 2 days from order placement
        if (this.currentOrder?.placed_at) {
            const date = new Date(this.currentOrder.placed_at);
            date.setDate(date.getDate() + 2);
            return date;
        }
        return new Date();
    }

    getArrivalDate(): Date {
        // Mock: 7 days from order placement
        if (this.currentOrder?.placed_at) {
            const date = new Date(this.currentOrder.placed_at);
            date.setDate(date.getDate() + 7);
            return date;
        }
        return new Date();
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
}