import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { ProductResponseDTO } from '../../../core/models/product';
import { Router } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-featured-products',
  standalone: true,
  imports: [CommonModule, MatSnackBarModule],
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
    private snackBar: MatSnackBar
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

  // Helper function to create an array for the star rating loop
  getStarArray(count: number): number[] {
    return Array(count).fill(0);
  }

  // Helper function to determine how many stars should be filled (default 4 stars for all products)
  getFullStars(): number {
    return 4;
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
      this.snackBar.open('Please login to add items to cart', 'Close', {
        duration: 3000,
        horizontalPosition: 'end',
        verticalPosition: 'top',
        panelClass: ['snackbar-warning']
      });
      this.router.navigate(['/login']);
      return;
    }

    // Check if product has stock
    if (product.quantity <= 0) {
      this.snackBar.open('Product is out of stock', 'Close', {
        duration: 3000,
        horizontalPosition: 'end',
        verticalPosition: 'top',
        panelClass: ['snackbar-error']
      });
      return;
    }

    // Add to cart
    this.cartService.addCartItem(product.id, 1).subscribe({
      next: () => {
        this.snackBar.open('Product added to cart successfully!', 'Close', {
          duration: 3000,
          horizontalPosition: 'end',
          verticalPosition: 'top',
          panelClass: ['snackbar-success']
        });
      },
      error: (err) => {
        console.error('Error adding to cart:', err);
        this.snackBar.open('Failed to add product to cart', 'Close', {
          duration: 3000,
          horizontalPosition: 'end',
          verticalPosition: 'top',
          panelClass: ['snackbar-error']
        });
      }
    });
  }
}