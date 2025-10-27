import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { ProductResponseDTO } from '../../../core/models/product';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-featured-products',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './featured-products.html'
})
export class FeaturedProductsComponent implements OnInit {
  products: ProductResponseDTO[] = [];
  isLoading = true;

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private authService: AuthService,
    private router: Router,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.isLoading = true;
    this.productService.getAllProducts().subscribe({
      next: (data) => {
        // Get only active products, limit to 4 for featured section
        this.products = data
          .filter(p => p.is_active)
          .slice(0, 4);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load products:', err);
        this.isLoading = false;
      }
    });
  }

  navigateToProduct(productId: number): void {
    this.router.navigate(['/products', productId]);
  }

  navigateToAllProducts(): void {
    this.router.navigate(['/products']);
  }

  addToCart(product: ProductResponseDTO, event: Event): void {
    event.stopPropagation(); // Prevent navigation to product details

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
    if (product.quantity <= 0) {
      this.messageService.add({
        severity: 'error',
        summary: 'Out of Stock',
        detail: '❌ Product is out of stock',
        life: 3000
      });
      return;
    }

    // Add to cart - notification is now handled by CartService
    this.cartService.addCartItem(product.id, 1).subscribe({
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