import { Component, ViewChild } from '@angular/core';
import { CurrentOrderComponent } from './components/current-order.component';
import { OrderHistoryComponent } from './components/order-history/order-history.component';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { PaymentService } from '../../core/services/payment.service';
import { OrderService } from '../../core/services/order.service';
import { Payment, PaymentStatus, PaymentType } from '../../core/models/payment.model';
import { AuthService } from '../../core/services/auth.service';
import { RazorpayService, RazorpayPaymentResponse } from '../../core/services/razorpay.service';
import { Order } from '../../shared/models/order.model';

@Component({
    selector: 'app-orders',
    standalone: true,
    imports: [
        CommonModule,
        CurrentOrderComponent,
        OrderHistoryComponent,
        MatButtonModule,
        ToastModule
    ],
    template: `
        <div class="container py-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h3>
                    <i class="bi bi-box-seam me-2"></i>My Orders
                </h3>
            </div>
            <p-toast></p-toast>
            <app-current-order
                (makePayment)="openPaymentDialog($event)"
                (orderCancelled)="onOrderCancelled()"
                #currentOrderComponent>
            </app-current-order>
            <hr class="my-5">
            <app-order-history #orderHistoryComponent (completePayment)="openPaymentDialog({orderId: $event.id, amount: $event.totalAmount})" (cancelOrder)="onCancelOrder($event)"></app-order-history>
        </div>
    `,
    styles: [`
        hr {
            border: none;
            height: 2px;
            background: linear-gradient(90deg, transparent, #EE7B30, transparent);
        }
    `]
})
export class OrdersComponent {
    @ViewChild('currentOrderComponent') currentOrderComponent!: CurrentOrderComponent;
    @ViewChild(OrderHistoryComponent) orderHistoryComponent!: OrderHistoryComponent;

    private userId: number;

    constructor(
        private razorpayService: RazorpayService,
        private paymentService: PaymentService,
        private orderService: OrderService,
        private authService: AuthService,
        private messageService: MessageService
    ) {
        const user = this.authService.getCurrentUser();
        this.userId = user?.id || 0;
    }

    openPaymentDialog(event: { orderId: number; amount: number }): void {
        console.log('Opening Razorpay payment for order:', event);

        // Get user details
        const user = this.authService.getCurrentUser();
        const userName = user ? user.name : 'Guest User';
        const userEmail = user?.email ? user.email : '';

        // Open Real Razorpay Checkout
        this.razorpayService.openCheckout({
            amount: this.razorpayService.convertToPaise(event.amount),
            name: 'E-Commerce Store',
            description: `Payment for Order #${event.orderId}`,
            image: '/full_logo.png',
            prefill: {
                name: userName,
                email: userEmail,
                contact: user?.name || '' // Assuming contact can be name or phone number
            },
            theme: {
                color: '#667eea'
            }
        }).subscribe({
            next: (response: RazorpayPaymentResponse) => {
                console.log('✅ Razorpay Payment successful:', response);
                this.processPayment({
                    orderId: event.orderId,
                    razorpayPaymentId: response.razorpay_payment_id,
                    razorpayOrderId: response.razorpay_order_id,
                    razorpaySignature: response.razorpay_signature,
                    paymentMethod: 'Razorpay'
                });
            },
            error: (error: any) => {
                console.log('Payment cancelled or failed:', error);
                if (error.error === 'Payment cancelled by user') {
                    // User cancelled - no error message needed
                    console.log('💡 User cancelled payment');
                } else {
                    // Actual error
                    this.showErrorMessage(error.description || error.error || 'Payment failed. Please try again.');
                }
            }
        });
    }

    private processPayment(paymentData: any): void {
        console.log('Processing payment with data:', paymentData);

        this.showInfoMessage('⏳ Processing your payment...');

        // First, create the payment record
        const request = {
            userId: this.userId,
            orderId: paymentData.orderId
        };

        this.paymentService.createPayment(request).subscribe({
            next: (response: Payment) => {
                console.log('Payment record created:', response);

                // Add verification step before marking as SUCCESS
                const paymentId = response.id || response.paymentId;
                if (paymentId) {
                    this.verifyPayment(paymentId, paymentData);
                } else {
                    this.showErrorMessage('Payment ID not found. Please contact support.');
                }
            },
            error: (error) => {
                console.error('Payment error:', error);
                this.showErrorMessage('An error occurred while processing payment. Please try again.');
            }
        });
    }

    private verifyPayment(paymentId: number, paymentData: any): void {
        console.log('Verifying payment with Razorpay test mode...');

        // Use test mode verification
        this.paymentService.verifyPaymentTestMode(paymentId, paymentData).subscribe({
            next: (verifiedPayment) => {
                console.log('Payment verification successful:', verifiedPayment);

                // Update order status to PAID
                this.updateOrderStatus(paymentData.orderId, 'PAID');
                this.showSuccessMessage();

                // Refresh both the current order and order history views
                if (this.currentOrderComponent) {
                    this.currentOrderComponent.refreshOrder();
                }
                if (this.orderHistoryComponent) {
                    this.orderHistoryComponent.refreshOrders();
                }
            },
            error: (error) => {
                console.error('Payment verification failed:', error);
                this.showErrorMessage('Payment verification failed. Your payment may not have been processed. Please contact support.');

                // Optionally, you could mark the payment as FAILED here
                // this.paymentService.updatePaymentStatus(paymentId, 'FAILED').subscribe(...);
            }
        });
    }


    private updateOrderStatus(orderId: number, status: string): void {
        this.orderService.updateOrderStatus(orderId, status).subscribe({
            next: (order) => {
                console.log('Order status updated to:', status);
            },
            error: (error) => {
                console.error('Error updating order status:', error);
            }
        });
    }

    onCancelOrder(order: Order): void {
        if (confirm('Are you sure you want to cancel this order?')) {
            const isPaid = order.status !== 'PENDING';
            this.orderService.cancelOrder(order.id, isPaid).subscribe(() => {
                if (this.currentOrderComponent) {
                    this.currentOrderComponent.refreshOrder();
                }
                if (this.orderHistoryComponent) {
                    this.orderHistoryComponent.refreshOrders();
                }
            });
        }
    }

    onOrderCancelled(): void {
        console.log('Order cancelled, refreshing order history...');
        // Refresh order history to show the cancelled order
        if (this.orderHistoryComponent) {
            this.orderHistoryComponent.refreshOrders();
        }
    }

    private showSuccessMessage(): void {
        this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: '✅ Payment processed successfully! Your order is being prepared.',
            life: 5000
        });
    }

    private showInfoMessage(message: string): void {
        this.messageService.add({
            severity: 'info',
            summary: 'Info',
            detail: message,
            life: 3000
        });
    }

    private showErrorMessage(message: string): void {
        this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: message,
            life: 5000
        });
    }
}
