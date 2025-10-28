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
    selectedDeliveryDate: any = null;
    deliveryOptions: any[] = [];
    loading = false;
    error: string | null = null;
    timelineEvents: TimelineEvent[] = [];

    @Output() makePayment = new EventEmitter<{ orderId: number; amount: number; deliveryDate: string }>();
    @Output() orderCancelled = new EventEmitter<void>();

    constructor(private orderService: OrderService, private addressService: AddressService, private paymentMethodService: PaymentMethodService, private router: Router) { }

    ngOnInit(): void {
        this.loadCurrentOrder();
        this.generateDeliveryOptions();
    }

    generateDeliveryOptions(): void {
        const today = new Date();
        
        // Next Day Delivery (Paid)
        const nextDay = new Date(today);
        nextDay.setDate(today.getDate() + 1);
        const nextDayString = nextDay.toLocaleDateString('en-US', {
            weekday: 'long',
            month: 'short',
            day: 'numeric'
        });

        // Nearest Sunday Delivery (Free)
        const nearestSunday = new Date(today);
        const dayOfWeek = today.getDay(); // 0=Sun, 1=Mon, ...
        const daysUntilSunday = (7 - dayOfWeek) % 7;
        nearestSunday.setDate(today.getDate() + daysUntilSunday);
        // If today is Sunday, calculate for next Sunday
        if (daysUntilSunday === 0) {
            nearestSunday.setDate(today.getDate() + 7);
        }
        const nearestSundayString = nearestSunday.toLocaleDateString('en-US', {
            weekday: 'long',
            month: 'short',
            day: 'numeric'
        });

        this.deliveryOptions = [
            { date: nearestSundayString, isFree: true },
            { date: nextDayString, isFree: false }
        ];
        // Default to the free option
        this.selectedDeliveryDate = this.deliveryOptions.find(option => option.isFree);
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
        console.log('Building timeline for current order:', this.currentOrder);
        if (!this.currentOrder) return;

        if (this.currentOrder.status === 'CANCELLED') {
            this.timelineEvents = [{
                status: 'CANCELLED',
                date: this.currentOrder.placed_at.toString(),
                isCurrent: true,
                isCompleted: true
            }];
            return;
        }

        const statuses: OrderStatus[] = ['PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED'];
        const statusOrder = statuses.indexOf(this.currentOrder.status);
        const placedAt = new Date(this.currentOrder.placed_at);

        this.timelineEvents = statuses.map((status, index) => {
            let date: Date;
            if (this.currentOrder?.deliveryDate) {
                // Calculate dates backwards from delivery date
                const deliveryDate = new Date(this.currentOrder.deliveryDate);
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
                isCurrent: this.currentOrder!.status === status,
                isCompleted: index < statusOrder
            };
            return event;
        });
    }

    refreshOrder(): void {
        this.loadCurrentOrder();
    }

    onMakePayment(): void {
        if (this.currentOrder && this.currentOrder.id && this.currentOrder.totalAmount && this.selectedDeliveryDate) {
            // Calculate total with tax (9%) and delivery charges
            const subtotal = this.currentOrder.totalAmount;
            const tax = subtotal * 0.09;
            const deliveryCharge = this.selectedDeliveryDate.isFree ? 0 : 5.99;
            const totalAmount = subtotal + tax + deliveryCharge;
            
            this.makePayment.emit({ 
                orderId: this.currentOrder.id, 
                amount: totalAmount, // Send total amount including tax and delivery
                deliveryDate: this.selectedDeliveryDate.date 
            });
        }
    }

    onCancelOrder(): void {
        if (this.currentOrder && this.currentOrder.id) {
            if (confirm('Are you sure you want to cancel this order? The items will be returned to your cart.')) {
                this.loading = true;
                const isPaid = this.currentOrder.status !== 'PENDING';
                this.orderService.cancelOrder(this.currentOrder.id, isPaid).subscribe({
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
            return this.selectedDeliveryDate.date;
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
