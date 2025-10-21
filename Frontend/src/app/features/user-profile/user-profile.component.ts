import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { AddressService, Address } from '../../core/services/address.service';
import { PaymentMethodService, PaymentMethod } from '../../core/services/payment-method.service';
import { User } from '../../core/models/user';

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
    private paymentMethodService: PaymentMethodService
  ) { }

  ngOnInit(): void {
    this.loadUserData();
    this.loadAddresses();
    this.loadPaymentMethods();
  }

  loadUserData(): void {
    this.user = this.authService.getCurrentUser();
  }

  loadAddresses(): void {
    this.addressService.getAddressesByUserId().subscribe((addresses: Address[]) => {
      this.addresses = addresses;
    });
  }

  loadPaymentMethods(): void {
    this.paymentMethodService.getPaymentMethodsByUserId().subscribe((paymentMethods: PaymentMethod[]) => {
      this.paymentMethods = paymentMethods;
    });
  }

  setActiveSection(section: string): void {
    this.activeSection = section;
  }
}
