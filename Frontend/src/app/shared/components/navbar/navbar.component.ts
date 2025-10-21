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
  private cartService = inject(CartService); // Inject CartService
  private authService = inject(AuthService);
  private router = inject(Router);

  ngOnInit(): void { // Implemented OnInit
    this.loadCartItemCount();
    this.checkLoginStatus();
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

  private checkLoginStatus(): void {
    this.isLoggedIn = this.authService.isLoggedIn();
  }

  logout(): void {
    this.authService.logout();
    this.isLoggedIn = false;
    this.router.navigate(['/login']);
  }

  get isAdmin(): boolean {
    return this.authService.getUserRole() === 'ROLE_ADMIN';
  }
}
