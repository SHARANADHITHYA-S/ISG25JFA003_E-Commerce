import { Routes } from '@angular/router';
import { OrdersComponent } from './features/orders/orders.component';
import { CartComponent } from './features/carts/component/component';
import { LoginComponent } from './features/login/login.component';
import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [
    { path: 'orders', component: OrdersComponent, canActivate: [AuthGuard] },
    { path: 'cart', component: CartComponent },
    { path: 'login', component: LoginComponent },
    { path: '', redirectTo: '/orders', pathMatch: 'full' }
];