import { Routes } from '@angular/router';
import { OrdersComponent } from './features/orders/orders.component';

export const routes: Routes = [
    { path: 'orders', component: OrdersComponent },
    { path: '', redirectTo: '/orders', pathMatch: 'full' }
];
