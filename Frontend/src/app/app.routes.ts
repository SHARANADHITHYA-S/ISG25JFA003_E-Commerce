import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
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

  // Lazy-load the Orders module (assuming OrdersComponent is now in a module)
  {
    path: 'orders',
    loadComponent: () => import('./features/orders/orders.component').then(m => m.OrdersComponent)
  },

  // Wildcard route for any other URL, redirects to the main product page
  { path: '**', redirectTo: 'products' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
