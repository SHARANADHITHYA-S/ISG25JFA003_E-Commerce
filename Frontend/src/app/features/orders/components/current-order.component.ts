import { Component, OnInit } from '@angular/core';
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

        <div class="current-order-card card border-0 rounded-3" 
             *ngIf="currentOrder && !loading && !error"
             [ngClass]="'order-status-' + currentOrder.status.toLowerCase()">
            <div class="card-body p-4">
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
                    <div class="col-md-6">
                        <h5 class="section-title">Items in this Order ({{ currentOrder.orderItems.length }})</h5>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item" *ngFor="let item of currentOrder.orderItems">
                                Product: {{ item.productName }}<br>
                                Quantity: {{ item.quantity }}<br>
                                Total: {{ (item.price * item.quantity) | currency }}
                            </li>
                        </ul>
                    </div>
                    <div class="col-md-6 order-summary">
                        <p>Shipping: <span class="summary-value">FREE</span></p>
                        <p>Taxes: <span class="summary-value">{{ currentOrder.totalAmount * 0.09 | currency }}</span></p>
                        <h5 class="total-amount">Total: {{ currentOrder.totalAmount * 1.09 | currency }}</h5>
                    </div>
                </div>

                <div class="order-actions">
                    <button class="btn btn-outline-secondary">View Invoice</button>
                    <button class="btn btn-outline-secondary">Contact Support</button>
                </div>
            </div>
        </div>
    `,
    styles: [`
        .current-order-card {
            background: rgba(255, 255, 255, 0.6);
            backdrop-filter: blur(12px) saturate(180%);
            -webkit-backdrop-filter: blur(12px) saturate(180%);
            border-radius: 1rem;
            border: 1px solid rgba(255, 255, 255, 0.2);
            margin-bottom: 2rem;
            max-width: 700px;
            margin: 0 auto;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        .order-title {
            color: #0B2545;
            font-weight: 700;
            font-size: 1.5rem;
            margin-bottom: 0.25rem;
        }

        .order-date {
            color: #555;
            font-size: 0.9rem;
            margin-bottom: 2rem;
        }

        .order-status-tracker {
            display: flex;
            align-items: flex-start;
            justify-content: space-between;
            margin-bottom: 2.5rem;
            position: relative;
        }

        .status-step {
            display: flex;
            flex-direction: column;
            align-items: center;
            text-align: center;
            width: 80px;
            position: relative;
        }

        .status-dot {
            width: 20px;
            height: 20px;
            border-radius: 50%;
            background-color: #e0e0e0;
            margin-bottom: 0.5rem;
            transition: all 0.4s ease;
        }

        .status-label {
            font-size: 0.8rem;
            color: #6c757d;
            font-weight: 500;
        }

        .status-line {
            flex-grow: 1;
            height: 2px;
            background-color: #e0e0e0;
            margin: 0 -10px;
            top: 9px;
            position: relative;
        }

        .status-step.active .status-dot {
            background-color: #EE7B30;
            transform: scale(1.2);
        }

        .status-step.completed .status-dot {
            background-color: #2ECC71;
        }
        
        .status-step.active .status-label, .status-step.completed .status-label {
            color: #0B2545;
            font-weight: 600;
        }

        .order-details-section {
            border-top: 1px solid rgba(255, 255, 255, 0.4);
            padding-top: 2rem;
        }

        .section-title {
            font-size: 1.2rem;
            font-weight: 600;
            margin-bottom: 1rem;
            color: #0B2545;
        }

        .list-group-item {
            background: transparent;
            border: none;
            padding: 0.75rem 0;
            color: #333;
        }

        .order-summary {
            text-align: right;
        }

        .order-summary p {
            color: #333;
        }

        .order-summary .summary-value {
            color: #0B2545;
            font-weight: 600;
        }

        .total-amount {
            font-size: 1.5rem;
            font-weight: 700;
            color: #0B2545;
            margin-top: 1rem;
        }

        .current-order-card.order-status-pending {
            box-shadow: 0 0 45px rgba(255, 165, 0, 0.1) !important; 
            background: rgba(255, 255, 255, 0.6) !important;
        }

        .current-order-card.order-status-completed {
            background: rgba(120, 251, 255, 0.23) !important;
        }

        .order-actions {
            text-align: center;
            margin-top: 2rem;
            padding-top: 1.5rem;
            border-top: 1px solid rgba(255, 255, 255, 0.4);
        }

        .order-actions .btn {
            border-radius: 50px;
            padding: 0.75rem 2rem;
            font-weight: 600;
            margin: 0 0.5rem;
            transition: all 0.3s ease;
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
