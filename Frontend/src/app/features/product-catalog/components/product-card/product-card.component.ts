import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ProductResponseDTO } from '../../../../core/models/product';
import { CartService } from '../../../../core/services/cart.service';
import { AuthService } from '../../../../core/services/auth.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule, RouterModule, MatSnackBarModule],
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.scss']
})
export class ProductCardComponent {
  @Input() product!: ProductResponseDTO;

  constructor(
    private cartService: CartService,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  addToCart(event: Event): void {
    event.preventDefault();
    event.stopPropagation();

    // Check if user is logged in
    if (!this.authService.isLoggedIn()) {
      this.snackBar.open('Please login to add items to cart', 'Close', {
        duration: 3000,
        horizontalPosition: 'end',
        verticalPosition: 'top',
        panelClass: ['warning-snackbar']
      });
      this.router.navigate(['/login']);
      return;
    }

    // Check if product has stock
    if (this.product.quantity <= 0) {
      this.snackBar.open('Product is out of stock', 'Close', {
        duration: 3000,
        horizontalPosition: 'end',
        verticalPosition: 'top',
        panelClass: ['error-snackbar']
      });
      return;
    }

    // Add to cart
    this.cartService.addCartItem(this.product.id, 1).subscribe({
      next: () => {
        this.snackBar.open('Product added to cart successfully!', 'Close', {
          duration: 3000,
          horizontalPosition: 'end',
          verticalPosition: 'top',
          panelClass: ['success-snackbar']
        });
      },
      error: (err) => {
        console.error('Error adding to cart:', err);
        this.snackBar.open('Failed to add product to cart', 'Close', {
          duration: 3000,
          horizontalPosition: 'end',
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
      }
    });
  }
}