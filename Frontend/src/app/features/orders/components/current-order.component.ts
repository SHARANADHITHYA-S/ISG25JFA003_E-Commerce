import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { OrderService } from '../../../core/services/order.service';
import { AddressService, Address } from '../../../core/services/address.service';
import { PaymentMethodService, PaymentMethod } from '../../../core/services/payment-method.service';
import { Order, OrderItem, OrderStatus } from '../../../shared/models/order.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';
import { ErrorMessageComponent } from '../../../shared/components/error-message/error-message.component';
import { ShippingTimelineComponent, TimelineEvent } from '../../../shared/components/shipping-timeline/shipping-timeline.component';

@Component({
    selector: 'app-current-order',
    standalone: true,
    imports: [CommonModule, FormsModule, LoaderComponent, ErrorMessageComponent, ShippingTimelineComponent],
    templateUrl: './current-order.component.html',
    styleUrl: './current-order.component.scss'
})
export class CurrentOrderComponent implements OnInit {
    currentOrder: Order | null = null;
    selectedAddress: Address | null = null;
    selectedPaymentMethod: PaymentMethod | null = null;
    selectedDeliveryDate: string = '';
    deliveryOptions: string[] = [];
    loading = false;
    error: string | null = null;
    timelineEvents: TimelineEvent[] = [];

    @Output() makePayment = new EventEmitter<{ orderId: number; amount: number }>();
    @Output() orderCancelled = new EventEmitter<void>();

    constructor(private orderService: OrderService, private addressService: AddressService, private paymentMethodService: PaymentMethodService, private router: Router) { }

    ngOnInit(): void {
        this.loadCurrentOrder();
        this.generateDeliveryOptions();
    }

    generateDeliveryOptions(): void {
        const today = new Date();
        this.deliveryOptions = [];
        for (let i = 1; i <= 2; i++) {
            const date = new Date(today);
            date.setDate(today.getDate() + i);
            this.deliveryOptions.push(date.toLocaleDateString('en-US', {
                weekday: 'long',
                month: 'short',
                day: 'numeric'
            }));
        }
        this.selectedDeliveryDate = this.deliveryOptions[0];
    }

    loadCurrentOrder(): void {
        this.loading = true;
        this.error = null;
        this.orderService.getCurrentOrder().subscribe({
            next: (order: Order | null) => {
                this.currentOrder = order;
                if (this.currentOrder) {
                    this.loadAddressAndPaymentMethod();
                    if (this.currentOrder.status !== 'PENDING') {
                        this.buildTimeline();
                    }
                }
                this.loading = false;
            },
            error: (err: any) => {
                console.error('Error loading current order:', err);
                this.error = 'Failed to load current order. Please try again.';
                this.loading = false;
            }
        });
    }

    loadAddressAndPaymentMethod(): void {
        if (!this.currentOrder) return;

        // Load address
        this.addressService.getAddress(this.currentOrder.addressId).subscribe({
            next: (address: Address) => {
                this.selectedAddress = address;
            },
            error: (err: any) => {
                console.error('Error loading address:', err);
            }
        });

        // Load payment method
        this.paymentMethodService.getPaymentMethod(this.currentOrder.paymentMethodId).subscribe({
            next: (paymentMethod: PaymentMethod) => {
                this.selectedPaymentMethod = paymentMethod;
            },
            error: (err: any) => {
                console.error('Error loading payment method:', err);
            }
        });
    }

    buildTimeline(): void {
        if (!this.currentOrder) return;

        const statuses: OrderStatus[] = ['PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED'];
        const statusOrder = statuses.indexOf(this.currentOrder.status);
        this.timelineEvents = statuses.map((status, index) => {
            const event: TimelineEvent = {
                status: status,
                date: this.getEstimatedDate(index),
                isCurrent: this.currentOrder!.status === status,
                isCompleted: index < statusOrder
            };
            return event;
        });
    }

    getEstimatedDate(step: number): string {
        if (!this.currentOrder) return '';
        const date = new Date(this.currentOrder.placed_at);
        date.setDate(date.getDate() + (step * 2)); // Add 2 days for each step
        return date.toLocaleDateString();
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
            if (confirm('Are you sure you want to cancel this order? The items will be returned to your cart.')) {
                this.loading = true;
                this.orderService.cancelOrder(this.currentOrder.id).subscribe({
                    next: (cancelledOrder) => {
                        console.log('Order cancelled successfully:', cancelledOrder);
                        this.loading = false;
                        // Emit event to parent to refresh order history
                        this.orderCancelled.emit();
                        // Refresh the current order view to show the next order or empty state
                        this.loadCurrentOrder();
                    },
                    error: (err) => {
                        console.error('Error cancelling order:', err);
                        this.error = 'Failed to cancel order. Please try again.';
                        this.loading = false;
                    }
                });
            }
        }
    }

    getStatusClass(status: string): string {
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

    navigateToProduct(productId: number): void {
        this.router.navigate(['/products', productId]);
    }

    onDeliveryDateChange(): void {
        // Handle delivery date change if needed
        console.log('Selected delivery date:', this.selectedDeliveryDate);
    }

    getEstimatedDeliveryDate(): string {
        if (!this.currentOrder) return '';
        // Use the selected delivery date if it exists, otherwise calculate based on order placement
        if (this.selectedDeliveryDate) {
            return this.selectedDeliveryDate;
        }
        // Default to 2 days from order placement for paid orders
        const date = new Date(this.currentOrder.placed_at);
        date.setDate(date.getDate() + 2);
        return date.toLocaleDateString('en-US', {
            weekday: 'long',
            month: 'short',
            day: 'numeric'
        });
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

        return './favicon.ico';
    }
}
