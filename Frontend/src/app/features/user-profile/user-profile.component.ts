import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { AddressService, Address } from '../../core/services/address.service';
import { PaymentMethodService, PaymentMethod } from '../../core/services/payment-method.service';
import { User } from '../../core/models/user';
import { MatDialog } from '@angular/material/dialog';
import { AddressFormComponent } from './address-form/address-form.component';
import { PaymentMethodFormComponent } from './payment-method-form/payment-method-form.component';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {
  user: User | null = null;
  addresses: Address[] = [];
  paymentMethods: PaymentMethod[] = [];
  activeSection: string = 'profile';
  successMessage: string = '';


  constructor(
    private authService: AuthService,
    private addressService: AddressService,
    private paymentMethodService: PaymentMethodService,
    public dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.loadUserData();
    if (this.user && this.user.id) {
      this.loadAddresses(this.user.id);
      this.loadPaymentMethods(this.user.id);
    }
  }

  loadUserData(): void {
    this.user = this.authService.getCurrentUser();
  }

  loadAddresses(userId: number): void {
    this.addressService.getAddressesByUserId(userId).subscribe((addresses: Address[]) => {
      this.addresses = addresses;
    });
  }

  loadPaymentMethods(userId: number): void {
    this.paymentMethodService.getPaymentMethodsByUserId(userId).subscribe((paymentMethods: PaymentMethod[]) => {
      this.paymentMethods = paymentMethods;
    });
  }

  setActiveSection(section: string): void {
    this.activeSection = section;
    this.successMessage = '';
  }

  addNewAddress(): void {
    const dialogRef = this.dialog.open(AddressFormComponent, {
      width: '400px',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && this.user?.id) {
        this.addressService.addAddress(this.user.id, result).subscribe(newAddress => {
          this.addresses.push(newAddress);
          this.successMessage = 'Address added successfully!';
          setTimeout(() => this.successMessage = '', 3000);
        });
      }
    });
  }

  addNewPaymentMethod(): void {
    const dialogRef = this.dialog.open(PaymentMethodFormComponent, {
        width: '400px',
        data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
        if (result && this.user?.id) {
            this.paymentMethodService.addPaymentMethod(this.user.id, result).subscribe(newPayment => {
                this.paymentMethods.push(newPayment);
                this.successMessage = 'Payment method added successfully!';
                setTimeout(() => this.successMessage = '', 3000);
            });
        }
    });
  }



  editAddress(address: Address): void {
    const dialogRef = this.dialog.open(AddressFormComponent, {
      width: '400px',
      data: address
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && this.user?.id) {
        this.addressService.updateAddress(this.user.id, address.id, result).subscribe(updatedAddress => {
          const index = this.addresses.findIndex(a => a.id === address.id);
          if (index !== -1) {
            this.addresses[index] = updatedAddress;
            this.successMessage = 'Address updated successfully!';
            setTimeout(() => this.successMessage = '', 3000);
          }
        });
      }
    });
  }

  editPaymentMethod(payment: PaymentMethod): void {
    const dialogRef = this.dialog.open(PaymentMethodFormComponent, {
      width: '400px',
      data: payment
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && this.user?.id) {
        this.paymentMethodService.updatePaymentMethod(payment.paymentMethodId, result).subscribe(updatedPayment => {
          const index = this.paymentMethods.findIndex(p => p.paymentMethodId === payment.paymentMethodId);
          if (index !== -1) {
            this.paymentMethods[index] = updatedPayment;
            this.successMessage = 'Payment method updated successfully!';
            setTimeout(() => this.successMessage = '', 3000);
          }
        });
      }
    });
  }

  deleteAddress(address: Address): void {
    if (confirm('Are you sure you want to delete this address?')) {
      this.addressService.deleteAddress(address.id).subscribe({
        next: () => {
          this.addresses = this.addresses.filter(a => a.id !== address.id);
          this.successMessage = 'Address deleted successfully!';
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          // Assume delete was successful even on error, as backend may return 404 but delete anyway
          this.addresses = this.addresses.filter(a => a.id !== address.id);
          this.successMessage = 'Address deleted successfully!';
          setTimeout(() => this.successMessage = '', 3000);
        }
      });
    }
  }

  deletePaymentMethod(payment: PaymentMethod): void {
    if (confirm('Are you sure you want to delete this payment method?')) {
      this.paymentMethodService.deletePaymentMethod(payment.paymentMethodId).subscribe({
        next: () => {
          this.paymentMethods = this.paymentMethods.filter(p => p.paymentMethodId !== payment.paymentMethodId);
          this.successMessage = 'Payment method deleted successfully!';
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          // Assume delete was successful even on error
          this.paymentMethods = this.paymentMethods.filter(p => p.paymentMethodId !== payment.paymentMethodId);
          this.successMessage = 'Payment method deleted successfully!';
          setTimeout(() => this.successMessage = '', 3000);
        }
      });
    }
  }
}
