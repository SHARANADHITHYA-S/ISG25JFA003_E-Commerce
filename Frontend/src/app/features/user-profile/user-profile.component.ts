import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { AddressService, Address } from '../../core/services/address.service';
import { PaymentMethodService, PaymentMethod } from '../../core/services/payment-method.service';
import { UserService } from '../../core/services/user.service';
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
  editMode = false;
  editableUser: Partial<User> = {};

  constructor(
    private authService: AuthService,
    private addressService: AddressService,
    private paymentMethodService: PaymentMethodService,
    private userService: UserService,
    public dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.loadUserData();
  }

  loadUserData(): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser && currentUser.id) {
      this.userService.getUserById(currentUser.id).subscribe(
        (user: User) => {
          this.user = user;
          this.editableUser = { ...user }; // Initialize editableUser
          this.authService.setCurrentUser(user);
          // Load addresses and payment methods after user data is fetched
          this.loadAddresses(this.user.id);
          this.loadPaymentMethods(this.user.id);
        },
        error => {
          console.error('Error fetching user data:', error);
        }
      );
    } else {
      console.warn('User not logged in or userId missing.');
      this.user = null;
    }
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

  toggleEditMode(): void {
    this.editMode = !this.editMode;
    if (this.editMode && this.user) {
      this.editableUser = { ...this.user };
    } else if (!this.editMode && this.user) {
      // Reset editableUser if canceling edit
      this.editableUser = { ...this.user };
    }
  }

  saveUser(): void {
    if (this.user && this.user.id && this.editableUser) {
      // Create a new object with only username and email for the API call
      const userUpdate: { username?: string; email?: string } = {
        username: this.editableUser.name, // Use editableUser.username for the username field
        email: this.editableUser.email
      };

      this.userService.updateUser(this.user.id, userUpdate).subscribe(updatedUser => {
        this.user = updatedUser;
        this.authService.setCurrentUser(updatedUser);
        this.editMode = false;
      }, error => {
        console.error('Error updating user:', error);
      });
    }
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
