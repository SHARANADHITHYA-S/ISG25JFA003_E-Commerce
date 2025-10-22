import { Component, ViewChild } from '@angular/core';
import { CurrentOrderComponent } from './components/current-order.component';
import { OrderHistoryComponent } from './components/order-history/order-history.component';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { PaymentService } from '../../core/services/payment.service';
import { OrderService } from '../../core/services/order.service';
import { Payment, PaymentStatus, PaymentType } from '../../core/models/payment.model';
import { AuthService } from '../../core/services/auth.service';
import { RazorpayService, RazorpayPaymentResponse } from '../../core/services/razorpay.service';

@Component({
    selector: 'app-orders',
    standalone: true,
    imports: [
        CommonModule,
        CurrentOrderComponent,
        OrderHistoryComponent,
        MatButtonModule,
        MatSnackBarModule
    ],
    template: `
        <div class="container py-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h3>
                    <i class="bi bi-box-seam me-2"></i>My Orders
                </h3>
            </div>
            <app-current-order 
                (makePayment)="openPaymentDialog($event)"
                #currentOrderComponent>
            </app-current-order>
            <hr class="my-5">
            <app-order-history></app-order-history>
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
    
    private userId: number;

    constructor(
        private razorpayService: RazorpayService,
        private paymentService: PaymentService,
        private orderService: OrderService,
        private authService: AuthService,
        private snackBar: MatSnackBar
    ) {
        const user = this.authService.getCurrentUser();
        this.userId = user?.id || 0;
    }

    openPaymentDialog(event: { orderId: number; amount: number }): void {
        console.log('Opening Razorpay payment for order:', event);
        
        // Get user details
        const user = this.authService.getCurrentUser();
        const userName = user ? `${user.firstName} ${user.lastName}` : 'Guest User';
        const userEmail = user?.username ? `${user.username}@example.com` : '';

        // Open Real Razorpay Checkout
        this.razorpayService.openCheckout({
            amount: this.razorpayService.convertToPaise(event.amount),
            name: 'E-Commerce Store',
            description: `Payment for Order #${event.orderId}`,
            image: '/full_logo.png',
            prefill: {
                name: userName,
                email: userEmail,
                contact: user?.username || ''
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
                
                // Update payment status to SUCCESS
                setTimeout(() => {
                    const paymentId = response.id || response.paymentId;
                    if (paymentId) {
                        this.updatePaymentToSuccess(paymentId, paymentData.orderId);
                    } else {
                        this.showErrorMessage('Payment ID not found. Please contact support.');
                    }
                }, 1000);
            },
            error: (error) => {
                console.error('Payment error:', error);
                this.showErrorMessage('An error occurred while processing payment. Please try again.');
            }
        });
    }

    private updatePaymentToSuccess(paymentId: number, orderId: number): void {
        // Update payment status to SUCCESS
        this.paymentService.updatePaymentStatus(paymentId, 'SUCCESS').subscribe({
            next: (updatedPayment) => {
                console.log('Payment status updated to SUCCESS:', updatedPayment);
                
                // Update order to PROCESSING
                this.updateOrderStatus(orderId, 'PROCESSING');
                
                // After 2 seconds, update to SHIPPED
                setTimeout(() => {
                    this.updateOrderStatus(orderId, 'SHIPPED');
                    this.showSuccessMessage();
                    
                    // Refresh the current order view to show next pending order
                    if (this.currentOrderComponent) {
                        setTimeout(() => {
                            this.currentOrderComponent.refreshOrder();
                        }, 1000);
                    }
                }, 2000);
            },
            error: (error) => {
                console.error('Error updating payment status:', error);
                this.showErrorMessage('Payment processing failed. Please try again.');
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

    private showSuccessMessage(): void {
        this.snackBar.open('✅ Payment processed successfully! Your order is being prepared.', 'Close', {
            duration: 5000,
            panelClass: ['success-snackbar'],
            horizontalPosition: 'center',
            verticalPosition: 'top'
        });
    }

    private showInfoMessage(message: string): void {
        this.snackBar.open(message, 'Close', {
            duration: 3000,
            panelClass: ['info-snackbar'],
            horizontalPosition: 'center',
            verticalPosition: 'top'
        });
    }

    private showErrorMessage(message: string): void {
        this.snackBar.open(message, 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar'],
            horizontalPosition: 'center',
            verticalPosition: 'top'
        });
    }
}
