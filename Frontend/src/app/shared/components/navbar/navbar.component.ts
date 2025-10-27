import { Component, HostListener, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CartService } from '../../../core/services/cart.service'; // Import CartService
import { CartResponse } from '../../../core/models/cart'; // Import CartResponse
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  isScrolled = false;
  cartItemCount = 0; // New property
  isLoggedIn = false;
  isAdmin = false; // New property
  private cartService = inject(CartService); // Inject CartService
  private authService = inject(AuthService);
  private router = inject(Router);

  ngOnInit(): void { // Implemented OnInit
    this.loadCartItemCount();
    
    // Subscribe to cart changes for dynamic updates
    this.cartService.cartChanges$.subscribe(() => {
      this.loadCartItemCount();
    });
    
    this.authService.loggedIn$.subscribe(isLoggedIn => {
      this.isLoggedIn = isLoggedIn;
      const user = this.authService.getCurrentUser();
      this.isAdmin = !!(user && (user.role === 'ADMIN' || user.role === 'ROLE_ADMIN'));
      
      // Reload cart count when user logs in/out
      if (isLoggedIn) {
        this.loadCartItemCount();
      } else {
        this.cartItemCount = 0;
      }
    });
  }

  @HostListener('window:scroll', [])
  onWindowScroll() {
    this.isScrolled = window.scrollY > 50;
  }

  private loadCartItemCount(): void {
    this.cartService.getCart().subscribe({
        next: (cart: CartResponse | null) => {
            this.cartItemCount = cart?.items?.reduce((sum: number, item: any) => sum + item.quantity, 0) || 0;
        },
        error: (error) => {
            console.error('Error loading cart item count in navbar:', error);
            this.cartItemCount = 0;
        }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
