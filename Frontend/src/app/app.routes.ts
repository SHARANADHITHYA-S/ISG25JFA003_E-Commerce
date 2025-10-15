import { Routes } from '@angular/router';
import { OrdersComponent } from './features/orders/orders.component';
import { CartComponent } from './features/carts/component/component';

export const routes: Routes = [
    { path: 'orders', component: OrdersComponent },
    { path: 'cart', component: CartComponent },
import { LoginComponent } from './features/login/login.component';
import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'orders', component: OrdersComponent, canActivate: [AuthGuard] },
    { path: '', redirectTo: '/orders', pathMatch: 'full' }
];
