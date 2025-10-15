import { Component, inject } from '@angular/core';
import { CurrentOrderComponent } from './components/current-order.component';
import { OrderHistoryComponent } from './components/order-history/order-history.component';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { CartResponse } from '../../core/models/cart';

@Component({
    selector: 'app-orders',
    standalone: true,
    imports: [CommonModule, RouterLink, CurrentOrderComponent, OrderHistoryComponent],
    template: `
        <div class="container py-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2>Orders</h2>
                <a [routerLink]="['/cart']" class="btn btn-primary">
                    <i class="bi bi-cart"></i> Cart 
                    <span class="badge bg-light text-dark ms-2">{{cartItemCount}}</span>
                </a>
            </div>
            <app-current-order></app-current-order>
            <hr class="my-5">
            <app-order-history></app-order-history>
        </div>
    `
})
export class OrdersComponent {
    cartItemCount = 0;
    private cartService = inject(CartService);

    constructor() {
        this.loadCartItemCount();
    }

    private loadCartItemCount(): void {
        // Assuming userId 2 as per cart component
        this.cartService.getCart(2).subscribe({
            next: (cart: CartResponse | null) => {
                this.cartItemCount = cart?.items?.reduce((sum: number, item: any) => sum + item.quantity, 0) || 0;
            }
        });
    }
}
