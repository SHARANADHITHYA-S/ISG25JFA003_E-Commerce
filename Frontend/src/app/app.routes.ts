import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { AdminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
    { path: '', loadComponent: () => import('./features/landing-page/landing-page').then(m => m.LandingPageComponent) },
    { path: 'shop', redirectTo: 'products', pathMatch: 'full' },
    {
        path: 'products',
        children: [
            { path: '', loadComponent: () => import('./features/product-catalog/product-list-page/product-list-page.component').then(m => m.ProductListPageComponent) },
            { path: ':id', loadComponent: () => import('./features/product-catalog/product-detail-page/product-detail-page.component').then(m => m.ProductDetailPageComponent) }
        ]
    },
    {
        path: 'admin',
        canActivate: [AdminGuard],
        children: [
            { path: '', redirectTo: 'categories', pathMatch: 'full' },
            { 
                path: 'categories', 
                loadComponent: () => import('./features/admin-management/category-crud/category-crud.component').then(m => m.CategoryCrudComponent) 
            },
            { 
                path: 'categories/new', 
                loadComponent: () => import('./features/admin-management/category-form/category-form.component').then(m => m.CategoryFormComponent) 
            },
            { 
                path: 'categories/edit/:id', 
                loadComponent: () => import('./features/admin-management/category-form/category-form.component').then(m => m.CategoryFormComponent) 
            },
            { 
                path: 'products', 
                loadComponent: () => import('./features/admin-management/product-crud/product-crud.component').then(m => m.ProductCrudComponent) 
            },
            { 
                path: 'products/new', 
                loadComponent: () => import('./features/admin-management/product-form/product-form.component').then(m => m.ProductFormComponent) 
            },
            { 
                path: 'products/edit/:id', 
                loadComponent: () => import('./features/admin-management/product-form/product-form.component').then(m => m.ProductFormComponent) 
            }
        ]
    },
    { 
        path: 'orders', 
        loadComponent: () => import('./features/orders/orders.component').then(c => c.OrdersComponent), 
        canActivate: [AuthGuard] 
    },
    {
        path: 'profile',
        loadComponent: () => import('./features/user-profile/user-profile.component').then(c => c.UserProfileComponent),
        canActivate: [AuthGuard]
    },
    { 
        path: 'cart', 
        loadComponent: () => import('./features/carts/component/component').then(c => c.CartComponent) 
    },
    { 
        path: 'login', 
        loadComponent: () => import('./features/login/login.component').then(c => c.LoginComponent) 
    },

    {
        path:'register',
        loadComponent: () => import('./features/register/register.component').then(c => c.RegisterComponent
            
        )
    }
   
];
