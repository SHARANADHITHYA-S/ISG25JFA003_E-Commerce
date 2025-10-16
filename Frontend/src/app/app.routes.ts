import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Routes } from '@angular/router';
import { OrdersComponent } from './features/orders/orders.component';
import { CartComponent } from './features/carts/component/component';
import { LoginComponent } from './features/login/login.component';
import { AuthGuard } from './core/guards/auth.guard
// Note: Removed direct import of OrdersComponent to enforce lazy loading conventions

const routes: Routes = [
  // Default route redirects to the public product catalog
  { path: '', redirectTo: 'products', pathMatch: 'full' },

  // Lazy-load the Product Catalog feature module
  { 
    path: 'products', 
    // This path is correct *if* the file exists at src/app/features/product-catalog/catalog.module.ts
    loadChildren: () => import('./features/product-catalog/catalog.module').then(m => m.CatalogModule) 
  },
  
  // Lazy-load the Admin Management feature module
  { 
    path: 'admin', 
    // This path is correct *if* the file exists at src/app/features/admin-management/admin.module.ts
    loadChildren: () => import('./features/admin-management/admin.module').then(m => m.AdminModule) 
  },
   { path: 'orders', component: OrdersComponent, canActivate: [AuthGuard] },
    { path: 'cart', component: CartComponent },
    { path: 'login', component: LoginComponent },
    { path: '', redirectTo: '/orders', pathMatch: 'full' }



  // Wildcard route for any other URL, redirects to the main product page
  { path: '**', redirectTo: 'products' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

