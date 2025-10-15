import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
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

  // Lazy-load the Orders module (assuming OrdersComponent is now in a module)
  { 
    path: 'orders', 
    loadChildren: () => import('./features/orders/orders.module').then(m => m.OrdersModule) 
  },

  // Wildcard route for any other URL, redirects to the main product page
  { path: '**', redirectTo: 'products' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
