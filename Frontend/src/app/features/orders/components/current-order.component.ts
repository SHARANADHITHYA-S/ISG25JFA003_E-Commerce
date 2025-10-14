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

        <div class="card shadow-sm border-0 rounded-3" *ngIf="currentOrder && !loading && !error">
            <div class="card-header bg-primary text-white py-3">
                <h5 class="card-title mb-1">Current Order</h5>
                <h6 class="card-subtitle mb-0 text-white-50">Order #{{ currentOrder.id }}</h6>
            </div>
            <div class="card-body p-4">
                <div class="order-status-timeline mb-4">
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
                    <div class="status-step" [ngClass]="getOrderStatusClass('COMPLETED')">
                        <div class="status-dot"></div>
                        <div class="status-label">Completed</div>
                    </div>
                </div>

                <div class="table-responsive rounded">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Product</th>
                                <th>Quantity</th>
                                <th>Price</th>
                                <th>Subtotal</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr *ngFor="let item of currentOrder.orderItems">
                                <td>{{ item.productName }}</td>
                                <td>{{ item.quantity }}</td>
                                <td>{{ item.price | currency }}</td>
                                <td>{{ (item.price * item.quantity) | currency }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <div class="text-end mt-3">
                    <h4 class="text-primary">Total: {{ currentOrder.totalAmount | currency }}</h4>
                </div>
            </div>
        </div>
    `,
    styles: [`
        .order-status-timeline {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 2rem;
        }

        .status-step {
            display: flex;
            flex-direction: column;
            align-items: center;
            position: relative;
        }

        .status-dot {
            width: 20px;
            height: 20px;
            border-radius: 50%;
            background-color: #e0e0e0;
            border: 3px solid #fff;
            transition: background-color 0.3s ease;
        }

        .status-label {
            margin-top: 0.5rem;
            font-size: 0.9rem;
            color: #6c757d;
        }

        .status-line {
            flex-grow: 1;
            height: 4px;
            background-color: #e0e0e0;
            margin: 0 -10px;
        }

        .status-step.active .status-dot {
            background-color: #0d6efd;
        }

        .status-step.completed .status-dot {
            background-color: #198754;
        }

        .status-step.active .status-label, .status-step.completed .status-label {
            font-weight: 600;
            color: #000;
        }

        .status-step.processing .status-dot {
            background-color: #ffc107; /* yellow for processing */
        }
    `]
})
export class CurrentOrderComponent implements OnInit {
    currentOrder: Order | null = null;
    loading = false;
    error: string | null = null;

    constructor(private orderService: OrderService) {}

    ngOnInit(): void {
        this.loadCurrentOrder();
    }

    loadCurrentOrder(): void {
        this.loading = true;
        this.error = null;
        this.orderService.getCurrentOrder().subscribe({
            next: (order) => {
                if (order && order.status === 'COMPLETED') {
                    this.currentOrder = null;
                } else {
                    this.currentOrder = order;
                }
                this.loading = false;
            },
            error: (error) => {
                console.error('Error loading current order:', error);
                this.error = 'Failed to load current order. Please try again.';
                this.loading = false;
            }
        });
    }

    getOrderStatusClass(status: OrderStatus): string {
        if (!this.currentOrder) return '';

        const currentStatusValue = ORDER_STATUS_HIERARCHY[this.currentOrder.status];
        const stepStatusValue = ORDER_STATUS_HIERARCHY[status];

        if (stepStatusValue < currentStatusValue) {
            return 'completed';
        }
        if (stepStatusValue === currentStatusValue) {
            if (status === 'PROCESSING') {
                return 'processing';
            }
            return 'active';
        }
        return '';
    }
}
