import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Address, AddressService } from '../../../core/services/address.service';
import { PaymentMethod, PaymentMethodService } from '../../../core/services/payment-method.service';
import { OrderService } from '../../../core/services/order.service';
import { Router } from '@angular/router';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';
import { ErrorMessageComponent } from '../../../shared/components/error-message/error-message.component';
import { MatDialogRef } from '@angular/material/dialog';
import { forkJoin } from 'rxjs'; // Import forkJoin
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-checkout-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule, LoaderComponent, ErrorMessageComponent],
  templateUrl: './checkout-dialog.html',
  styleUrls: ['./checkout-dialog.scss']
})
export class CheckoutDialogComponent implements OnInit {
  addresses: Address[] = [];
  paymentMethods: PaymentMethod[] = [];
  selectedAddressId: number | null = null;
  selectedPaymentMethodId: number | null = null;
  isLoading = true;
  error: string | null = null;

  private addressService = inject(AddressService);
  private paymentMethodService = inject(PaymentMethodService);
  private orderService = inject(OrderService);
  private router = inject(Router);
  private dialogRef = inject(MatDialogRef<CheckoutDialogComponent>);
  private authService = inject(AuthService);

  ngOnInit(): void {
    this.loadCheckoutData();
  }

  loadCheckoutData(): void {
    this.isLoading = true;
    this.error = null;
    this.selectedAddressId = null; // Reset selections
    this.selectedPaymentMethodId = null; // Reset selections
    const userId = this.authService.getCurrentUser()?.id;
    if (!userId) {
      this.error = 'User not logged in.';
      this.isLoading = false;
      return;
    }

    forkJoin([
      this.addressService.getAddressesByUserId(userId),
      this.paymentMethodService.getPaymentMethodsByUserId(userId)
    ]).subscribe({
      next: ([addresses, paymentMethods]) => {
        this.addresses = addresses;
        if (addresses.length > 0) {
          this.selectedAddressId = addresses[0].id;
        } else {
          this.error = 'No addresses found. Please add one in your profile.';
        }

        this.paymentMethods = paymentMethods;
        if (paymentMethods.length > 0) {
          this.selectedPaymentMethodId = paymentMethods[0].paymentMethodId;
        } else {
          if (!this.error) { // Only set if no address error already
            this.error = 'No payment methods found. Please add one in your profile.';
          }
        }
        this.isLoading = false;
        console.log('loadCheckoutData - selectedAddressId:', this.selectedAddressId);
        console.log('loadCheckoutData - selectedPaymentMethodId:', this.selectedPaymentMethodId);
      },
      error: (err) => {
        console.error('Error loading checkout data:', err);
        this.error = 'Failed to load checkout options. Please try again.';
        this.isLoading = false;
      }
    });
  }

  placeOrder(): void {
    console.log('placeOrder - selectedAddressId:', this.selectedAddressId);
    console.log('placeOrder - selectedPaymentMethodId:', this.selectedPaymentMethodId);

    // Ensure selected IDs are numbers and not null/undefined
    const addressId = Number(this.selectedAddressId);
    const paymentMethodId = Number(this.selectedPaymentMethodId);

    if (isNaN(addressId) || isNaN(paymentMethodId) || addressId === null || paymentMethodId === null) {
      this.error = 'Please select both an address and a payment method.';
      return;
    }

    this.isLoading = true;
    this.error = null;

    this.orderService.createOrder(addressId, paymentMethodId).subscribe({
      next: (order) => {
        console.log('Order placed successfully:', order);
        this.isLoading = false;
        this.dialogRef.close('orderPlaced'); // Close dialog and pass result
      },
      error: (err) => {
        console.error('Error placing order:', err);
        this.error = 'Failed to place order. Please try again.';
        this.isLoading = false;
      }
    });
  }

  closeDialog(): void {
    this.dialogRef.close();
  }
}