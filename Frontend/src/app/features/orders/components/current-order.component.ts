import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { OrderService } from '../../../core/services/order.service';
import { Order, OrderStatus, ORDER_STATUS_HIERARCHY } from '../../../shared/models/order.model';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';
import { ErrorMessageComponent } from '../../../shared/components/error-message/error-message.component';

@Component({
    selector: 'app-current-order',
    standalone: true,
    imports: [CommonModule, LoaderComponent, ErrorMessageComponent],
    template: `
        <app-loader *ngIf="loading" [overlay]="true" message="Loading current order..."></app-loader>
        
        <app-error-message
            *ngIf="error"
            [message]="error"
            (retryClick)="loadCurrentOrder()"
        ></app-error-message>

        <div *ngIf="noCurrentOrderFound && !loading && !error" class="alert alert-info text-center my-4">
            No current order found.
        </div>

        <div class="current-order-card card border-0" 
             *ngIf="currentOrder && !loading && !error"
             [ngClass]="'order-status-' + currentOrder.status.toLowerCase()">
            <div class="card-body p-3">
                <h3 class="order-title">Order #{{ currentOrder.id }} - {{ currentOrder.status }}</h3>
                <p class="order-date">Placed on {{ currentOrder.placed_at | date:'mediumDate' }}</p>

                <div *ngIf="currentOrder.status === 'CANCELLED'" class="cancelled-order-info">
                    <p>This order has been cancelled.</p>
                </div>

                <div class="order-status-tracker mb-4" *ngIf="currentOrder.status !== 'CANCELLED'">
                    <div class="status-step" [ngClass]="getOrderStatusClass('PENDING')">
                        <div class="status-dot"></div>
                        <div class="status-label">Pending</div>
                    </div>
                    <div class="status-line"></div>
                    <div class="status-step" [ngClass]="getOrderStatusClass('PROCESSING')">
                        <div class="status-dot"></div>
                        <div class="status-label">Processing</div>
                    </div>
                    <div class="status-line"></div>
                    <div class="status-step" [ngClass]="getOrderStatusClass('SHIPPED')">
                        <div class="status-dot"></div>
                        <div class="status-label">Shipped</div>
                    </div>
                    <div class="status-line"></div>
                    <div class="status-step" [ngClass]="getOrderStatusClass('DELIVERED')">
                        <div class="status-dot"></div>
                        <div class="status-label">Delivered</div>
                    </div>
                </div>

                <div class="row order-details-section">
                    <div class="col-md-7">
                        <h5 class="section-title">
                            <i class="bi bi-bag-check me-2"></i>Items in this Order ({{ currentOrder.orderItems.length }})
                        </h5>
                        <div class="order-items-container">
                            <div class="order-item-card" *ngFor="let item of currentOrder.orderItems">
                                <div class="item-image">
                                    <img [src]="getProductImage(item.productName)" 
                                         [alt]="item.productName"
                                         onerror="this.src='/favicon.ico'">
                                </div>
                                <div class="item-details">
                                    <h6 class="item-name">{{ item.productName }}</h6>
                                    <div class="item-info">
                                        <span class="item-quantity">
                                            <i class="bi bi-box me-1"></i>Qty: {{ item.quantity }}
                                        </span>
                                        <span class="item-price">
                                            <i class="bi bi-tag me-1"></i>{{ item.price | currency }}
                                        </span>
                                    </div>
                                </div>
                                <div class="item-total">
                                    {{ (item.price * item.quantity) | currency }}
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-5 order-summary">
                        <div class="summary-card">
                            <h5 class="summary-title">
                                <i class="bi bi-receipt me-2"></i>Order Summary
                            </h5>
                            <div class="summary-item">
                                <span>Subtotal:</span>
                                <span class="summary-value">{{ currentOrder.totalAmount | currency }}</span>
                            </div>
                            <div class="summary-item">
                                <span>Shipping:</span>
                                <span class="summary-value success">FREE</span>
                            </div>
                            <div class="summary-item">
                                <span>Tax (9%):</span>
                                <span class="summary-value">{{ currentOrder.totalAmount * 0.09 | currency }}</span>
                            </div>
                            <hr>
                            <div class="summary-total">
                                <span>Total:</span>
                                <span class="total-amount">{{ currentOrder.totalAmount * 1.09 | currency }}</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="order-actions-top">
                    <button class="btn btn-outline-info btn-sm">
                        <i class="bi bi-file-earmark-text me-2"></i>View Invoice
                    </button>
                    <button class="btn btn-outline-info btn-sm">
                        <i class="bi bi-headset me-2"></i>Contact Support
                    </button>
                </div>

                <div class="order-actions-center">
                    <button class="btn btn-success btn-lg" (click)="onMakePayment()">
                        <i class="bi bi-credit-card-2-front me-2"></i>Make Payment
                    </button>
                    <button class="btn btn-danger btn-lg" (click)="onCancelOrder()">
                        <i class="bi bi-x-circle me-2"></i>Cancel Order
                    </button>
                </div>
            </div>
        </div>
    `,
    styles: [`
        .current-order-card {
            background: rgba(255, 255, 255, 0.4);
            backdrop-filter: blur(10px);
            -webkit-backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.3);
            border-radius: 8px;
            margin-bottom: 1.5rem;
            max-width: 900px;
            margin: 0 auto;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        .order-title {
            color: #0B2545;
            font-weight: 600;
            font-size: 1.1rem;
            margin-bottom: 0.15rem;
        }

        .order-date {
            color: #666;
            font-size: 0.8rem;
            margin-bottom: 1rem;
        }

        .order-status-tracker {
            display: flex;
            align-items: flex-start;
            justify-content: space-between;
            margin-bottom: 1.5rem;
            position: relative;
        }

        .status-step {
            display: flex;
            flex-direction: column;
            align-items: center;
            text-align: center;
            width: 70px;
            position: relative;
        }

        .status-dot {
            width: 16px;
            height: 16px;
            border-radius: 50%;
            background-color: #e0e0e0;
            margin-bottom: 0.35rem;
            transition: all 0.3s ease;
        }

        .status-label {
            font-size: 0.7rem;
            color: #6c757d;
            font-weight: 500;
        }

        .status-line {
            flex-grow: 1;
            height: 2px;
            background-color: #e0e0e0;
            margin: 0 -10px;
            top: 7px;
            position: relative;
        }

        .status-step.active .status-dot {
            background-color: #EE7B30;
            transform: scale(1.1);
        }

        .status-step.completed .status-dot {
            background-color: #2ECC71;
        }
        
        .status-step.active .status-label, .status-step.completed .status-label {
            color: #0B2545;
            font-weight: 600;
        }

        .order-details-section {
            border-top: 1px solid #e9ecef;
            padding-top: 1rem;
        }

        .section-title {
            font-size: 1rem;
            font-weight: 600;
            margin-bottom: 0.75rem;
            color: #0B2545;
        }

        .order-items-container {
            display: flex;
            flex-direction: column;
            gap: 0.5rem;
        }

        .order-item-card {
            display: flex;
            align-items: center;
            background: rgba(248, 249, 250, 0.5);
            backdrop-filter: blur(5px);
            -webkit-backdrop-filter: blur(5px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            border-radius: 6px;
            padding: 0.65rem;
            transition: all 0.2s ease;
        }

        .order-item-card:hover {
            background: rgba(233, 236, 239, 0.6);
            transform: translateX(5px);
        }

        .item-image {
            width: 50px;
            height: 50px;
            border-radius: 6px;
            overflow: hidden;
            margin-right: 0.75rem;
            flex-shrink: 0;
            background: white;
        }

        .item-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .item-details {
            flex-grow: 1;
        }

        .item-name {
            font-weight: 600;
            color: #0B2545;
            margin-bottom: 0.25rem;
            font-size: 0.875rem;
        }

        .item-info {
            display: flex;
            gap: 0.75rem;
            font-size: 0.75rem;
            color: #666;
        }

        .item-quantity, .item-price {
            display: flex;
            align-items: center;
        }

        .item-total {
            font-size: 0.95rem;
            font-weight: 700;
            color: #EE7B30;
            margin-left: 0.75rem;
        }

        .summary-card {
            background: rgba(248, 249, 250, 0.5);
            backdrop-filter: blur(8px);
            -webkit-backdrop-filter: blur(8px);
            border: 1px solid rgba(255, 255, 255, 0.25);
            border-radius: 8px;
            padding: 1rem;
            position: sticky;
            top: 20px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
        }

        .summary-title {
            font-size: 1rem;
            font-weight: 600;
            color: #0B2545;
            margin-bottom: 0.75rem;
            padding-bottom: 0.5rem;
            border-bottom: 1px solid #dee2e6;
        }

        .summary-item {
            display: flex;
            justify-content: space-between;
            padding: 0.5rem 0;
            color: #333;
            font-size: 0.85rem;
        }

        .summary-value {
            color: #0B2545;
            font-weight: 600;
        }

        .summary-value.success {
            color: #2ECC71;
            font-weight: 600;
        }

        .summary-total {
            display: flex;
            justify-content: space-between;
            padding: 0.75rem 0 0.25rem;
            font-size: 0.95rem;
            font-weight: 700;
        }

        .total-amount {
            font-size: 1.3rem;
            font-weight: 700;
            color: #EE7B30;
        }

        .current-order-card.order-status-pending {
            border-left: 3px solid #F39C12;
        }

        .current-order-card.order-status-completed {
            border-left: 3px solid #2ECC71;
        }

        .order-actions-top {
            text-align: right;
            margin-bottom: 1rem;
            display: flex;
            justify-content: flex-end;
            gap: 0.5rem;
        }

        .order-actions-top .btn {
            border-radius: 6px;
            padding: 0.4rem 1rem;
            font-weight: 500;
            transition: all 0.2s ease;
            font-size: 0.8rem;
        }

        .order-actions-center {
            text-align: center;
            margin-top: 1rem;
            padding-top: 1rem;
            border-top: 1px solid #e9ecef;
            display: flex;
            justify-content: center;
            gap: 0.75rem;
            flex-wrap: wrap;
        }

        .order-actions-center .btn {
            border-radius: 6px;
            padding: 0.65rem 2rem;
            font-weight: 600;
            font-size: 0.9rem;
            transition: all 0.2s ease;
            border: none;
        }

        .btn-success {
            background: #2ECC71;
            color: white;
        }

        .btn-success:hover {
            background: #27AE60;
        }

        .btn-danger {
            background: #E74C3C;
            color: white;
        }

        .btn-danger:hover {
            background: #C0392B;
        }

        .btn-outline-info {
            color: #0B2545;
            border: 2px solid #0B2545;
            background: rgba(255, 255, 255, 0.9);
        }

        .btn-outline-info:hover {
            background: #0B2545;
            color: white;
            transform: translateY(-2px);
            box-shadow: 0 3px 10px rgba(11, 37, 69, 0.3);
        }

        .cancelled-order-info {
            text-align: center;
            padding: 2rem;
            font-size: 1.2rem;
            font-weight: 500;
            color: #dc3545;
        }
    `],
})
export class CurrentOrderComponent implements OnInit {
    @Output() makePayment = new EventEmitter<{ orderId: number; amount: number }>();
    
