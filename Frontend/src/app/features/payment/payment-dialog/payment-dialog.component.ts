import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { Payment, PaymentType, PaymentStatus, ValidatePaymentRequest, CreditCardPayment, DebitCardPayment, UpiPayment } from '../../../core/models/payment.model';

@Component({
  selector: 'app-payment-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatIconModule,
    FormsModule
  ],
  template: `
    <div class="payment-dialog">
      <h2 mat-dialog-title class="dialog-title">
        <i class="bi bi-credit-card-2-front me-2"></i>
        Secure Payment
      </h2>
      <mat-dialog-content>
        <div class="payment-form">
          <!-- Amount Display -->
          <div class="amount-display mb-4">
            <span class="amount-label">Total Amount</span>
            <span class="amount-value">{{ data.amount | currency }}</span>
          </div>

          <!-- Payment Method Selection -->
          <mat-form-field appearance="outline" class="w-100 mb-3">
            <mat-label>Payment Method</mat-label>
            <mat-select [(ngModel)]="paymentData.paymentType" (selectionChange)="onPaymentTypeChange()">
              <mat-option [value]="PaymentType.CREDIT_CARD">
                <i class="bi bi-credit-card me-2"></i>Credit Card
              </mat-option>
              <mat-option [value]="PaymentType.DEBIT_CARD">
                <i class="bi bi-credit-card me-2"></i>Debit Card
              </mat-option>
              <mat-option [value]="PaymentType.UPI">
                <i class="bi bi-phone me-2"></i>UPI
              </mat-option>
            </mat-select>
          </mat-form-field>

          <!-- Card Payment Fields -->
          <ng-container *ngIf="paymentData.paymentType === PaymentType.CREDIT_CARD || paymentData.paymentType === PaymentType.DEBIT_CARD">
            <mat-form-field appearance="outline" class="w-100 mb-3">
              <mat-label>Card Number</mat-label>
              <input matInput 
                     [(ngModel)]="paymentData.cardNumber" 
                     maxlength="16" 
                     pattern="[0-9]*"
                     placeholder="1234 5678 9012 3456"
                     (input)="validateCardNumber()"
                     required>
              <mat-icon matSuffix>credit_card</mat-icon>
              <mat-error *ngIf="cardNumberError">{{ cardNumberError }}</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="w-100 mb-3">
              <mat-label>Cardholder Name</mat-label>
              <input matInput 
                     [(ngModel)]="paymentData.cardholderName" 
                     placeholder="John Doe"
                     (input)="validateCardholderName()"
                     required>
              <mat-icon matSuffix>person</mat-icon>
              <mat-error *ngIf="cardholderNameError">{{ cardholderNameError }}</mat-error>
            </mat-form-field>

            <div class="row mb-3">
              <div class="col-md-6">
                <mat-form-field appearance="outline" class="w-100">
                  <mat-label>Expiry Date</mat-label>
                  <input matInput 
                         [(ngModel)]="paymentData.expiryDate" 
                         placeholder="MM/YY"
                         maxlength="5"
                         (input)="formatExpiryDate()"
                         required>
                  <mat-icon matSuffix>calendar_today</mat-icon>
                  <mat-error *ngIf="expiryDateError">{{ expiryDateError }}</mat-error>
                </mat-form-field>
              </div>

              <div class="col-md-6">
                <mat-form-field appearance="outline" class="w-100">
                  <mat-label>CVV</mat-label>
                  <input matInput 
                         type="password" 
                         [(ngModel)]="paymentData.cvv" 
                         maxlength="3" 
                         pattern="[0-9]*"
                         placeholder="123"
                         (input)="validateCVV()"
                         required>
                  <mat-icon matSuffix>lock</mat-icon>
                  <mat-error *ngIf="cvvError">{{ cvvError }}</mat-error>
                </mat-form-field>
              </div>
            </div>

            <mat-form-field appearance="outline" class="w-100 mb-3">
              <mat-label>PIN</mat-label>
              <input matInput 
                     type="password" 
                     [(ngModel)]="paymentData.pin" 
                     maxlength="4" 
                     pattern="[0-9]*"
                     placeholder="Enter 4-digit PIN"
                     (input)="validatePin()"
                     required>
              <mat-icon matSuffix>vpn_key</mat-icon>
              <mat-error *ngIf="pinError">{{ pinError }}</mat-error>
            </mat-form-field>
          </ng-container>

          <!-- UPI Payment Fields -->
          <ng-container *ngIf="paymentData.paymentType === PaymentType.UPI">
            <mat-form-field appearance="outline" class="w-100 mb-3">
              <mat-label>UPI ID</mat-label>
              <input matInput 
                     [(ngModel)]="paymentData.upiId" 
                     placeholder="yourname@upi"
                     (input)="validateUpiId()"
                     required>
              <mat-icon matSuffix>phone_android</mat-icon>
              <mat-error *ngIf="upiIdError">{{ upiIdError }}</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="w-100 mb-3">
              <mat-label>UPI PIN</mat-label>
              <input matInput 
                     type="password" 
                     [(ngModel)]="paymentData.upiPin" 
                     maxlength="6" 
                     pattern="[0-9]*"
                     placeholder="Enter 6-digit PIN"
                     (input)="validateUpiPin()"
                     required>
              <mat-icon matSuffix>vpn_key</mat-icon>
              <mat-error *ngIf="upiPinError">{{ upiPinError }}</mat-error>
            </mat-form-field>
          </ng-container>

          <!-- Security Info -->
          <div class="security-info">
            <i class="bi bi-shield-check me-2"></i>
            <small>Your payment information is secure and encrypted</small>
          </div>
        </div>
      </mat-dialog-content>
      <mat-dialog-actions align="end" class="dialog-actions">
        <button mat-button (click)="onCancel()" class="cancel-btn">
          <i class="bi bi-x-circle me-1"></i>Cancel
        </button>
        <button mat-raised-button 
                color="primary" 
                [disabled]="!isFormValid() || isProcessing" 
                (click)="onConfirm()"
                class="confirm-btn">
          <span *ngIf="!isProcessing">
            <i class="bi bi-check-circle me-1"></i>Pay {{ data.amount | currency }}
          </span>
          <span *ngIf="isProcessing">
            <span class="spinner-border spinner-border-sm me-2"></span>Processing...
          </span>
        </button>
      </mat-dialog-actions>
    </div>
  `,
  styles: [`
    .payment-dialog {
      min-width: 450px;
      max-width: 600px;
    }

    .dialog-title {
      color: #0B2545;
      font-weight: 700;
      font-size: 1.5rem;
      border-bottom: 2px solid #EE7B30;
      padding-bottom: 1rem;
      margin-bottom: 1.5rem;
    }

    .payment-form {
      padding: 1rem 0;
    }

    .amount-display {
      background: linear-gradient(135deg, #0B2545 0%, #134074 100%);
      border-radius: 12px;
      padding: 1.5rem;
      text-align: center;
      color: white;
      box-shadow: 0 4px 15px rgba(11, 37, 69, 0.2);
    }

    .amount-label {
      display: block;
      font-size: 0.9rem;
      opacity: 0.9;
      margin-bottom: 0.5rem;
    }

    .amount-value {
      display: block;
      font-size: 2rem;
      font-weight: 700;
    }

    .w-100 {
      width: 100%;
    }

    ::ng-deep .mat-mdc-form-field {
      width: 100%;
    }

    ::ng-deep .mat-mdc-text-field-wrapper {
      background-color: rgba(255, 255, 255, 0.8);
    }

    .security-info {
      background-color: #e8f5e9;
      border-left: 3px solid #4caf50;
      padding: 0.75rem;
      border-radius: 4px;
      margin-top: 1rem;
      color: #2e7d32;
      font-size: 0.875rem;
    }

    .dialog-actions {
      padding: 1.5rem 1.5rem;
      border-top: 1px solid #e0e0e0;
      background-color: #f5f5f5;
    }

    .cancel-btn {
      color: #666;
      font-weight: 500;
    }

    .confirm-btn {
      background: linear-gradient(135deg, #EE7B30 0%, #ff9447 100%);
      color: white;
      font-weight: 600;
      padding: 0.75rem 2rem;
      border-radius: 25px;
      box-shadow: 0 4px 15px rgba(238, 123, 48, 0.3);
      transition: all 0.3s ease;
    }

    .confirm-btn:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(238, 123, 48, 0.4);
    }

    .confirm-btn:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    mat-error {
      font-size: 0.75rem;
    }
  `]
})
export class PaymentDialogComponent {
  PaymentType = PaymentType;
  isProcessing = false;
  
