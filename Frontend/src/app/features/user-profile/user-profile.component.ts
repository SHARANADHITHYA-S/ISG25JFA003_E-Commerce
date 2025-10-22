import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
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
  imports: [CommonModule],
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {
  user: User | null = null;
  addresses: Address[] = [];
  paymentMethods: PaymentMethod[] = [];
  activeSection: string = 'profile';

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
            });
        }
    });
  }
}