    currentOrder: Order | null = null;
    loading = false;
    error: string | null = null;
    noCurrentOrderFound: boolean = false;

    constructor(private orderService: OrderService) {}

    ngOnInit(): void {
        this.loadCurrentOrder();
    }

    loadCurrentOrder(): void {
        this.loading = true;
        this.error = null;
        this.noCurrentOrderFound = false;
        this.orderService.getCurrentOrder().subscribe({
            next: (order) => {
                if (order) {
                    this.currentOrder = order;
                    this.noCurrentOrderFound = false;
                } else {
                    this.currentOrder = null;
                    this.noCurrentOrderFound = true;
                }
                this.loading = false;
            },
            error: (error) => {
                console.error('Error loading current order:', error);
                this.error = 'Failed to load current order. Please try again.';
                this.loading = false;
                this.noCurrentOrderFound = false;
            }
        });
    }

    onMakePayment(): void {
        if (this.currentOrder) {
            this.makePayment.emit({
                orderId: this.currentOrder.id,
                amount: this.currentOrder.totalAmount * 1.09 // Include taxes
            });
        }
    }

    onCancelOrder(): void {
        if (!this.currentOrder) return;
        
        if (confirm(`Are you sure you want to cancel Order #${this.currentOrder.id}?`)) {
            this.orderService.updateOrderStatus(this.currentOrder.id, 'CANCELLED').subscribe({
                next: () => {
                    this.loadCurrentOrder(); // Reload to show next pending order
                },
                error: (error: any) => {
                    console.error('Error cancelling order:', error);
                    this.error = 'Failed to cancel order. Please try again.';
                }
            });
        }
    }

    refreshOrder(): void {
        this.loadCurrentOrder();
    }

    getProductImage(productName: string): string {
        // Map product names to images
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
        
        // Try exact match first
        if (imageMap[productName]) {
            return imageMap[productName];
        }
        
        // Try partial match
        for (const key in imageMap) {
            if (productName.toLowerCase().includes(key.toLowerCase())) {
                return imageMap[key];
            }
        }
        
        // Default image
        return '/favicon.ico';
    }

    getOrderStatusClass(status: OrderStatus): string {
        if (!this.currentOrder) return '';

        if (this.currentOrder.status === 'CANCELLED') {
            return 'cancelled';
        }

        const currentStatusValue = ORDER_STATUS_HIERARCHY[this.currentOrder.status];
        const stepStatusValue = ORDER_STATUS_HIERARCHY[status];

        if (stepStatusValue < currentStatusValue) {
            return 'completed';
        }
        if (stepStatusValue === currentStatusValue) {
            return 'active';
        }
        return '';
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
