import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../../core/services/cart.service';
import { CartResponse, CartItemResponse, CartItemRequest } from '../../../core/models/cart';
import { MatDialog, MatDialogModule } from '@angular/material/dialog'; // Import MatDialog and MatDialogModule
import { CheckoutDialogComponent } from '../components/checkout-dialog/checkout-dialog.component'; // Import CheckoutDialogComponent
import { Router } from '@angular/router'; // Import Router
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar'; // Import MatSnackBar and MatSnackBarModule

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatSnackBarModule], // Add MatSnackBarModule to imports
  templateUrl: './component.html',
  styleUrls: ['./component.scss']
})
export class CartComponent implements OnInit {
  cart: CartResponse | null = null;
  isLoading = true;
  error: string | null = null;

  constructor(
    private cartService: CartService,
    private dialog: MatDialog,
    private router: Router,
    private snackBar: MatSnackBar // Inject MatSnackBar
  ) { }

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.isLoading = true;
    this.error = null;
            this.cartService.getCart().subscribe({
                next: (data) => {
                    console.log('Cart data received:', data); // Debug log
                    data.items.forEach(item => console.log(`Cart Item: ${item.productName}, Image URL: ${item.image_url}`)); // Log image_url
                    this.cart = data;
                    this.isLoading = false;
                },      error: (err) => {
        this.cart = null;
        this.isLoading = false;
        this.error = "Failed to load cart. Please try again.";
        console.error('Failed to load cart:', err);
        // Detailed error logging
        if (err.error instanceof ErrorEvent) {
          // Client-side error
          console.error('Client error:', err.error.message);
        } else {
          // Server-side error
          console.error('Server error:', {
            status: err.status,
            message: err.message,
            error: err.error
          });
        }
      }
    });
  }

  removeItem(itemId: number): void {
    if (!confirm('Are you sure you want to remove this item?')) return;

    this.cartService.removeCartItem(itemId).subscribe({
      next: () => {
        if (this.cart) {
          // Remove item locally
          this.cart.items = this.cart.items.filter(item => item.id !== itemId);
          this.recalculateCartTotal();
        }
      },
      error: (err) => {
        this.error = 'Failed to remove item.';
        console.error('Error removing item:', err);
      }
    });
  }

  updateQuantity(item: CartItemResponse, event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    const newQuantity = parseInt(inputElement.value, 10);

    if (isNaN(newQuantity) || newQuantity < 1) {
      this.snackBar.open('Quantity must be a positive number.', 'Dismiss', { duration: 3000 });
      inputElement.value = item.quantity.toString();
      return;
    }

    const itemRequest: CartItemRequest = { productId: item.productId, quantity: newQuantity };

    this.cartService.updateCartItem(item.id, itemRequest).subscribe({
      next: (updatedItem) => {
        // Update local data instead of reloading
        if (this.cart) {
          const index = this.cart.items.findIndex(i => i.id === updatedItem.id);
          if (index !== -1) {
            this.cart.items[index] = updatedItem;
            this.recalculateCartTotal();
          }
        }
      },
      error: (err) => {
        this.snackBar.open('Failed to update quantity. The item may be out of stock.', 'Dismiss', { duration: 3000 });
        inputElement.value = item.quantity.toString(); // Reset the input value
      }
    });
  }

  clearCart(): void {
    if (!this.cart || !confirm('Are you sure you want to empty your entire cart?')) return;

    this.cartService.clearCart().subscribe({
      next: () => {
        // Update local state
        if (this.cart) {
          this.cart.items = [];
          this.cart.totalPrice = 0;
        }
      },
      error: (err) => {
        this.error = 'Failed to clear the cart.';
        console.error('Error clearing cart:', err);
      }
    });
  }

  openCheckoutDialog(): void {
    const dialogRef = this.dialog.open(CheckoutDialogComponent, {
      width: '600px',
      disableClose: true // Prevent closing by clicking outside or pressing escape
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'orderPlaced') {
        // Order was successfully placed, navigate to orders page
        this.router.navigate(['/orders']);
      }
      // If dialog was cancelled or closed without placing order, do nothing
    });
  }

  navigateToProduct(productId: number): void {
    this.router.navigate(['/products', productId]);
  }

  private recalculateCartTotal(): void {
    if (this.cart) {
      this.cart.totalPrice = this.cart.items.reduce((total, item) => {
        return total + (Number(item.price) * Number(item.quantity));
      }, 0);
    }
  }

  get totalItems(): number {
    if (!this.cart || !this.cart.items) {
      return 0;
    }
    return this.cart.items.reduce((sum, item) => sum + item.quantity, 0);
  }

  getProductImage(item: CartItemResponse): string {
    if (item && item.product && item.product.image_url) {
      return item.product.image_url;
    }

    // Fallback to hardcoded mapping
    const imageMap: { [key: string]: string } = {
      'Atomic Habits': '/atomichabitsbk.jpg',
      'The Alchemist': '/thealchemistbk.jpg',
      'Clean Code': '/cleancodebk.jpg',
      'Headset': '/headset.jpg',
      'Kettle': '/kettle.jpg',
      'LED TV': '/ledtv.jpg',
      'Lipstick': '/lipstick.jpg',
      'Pan': '/pan.jpg',
      'Pillow': '/pillow.jpg',
      'Serum': '/serum.jpg',
      'Shampoo': '/shampoo.jpg',
      'Smartwatch': '/smartwatch.jpg'
    };

    if (imageMap[item.productName]) {
      return imageMap[item.productName];
    }

    for (const key in imageMap) {
      if (item.productName.toLowerCase().includes(key.toLowerCase())) {
        return imageMap[key];
      }
    }

    return '/favicon.ico';
  }

  incrementQuantity(item: CartItemResponse, event: Event): void {
    event.stopPropagation();
    const newQuantity = item.quantity + 1;
    const itemRequest: CartItemRequest = { productId: item.productId, quantity: newQuantity };

    this.cartService.updateCartItem(item.id, itemRequest).subscribe({
      next: (updatedItem) => {
        if (this.cart) {
          const index = this.cart.items.findIndex(i => i.id === updatedItem.id);
          if (index !== -1) {
            this.cart.items[index] = updatedItem;
            this.recalculateCartTotal();
          }
        }
      },
      error: (err) => {
        this.snackBar.open('Failed to update quantity.', 'Dismiss', { duration: 3000 });
      }
    });
  }

  decrementQuantity(item: CartItemResponse, event: Event): void {
    event.stopPropagation();
    if (item.quantity <= 1) return;

    const newQuantity = item.quantity - 1;
    const itemRequest: CartItemRequest = { productId: item.productId, quantity: newQuantity };

    this.cartService.updateCartItem(item.id, itemRequest).subscribe({
      next: (updatedItem) => {
        if (this.cart) {
          const index = this.cart.items.findIndex(i => i.id === updatedItem.id);
          if (index !== -1) {
            this.cart.items[index] = updatedItem;
            this.recalculateCartTotal();
          }
        }
      },
      error: (err) => {
        this.snackBar.open('Failed to update quantity.', 'Dismiss', { duration: 3000 });
      }
    });
  }
}
