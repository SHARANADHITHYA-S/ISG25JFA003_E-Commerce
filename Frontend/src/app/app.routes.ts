import { Routes } from '@angular/router';
import { OrdersComponent } from './features/orders/orders.component';
import { CartComponent } from './features/carts/component/component';

export const routes: Routes = [
    { path: 'orders', component: OrdersComponent },
    { path: 'cart', component: CartComponent },
    { path: '', redirectTo: '/orders', pathMatch: 'full' }
];
