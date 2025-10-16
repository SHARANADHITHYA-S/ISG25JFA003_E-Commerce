import { Component, OnInit } from '@angular/core';
import { MockOrderService } from '../../../core/services/mock-order.service';
import { Order } from '../../../core/models/order.model';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-previous-orders',
    standalone: true,
    imports: [CommonModule],
    template: `
        <div class="container py-4">
            <div class="row justify-content-center">
                <div class="col-12 col-lg-10 col-xl-9">
                    <h2 class="mb-4 text-center">Previous Orders</h2>
                    <div class="row g-4">
                        <div class="col-12" *ngFor="let order of previousOrders">
                            <div class="card border-0 rounded-3" [ngClass]="'order-status-' + order.status.toLowerCase()">
                                <div class="card-header py-3" [ngClass]="'bg-' + getStatusColor(order.status)">
                                    <div class="d-flex align-items-center justify-content-between">
                                        <h5 class="mb-0">Order #{{ order.id }}</h5>
                                        <span>{{ order.orderDate | date }} - {{ order.status }}</span>
                                    </div>
                                </div>
                                <div class="card-body p-4">
                                    <div class="table-responsive rounded">
                                        <table class="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>Product</th>
                                            <th>Quantity</th>
                                            <th>Price</th>
                                            <th>Subtotal</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr *ngFor="let item of order.items">
                                            <td>{{ item.productName }}</td>
                                            <td>{{ item.quantity }}</td>
                                            <td>{{ item.price | currency }}</td>
                                            <td>{{ item.subtotal | currency }}</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <hr class="my-4">
                            <div class="row g-4">
                                <div class="col-md-6">
                                    <div class="p-3 bg-light rounded-3">
                                        <h6 class="text-muted mb-3">Order Details</h6>
                                        <p class="mb-2"><strong>Order ID:</strong> {{ order.id }}</p>
                                        <p class="mb-2"><strong>Customer ID:</strong> {{ order.customerId }}</p>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="p-3 bg-light rounded-3">
                                        <h6 class="text-muted mb-3">Timestamps</h6>
                                        <p class="mb-2"><strong>Created:</strong> {{ order.createdAt | date:'medium' }}</p>
                                        <p class="mb-2"><strong>Last Updated:</strong> {{ order.updatedAt | date:'medium' }}</p>
                                    </div>
                                </div>
                            </div>
                            <div class="text-end mt-4">
                                <div class="bg-light p-3 rounded-3 d-inline-block">
                                    <h4 class="text-primary mb-0">Total: {{ order.total | currency }}</h4>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `,
    styles: [`
        .hover-shadow {
            transition: box-shadow 0.3s ease-in-out;
        }
        .hover-shadow:hover {
            box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
        }
        .table-hover tbody tr:hover {
            background-color: rgba(0, 0, 0, 0.02);
        }
        h2 {
            color: var(--bs-primary);
            font-weight: 600;
        }
        .card {
            margin-bottom: 1.5rem;
        }
    `]
})
export class PreviousOrdersComponent implements OnInit {
    previousOrders: Order[] = [];
    displayedColumns: string[] = ['productName', 'quantity', 'price', 'subtotal'];

    constructor(private orderService: MockOrderService) {}

    ngOnInit(): void {
        this.loadPreviousOrders();
    }

    getStatusColor(status: string): string {
        switch (status.toLowerCase()) {
            case 'pending':
                return 'warning text-dark';
            case 'processing':
                return 'primary text-white';
            case 'shipped':
                return 'info text-white';
            case 'delivered':
            case 'completed':
                return 'success text-white';
            case 'cancelled':
                return 'danger text-white';
            default:
                return 'secondary text-white';
        }
    }

    loadPreviousOrders(): void {
        this.orderService.getPreviousOrders().subscribe(
            (orders: Order[]) => {
                this.previousOrders = orders;
            },
            (error: Error) => {
                console.error('Error loading previous orders:', error);
            }
        );
    }
}
