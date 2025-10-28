import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ProductResponseDTO } from '../../../../core/models/product';
import { CartService } from '../../../../core/services/cart.service';
import { AuthService } from '../../../../core/services/auth.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.scss']
})
export class ProductCardComponent {
  @Input() product!: ProductResponseDTO;

  constructor(
    private cartService: CartService,
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService
  ) {}

  addToCart(event: Event): void {
    event.preventDefault();
    event.stopPropagation();

    // Check if user is logged in
    if (!this.authService.isLoggedIn()) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Login Required',
        detail: '🔒 Please login to add items to cart',
        life: 3000
      });
      this.router.navigate(['/login']);
      return;
    }

    // Check if product has stock
    if (this.product.quantity <= 0) {
      this.messageService.add({
        severity: 'error',
        summary: 'Out of Stock',
        detail: '❌ Product is out of stock',
        life: 3000
      });
      return;
    }

    // Add to cart - notification is now handled by CartService
    this.cartService.addCartItem(this.product.id, 1).subscribe({
      next: () => {
        // CartService already shows success notification
      },
      error: (err) => {
        console.error('Error adding to cart:', err);
        // CartService already shows error notification
      }
    });
  }
}