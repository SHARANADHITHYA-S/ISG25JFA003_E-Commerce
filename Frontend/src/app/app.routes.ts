import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Routes } from '@angular/router';
import { OrdersComponent } from './features/orders/orders.component';
import { CartComponent } from './features/carts/component/component';
import { LoginComponent } from './features/login/login.component';
import { AuthGuard } from './core/guards/auth.guard
// Note: Removed direct import of OrdersComponent to enforce lazy loading conventions

export const routes: Routes = [
  // Default route redirects to the public product catalog
  { path: '', redirectTo: 'products', pathMatch: 'full' },

  // Lazy-load the Product Catalog feature module
  {
    path: 'products',
    children: [
      { path: '', loadComponent: () => import('./features/product-catalog/product-list-page/product-list-page.component').then(m => m.ProductListPageComponent) },
      { path: ':id', loadComponent: () => import('./features/product-catalog/product-detail-page/product-detail-page.component').then(m => m.ProductDetailPageComponent) }
    ]
  },
  
  // Lazy-load the Admin Management feature module
  {
    path: 'admin',
    children: [
      { path: '', redirectTo: 'categories', pathMatch: 'full' },
      { path: 'categories', loadComponent: () => import('./features/admin-management/category-crud/category-crud.component').then(m => m.CategoryCrudComponent) },
      { path: 'categories/new', loadComponent: () => import('./features/admin-management/category-form/category-form.component').then(m => m.CategoryFormComponent) },
      { path: 'categories/edit/:id', loadComponent: () => import('./features/admin-management/category-form/category-form.component').then(m => m.CategoryFormComponent) },
      { path: 'products', loadComponent: () => import('./features/admin-management/product-crud/product-crud.component').then(m => m.ProductCrudComponent) },
      { path: 'products/new', loadComponent: () => import('./features/admin-management/product-form/product-form.component').then(m => m.ProductFormComponent) },
      { path: 'products/edit/:id', loadComponent: () => import('./features/admin-management/product-form/product-form.component').then(m => m.ProductFormComponent) }
    ]
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