  paymentData = {
    paymentType: PaymentType.CREDIT_CARD,
    cardNumber: '',
    cardholderName: '',
    expiryDate: '',
    cvv: '',
    pin: '',
    upiId: '',
    upiPin: ''
  };

  // Validation error messages
  cardNumberError = '';
  cardholderNameError = '';
  expiryDateError = '';
  cvvError = '';
  pinError = '';
  upiIdError = '';
  upiPinError = '';

  constructor(
    public dialogRef: MatDialogRef<PaymentDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { orderId: number; amount: number }
  ) {}

  onPaymentTypeChange(): void {
    // Reset form fields when payment type changes
    this.resetValidationErrors();
    if (this.paymentData.paymentType === PaymentType.UPI) {
      this.paymentData.cardNumber = '';
      this.paymentData.cardholderName = '';
      this.paymentData.expiryDate = '';
      this.paymentData.cvv = '';
      this.paymentData.pin = '';
    } else {
      this.paymentData.upiId = '';
      this.paymentData.upiPin = '';
    }
  }

  validateCardNumber(): void {
    const cardNumber = this.paymentData.cardNumber.replace(/\s/g, '');
    if (!cardNumber) {
      this.cardNumberError = 'Card number is required';
    } else if (!/^[0-9]+$/.test(cardNumber)) {
      this.cardNumberError = 'Card number must contain only digits';
    } else if (cardNumber.length !== 16) {
      this.cardNumberError = 'Card number must be 16 digits';
    } else {
      this.cardNumberError = '';
    }
  }

