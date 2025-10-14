import { Component } from '@angular/core';
import { CurrentOrderComponent } from './components/current-order.component';
import { OrderHistoryComponent } from './components/order-history/order-history.component';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-orders',
    standalone: true,
    imports: [CommonModule, CurrentOrderComponent, OrderHistoryComponent],
    template: `
        <div class="container py-4">
            <app-current-order></app-current-order>
            <hr class="my-5">
            <app-order-history></app-order-history>
        </div>
    `
})
export class OrdersComponent {}
