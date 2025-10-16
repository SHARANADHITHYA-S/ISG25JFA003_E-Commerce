import { Component, HostListener, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CartService } from '../../../core/services/cart.service'; // Import CartService
import { CartResponse } from '../../../core/models/cart'; // Import CartResponse

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
  private cartService = inject(CartService); // Inject CartService

  ngOnInit(): void { // Implemented OnInit
    this.loadCartItemCount();
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
}