  validateCardholderName(): void {
    if (!this.paymentData.cardholderName.trim()) {
      this.cardholderNameError = 'Cardholder name is required';
    } else if (this.paymentData.cardholderName.trim().length < 3) {
      this.cardholderNameError = 'Name must be at least 3 characters';
    } else {
      this.cardholderNameError = '';
    }
  }

  formatExpiryDate(): void {
    let value = this.paymentData.expiryDate.replace(/\D/g, '');
    if (value.length >= 2) {
      value = value.substring(0, 2) + '/' + value.substring(2, 4);
    }
    this.paymentData.expiryDate = value;
    this.validateExpiryDate();
  }

  validateExpiryDate(): void {
    const expiryPattern = /^(0[1-9]|1[0-2])\/\d{2}$/;
    if (!this.paymentData.expiryDate) {
      this.expiryDateError = 'Expiry date is required';
    } else if (!expiryPattern.test(this.paymentData.expiryDate)) {
      this.expiryDateError = 'Invalid format (MM/YY)';
    } else {
      // Check if date is not expired
      const [month, year] = this.paymentData.expiryDate.split('/');
      const expiry = new Date(2000 + parseInt(year), parseInt(month) - 1);
      const today = new Date();
      if (expiry < today) {
        this.expiryDateError = 'Card has expired';
      } else {
        this.expiryDateError = '';
      }
    }
  }

  validateCVV(): void {
    if (!this.paymentData.cvv) {
      this.cvvError = 'CVV is required';
    } else if (!/^[0-9]{3}$/.test(this.paymentData.cvv)) {
      this.cvvError = 'CVV must be 3 digits';
    } else {
      this.cvvError = '';
    }
  }

  validatePin(): void {
    if (!this.paymentData.pin) {
      this.pinError = 'PIN is required';
    } else if (!/^[0-9]{4}$/.test(this.paymentData.pin)) {
      this.pinError = 'PIN must be 4 digits';
    } else {
      this.pinError = '';
    }
  }

  validateUpiId(): void {
    const upiPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+$/;
    if (!this.paymentData.upiId) {
      this.upiIdError = 'UPI ID is required';
    } else if (!upiPattern.test(this.paymentData.upiId)) {
      this.upiIdError = 'Invalid UPI ID format';
    } else {
      this.upiIdError = '';
    }
  }

  validateUpiPin(): void {
    if (!this.paymentData.upiPin) {
      this.upiPinError = 'UPI PIN is required';
    } else if (!/^[0-9]{6}$/.test(this.paymentData.upiPin)) {
      this.upiPinError = 'UPI PIN must be 6 digits';
    } else {
      this.upiPinError = '';
    }
  }

  resetValidationErrors(): void {
    this.cardNumberError = '';
    this.cardholderNameError = '';
    this.expiryDateError = '';
    this.cvvError = '';
    this.pinError = '';
    this.upiIdError = '';
    this.upiPinError = '';
  }

  isFormValid(): boolean {
    if (this.paymentData.paymentType === PaymentType.UPI) {
      this.validateUpiId();
      this.validateUpiPin();
      return !!this.paymentData.upiId && 
             !!this.paymentData.upiPin &&
             !this.upiIdError &&
             !this.upiPinError;
    }

    // Card validation
    this.validateCardNumber();
    this.validateCardholderName();
    this.validateExpiryDate();
    this.validateCVV();
    this.validatePin();

    return !!(
      this.paymentData.cardNumber &&
      this.paymentData.cardholderName &&
      this.paymentData.expiryDate &&
      this.paymentData.cvv &&
      this.paymentData.pin &&
      !this.cardNumberError &&
      !this.cardholderNameError &&
      !this.expiryDateError &&
      !this.cvvError &&
      !this.pinError
    );
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onConfirm(): void {
    if (!this.isFormValid()) {
      return;
    }

    this.isProcessing = true;

    // Create payment details based on payment type
    const paymentDetails = this.paymentData.paymentType === PaymentType.UPI
      ? {
          upiId: this.paymentData.upiId,
          pin: this.paymentData.upiPin
        }
      : {
          cardNumber: this.paymentData.cardNumber,
          cardHolderName: this.paymentData.cardholderName,
          expiryDate: this.paymentData.expiryDate,
          cvv: this.paymentData.cvv,
          pin: this.paymentData.pin
        };

    // Create a ValidatePaymentRequest object
    const validateRequest: ValidatePaymentRequest = {
      orderId: this.data.orderId,
      paymentType: this.paymentData.paymentType,
      paymentDetails
    };

    // Simulate processing delay
    setTimeout(() => {
      this.dialogRef.close(validateRequest);
    }, 1000);
  }
}
