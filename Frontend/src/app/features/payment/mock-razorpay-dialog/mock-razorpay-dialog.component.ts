import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';

export interface MockRazorpayDialogData {
  orderId: number;
  amount: number;
  userName?: string;
  userEmail?: string;
}

export interface PaymentResponse {
  success: boolean;
  paymentId: string;
  orderId: number;
  amount: number;
  paymentMethod: string;
}

@Component({
  selector: 'app-mock-razorpay-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatTabsModule,
    MatIconModule,
    FormsModule
  ],
  template: `
    <div class="razorpay-modal">
      <!-- Header -->
      <div class="razorpay-header">
        <div class="razorpay-logo">
          <i class="bi bi-shield-lock-fill"></i>
          <span>SecurePay</span>
        </div>
        <button mat-icon-button (click)="onClose()" class="close-btn">
          <mat-icon>close</mat-icon>
        </button>
      </div>

      <!-- Merchant Info -->
      <div class="merchant-info">
        <div class="merchant-logo">
          <i class="bi bi-shop"></i>
        </div>
        <div class="merchant-details">
          <h4>E-Commerce Store</h4>
          <p>Order #{{data.orderId}}</p>
        </div>
      </div>

      <!-- Amount Display -->
      <div class="amount-display">
        <span class="amount-label">Amount to Pay</span>
        <span class="amount-value">{{data.amount | currency:'INR':'symbol':'1.2-2'}}</span>
      </div>

      <!-- Processing State -->
      <div *ngIf="isProcessing" class="processing-overlay">
        <div class="processing-content">
          <div class="spinner"></div>
          <h4>{{processingMessage}}</h4>
          <p>Please wait, do not close this window...</p>
        </div>
      </div>

      <!-- Success State -->
      <div *ngIf="showSuccess" class="success-overlay">
        <div class="success-content">
          <div class="success-icon">
            <i class="bi bi-check-circle-fill"></i>
          </div>
          <h3>Payment Successful!</h3>
          <p class="payment-id">Payment ID: {{generatedPaymentId}}</p>
          <p class="success-message">Your payment has been processed successfully</p>
          <button mat-raised-button color="primary" (click)="onSuccessClose()">
            Continue
          </button>
        </div>
      </div>

      <!-- Payment Methods -->
      <mat-tab-group *ngIf="!isProcessing && !showSuccess" class="payment-tabs" [(selectedIndex)]="selectedTabIndex">
        <!-- UPI Tab -->
        <mat-tab>
          <ng-template mat-tab-label>
            <i class="bi bi-phone me-1"></i>
            <span>UPI</span>
          </ng-template>
          <div class="payment-method-content">
            
            <!-- UPI Apps -->
            <div class="upi-apps">
              <button class="upi-app-btn" (click)="payWithUpiApp('Google Pay')">
                <div class="app-icon">
                  <svg viewBox="0 0 512 512" width="40" height="40">
                    <path fill="#5F6368" d="M288 256c0 17.7-14.3 32-32 32s-32-14.3-32-32 14.3-32 32-32 32 14.3 32 32z"/>
                    <path fill="#4285F4" d="M409.8 148.8c-13.2-24.1-32.1-44.6-55.1-60C327.6 71.3 293.4 64 256 64c-37.4 0-71.6 7.3-98.7 24.8-23 15.4-41.9 35.9-55.1 60C87.4 180 80 217.2 80 256s7.4 76 22.2 107.2c13.2 24.1 32.1 44.6 55.1 60C184.4 440.7 218.6 448 256 448c37.4 0 71.6-7.3 98.7-24.8 23-15.4 41.9-35.9 55.1-60C424.6 332 432 294.8 432 256s-7.4-76-22.2-107.2z"/>
                    <path fill="#34A853" d="M256 352c-53 0-96-43-96-96s43-96 96-96 96 43 96 96-43 96-96 96zm0-160c-35.3 0-64 28.7-64 64s28.7 64 64 64 64-28.7 64-64-28.7-64-64-64z"/>
                    <path fill="#FBBC04" d="M144 256c0-61.9 50.1-112 112-112 28.6 0 54.7 10.7 74.5 28.3l23.3-23.3C325 123.7 292.3 112 256 112c-79.5 0-144 64.5-144 144s64.5 144 144 144c36.3 0 69-11.7 95.8-37l-23.3-23.3C352.7 357.3 326.6 368 256 368c-61.9 0-112-50.1-112-112z"/>
                  </svg>
                </div>
                <span>Google Pay</span>
              </button>
              
              <button class="upi-app-btn" (click)="payWithUpiApp('PhonePe')">
                <div class="app-icon phonepe-icon">
                  <svg viewBox="0 0 40 40" width="40" height="40">
                    <rect fill="#5f259f" width="40" height="40" rx="8"/>
                    <text x="20" y="28" font-family="Arial" font-size="24" font-weight="bold" fill="white" text-anchor="middle">P</text>
                  </svg>
                </div>
                <span>PhonePe</span>
              </button>
              
              <button class="upi-app-btn" (click)="payWithUpiApp('Paytm')">
                <div class="app-icon paytm-icon">
                  <svg viewBox="0 0 40 40" width="40" height="40">
                    <defs>
                      <linearGradient id="paytmGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                        <stop offset="0%" style="stop-color:#00BAF2;stop-opacity:1" />
                        <stop offset="100%" style="stop-color:#002E6E;stop-opacity:1" />
                      </linearGradient>
                    </defs>
                    <rect fill="url(#paytmGrad)" width="40" height="40" rx="8"/>
                    <text x="20" y="28" font-family="Arial" font-size="24" font-weight="bold" fill="white" text-anchor="middle">₹</text>
                  </svg>
                </div>
                <span>Paytm</span>
              </button>
              
              <button class="upi-app-btn" (click)="payWithUpiApp('BHIM')">
                <div class="app-icon bhim-icon">
                  <svg viewBox="0 0 40 40" width="40" height="40">
                    <rect fill="#FF6600" width="40" height="40" rx="8"/>
                    <text x="20" y="28" font-family="Arial" font-size="20" font-weight="bold" fill="white" text-anchor="middle">B</text>
                  </svg>
                </div>
                <span>BHIM</span>
              </button>
            </div>

            <div class="divider">OR</div>

            <!-- UPI ID Input -->
            <mat-form-field appearance="outline" class="w-100">
              <mat-label>Enter UPI ID</mat-label>
              <input matInput 
                     [(ngModel)]="upiId" 
                     placeholder="yourname@upi"
                     (keyup.enter)="payWithUpiId()">
              <mat-icon matSuffix>account_balance</mat-icon>
            </mat-form-field>

            <button mat-raised-button 
                    color="primary" 
                    class="w-100 pay-btn"
                    [disabled]="!upiId"
                    (click)="payWithUpiId()">
              Pay {{data.amount | currency:'INR':'symbol':'1.2-2'}}
            </button>

            <div class="qr-section">
              <p class="text-center mb-2">Scan QR Code to Pay</p>
              <div class="qr-code">
                <div class="qr-pattern">
                  <div class="qr-corner tl"></div>
                  <div class="qr-corner tr"></div>
                  <div class="qr-corner bl"></div>
                  <div class="qr-corner br"></div>
                  <div class="qr-center">
                    <i class="bi bi-upc-scan"></i>
                  </div>
                </div>
              </div>
              <p class="text-center text-muted">Scan with any UPI app</p>
            </div>
          </div>
        </mat-tab>

        <!-- Cards Tab -->
        <mat-tab>
          <ng-template mat-tab-label>
            <i class="bi bi-credit-card me-2"></i>
            <span>Cards</span>
          </ng-template>
          <div class="payment-method-content">
            <h4>Credit / Debit / ATM Card</h4>

            <mat-form-field appearance="outline" class="w-100">
              <mat-label>Card Number</mat-label>
              <input matInput 
                     [(ngModel)]="cardNumber" 
                     maxlength="19"
                     placeholder="1234 5678 9012 3456"
                     (input)="formatCardNumber()">
              <mat-icon matSuffix>credit_card</mat-icon>
            </mat-form-field>

            <mat-form-field appearance="outline" class="w-100">
              <mat-label>Cardholder Name</mat-label>
              <input matInput 
                     [(ngModel)]="cardholderName" 
                     placeholder="Name on card">
              <mat-icon matSuffix>person</mat-icon>
            </mat-form-field>

            <div class="row">
              <div class="col-6">
                <mat-form-field appearance="outline" class="w-100">
                  <mat-label>Expiry (MM/YY)</mat-label>
                  <input matInput 
                         [(ngModel)]="expiryDate" 
                         maxlength="5"
                         placeholder="12/28"
                         (input)="formatExpiry()">
                  <mat-icon matSuffix>calendar_today</mat-icon>
                </mat-form-field>
              </div>
              <div class="col-6">
                <mat-form-field appearance="outline" class="w-100">
                  <mat-label>CVV</mat-label>
                  <input matInput 
                         type="password"
                         [(ngModel)]="cvv" 
                         maxlength="3"
                         placeholder="123">
                  <mat-icon matSuffix>lock</mat-icon>
                </mat-form-field>
              </div>
            </div>

            <button mat-raised-button 
                    color="primary" 
                    class="w-100 pay-btn"
                    [disabled]="!isCardFormValid()"
                    (click)="payWithCard()">
              Pay {{data.amount | currency:'INR':'symbol':'1.2-2'}}
            </button>

            <div class="secure-note">
              <i class="bi bi-shield-check me-2"></i>
              <small>Your card details are encrypted and secure</small>
            </div>
          </div>
        </mat-tab>

        <!-- Net Banking Tab -->
        <mat-tab>
          <ng-template mat-tab-label>
            <i class="bi bi-bank me-2"></i>
            <span>Net Banking</span>
          </ng-template>
          <div class="payment-method-content">
            <h4>Select Your Bank</h4>

            <div class="bank-list">
              <button class="bank-btn" 
                      *ngFor="let bank of popularBanks"
                      (click)="payWithNetBanking(bank)">
                <div class="bank-icon">
                  <i class="bi bi-bank2"></i>
                </div>
                <span>{{bank}}</span>
              </button>
            </div>

            <mat-form-field appearance="outline" class="w-100 mt-3">
              <mat-label>Other Banks</mat-label>
              <input matInput 
                     [(ngModel)]="selectedBank" 
                     placeholder="Select or type bank name"
                     list="bankList">
              <mat-icon matSuffix>search</mat-icon>
            </mat-form-field>
            <datalist id="bankList">
              <option *ngFor="let bank of allBanks" [value]="bank"></option>
            </datalist>

            <button mat-raised-button 
                    color="primary" 
                    class="w-100 pay-btn"
                    [disabled]="!selectedBank"
                    (click)="payWithNetBanking(selectedBank)">
              Pay {{data.amount | currency:'INR':'symbol':'1.2-2'}}
            </button>
          </div>
        </mat-tab>

        <!-- Wallets Tab -->
        <mat-tab>
          <ng-template mat-tab-label>
            <i class="bi bi-wallet2 me-2"></i>
            <span>Wallets</span>
          </ng-template>
          <div class="payment-method-content">
            <h4>Pay with Wallet</h4>

            <div class="wallet-list">
              <button class="wallet-btn" (click)="payWithWallet('Paytm')">
                <div class="wallet-icon paytm-wallet">
                  <i class="bi bi-wallet-fill"></i>
                </div>
                <span>Paytm</span>
              </button>
              <button class="wallet-btn" (click)="payWithWallet('Mobikwik')">
                <div class="wallet-icon mobikwik-wallet">
                  <i class="bi bi-wallet-fill"></i>
                </div>
                <span>Mobikwik</span>
              </button>
              <button class="wallet-btn" (click)="payWithWallet('Freecharge')">
                <div class="wallet-icon freecharge-wallet">
                  <i class="bi bi-wallet-fill"></i>
                </div>
                <span>Freecharge</span>
              </button>
              <button class="wallet-btn" (click)="payWithWallet('Amazon Pay')">
                <div class="wallet-icon amazon-wallet">
                  <i class="bi bi-wallet-fill"></i>
                </div>
                <span>Amazon Pay</span>
              </button>
            </div>
          </div>
        </mat-tab>
      </mat-tab-group>

      <!-- Footer -->
      <div class="razorpay-footer" *ngIf="!isProcessing && !showSuccess">
        <div class="security-badges">
          <i class="bi bi-shield-check"></i>
          <span>Secured by 256-bit SSL</span>
        </div>
        <div class="powered-by">
          <small>Powered by SecurePay</small>
        </div>
      </div>
    </div>
  `,
  styles: [`
    /* Remove Material Dialog default padding and styling */
    ::ng-deep .cdk-overlay-pane {
      max-width: 90vw !important;
    }

    ::ng-deep .mat-mdc-dialog-container {
      padding: 0 !important;
      overflow: hidden !important;
      background: transparent !important;
      box-shadow: none !important;
    }

    ::ng-deep .mat-mdc-dialog-surface {
      padding: 0 !important;
      overflow: hidden !important;
      background: transparent !important;
      box-shadow: 0 11px 15px -7px rgba(0, 0, 0, 0.2), 
                  0 24px 38px 3px rgba(0, 0, 0, 0.14), 
                  0 9px 46px 8px rgba(0, 0, 0, 0.12) !important;
      border-radius: 12px !important;
    }

    .razorpay-modal {
      width: 480px;
      max-height: 85vh;
      overflow: hidden;
      background: #f5f7fa;
      position: relative;
      display: flex;
      flex-direction: column;
      border-radius: 12px;
      box-shadow: 0 11px 15px -7px rgba(0, 0, 0, 0.2), 
                  0 24px 38px 3px rgba(0, 0, 0, 0.14), 
                  0 9px 46px 8px rgba(0, 0, 0, 0.12);
    }

    .razorpay-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.75rem 1rem;
      border-bottom: 1px solid #e0e0e0;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      flex-shrink: 0;
    }

    .razorpay-logo {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      font-size: 1rem;
      font-weight: 600;
    }

    .razorpay-logo i {
      font-size: 1.25rem;
    }

    .close-btn {
      color: white !important;
    }

    .merchant-info {
      display: flex;
      align-items: center;
      padding: 0.75rem 1rem;
      border-bottom: 1px solid #e0e0e0;
      background: #f8f9fa;
      flex-shrink: 0;
    }

    .merchant-logo {
      width: 40px;
      height: 40px;
      border-radius: 8px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 0.75rem;
      color: white;
      font-size: 1.25rem;
    }

    .merchant-details h4 {
      margin: 0;
      font-size: 0.95rem;
      font-weight: 600;
      color: #333;
    }

    .merchant-details p {
      margin: 0.15rem 0 0 0;
      font-size: 0.8rem;
      color: #666;
    }

    .amount-display {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.75rem 1rem;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      flex-shrink: 0;
    }

    .amount-label {
      font-size: 0.8rem;
      opacity: 0.9;
    }

    .amount-value {
      font-size: 1.5rem;
      font-weight: 700;
    }

    .payment-tabs {
      flex: 1;
      overflow: hidden;
      display: flex;
      flex-direction: column;
    }

    ::ng-deep .payment-tabs .mat-mdc-tab-body-wrapper {
      flex: 1;
      overflow-y: auto;
      scroll-behavior: smooth;
      scrollbar-width: thin;
      scrollbar-color: #d0d0d0 transparent;
      margin: 0 !important;
      padding: 0 !important;
    }

    ::ng-deep .payment-tabs .mat-mdc-tab-body-wrapper::-webkit-scrollbar {
      width: 6px;
    }

    ::ng-deep .payment-tabs .mat-mdc-tab-body-wrapper::-webkit-scrollbar-track {
      background: transparent;
      margin: 0;
    }

    ::ng-deep .payment-tabs .mat-mdc-tab-body-wrapper::-webkit-scrollbar-thumb {
      background: #d0d0d0;
      border-radius: 3px;
    }

    ::ng-deep .payment-tabs .mat-mdc-tab-body-wrapper::-webkit-scrollbar-thumb:hover {
      background: #b0b0b0;
    }

    ::ng-deep .payment-tabs .mat-mdc-tab-body-content {
      overflow-y: auto !important;
      max-height: none !important;
      padding: 0 !important;
      margin: 0 !important;
    }

    .payment-method-content {
      padding: 1rem;
      background: #fafbfc;
    }

    .payment-method-content h4 {
      font-size: 0.95rem;
      font-weight: 600;
      color: #333;
      margin-bottom: 1rem;
      padding-bottom: 0.5rem;
      border-bottom: 2px solid #e8eaed;
    }

    .secure-note {
      display: flex;
      align-items: center;
      margin-top: 0.75rem;
      padding: 0.75rem;
      background: #e8f5e9;
      border-radius: 6px;
      color: #2e7d32;
      font-size: 0.8rem;
    }

    .secure-note i {
      font-size: 1.1rem;
      margin-right: 0.5rem;
    }

    .upi-apps {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 0.75rem;
      margin-bottom: 1rem;
    }

    .upi-app-btn {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 0.4rem;
      padding: 0.75rem 0.4rem;
      border: 1.5px solid #e0e0e0;
      border-radius: 8px;
      background: #ffffff;
      cursor: pointer;
      transition: all 0.2s ease;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
    }

    .upi-app-btn:hover {
      border-color: #667eea;
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
      background: #f8f9ff;
    }

    .app-icon {
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .upi-app-btn span {
      font-size: 0.7rem;
      font-weight: 500;
      color: #333;
    }

    .divider {
      text-align: center;
      margin: 1rem 0;
      position: relative;
      color: #999;
      font-size: 0.8rem;
    }

    .divider::before,
    .divider::after {
      content: '';
      position: absolute;
      top: 50%;
      width: 42%;
      height: 1px;
      background: #e0e0e0;
    }

    .divider::before { left: 0; }
    .divider::after { right: 0; }

    .qr-section {
      margin-top: 1.5rem;
      padding: 1.5rem;
      border-top: 1px solid #e0e0e0;
      background: #ffffff;
      border-radius: 8px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
    }

    .qr-section p {
      font-size: 0.85rem;
      color: #666;
    }

    .qr-code {
      width: 150px;
      height: 150px;
      margin: 0.75rem auto;
      border: 2px solid #667eea;
      border-radius: 8px;
      padding: 0.75rem;
      background: white;
      box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
    }

    .qr-pattern {
      width: 100%;
      height: 100%;
      position: relative;
      background: 
        linear-gradient(90deg, #333 1.5px, transparent 1.5px),
        linear-gradient(#333 1.5px, transparent 1.5px);
      background-size: 15px 15px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .qr-corner {
      position: absolute;
      width: 30px;
      height: 30px;
      border: 2.5px solid #667eea;
    }

    .qr-corner.tl { top: 0; left: 0; border-right: none; border-bottom: none; }
    .qr-corner.tr { top: 0; right: 0; border-left: none; border-bottom: none; }
    .qr-corner.bl { bottom: 0; left: 0; border-right: none; border-top: none; }
    .qr-corner.br { bottom: 0; right: 0; border-left: none; border-top: none; }

    .qr-center {
      font-size: 2.5rem;
      color: #667eea;
      background: white;
      padding: 0.35rem;
      border-radius: 6px;
    }

    .bank-list {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 0.75rem;
      margin-bottom: 0.75rem;
    }

    .bank-btn, .wallet-btn {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      padding: 0.75rem;
      border: 1.5px solid #e0e0e0;
      border-radius: 8px;
      background: #ffffff;
      cursor: pointer;
      transition: all 0.2s ease;
      text-align: left;
      font-size: 0.85rem;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
    }

    .bank-btn:hover, .wallet-btn:hover {
      border-color: #667eea;
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
      background: #f8f9ff;
    }

    .bank-icon {
      width: 38px;
      height: 38px;
      border-radius: 6px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 1.25rem;
      flex-shrink: 0;
    }

    .wallet-list {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 0.75rem;
    }

    .wallet-btn {
      flex-direction: column;
      text-align: center;
      padding: 0.75rem 0.5rem;
      background: #ffffff;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
    }

    .wallet-icon {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 1.75rem;
    }

    .wallet-icon.paytm-wallet { background: linear-gradient(135deg, #00b9f5, #002e6e); }
    .wallet-icon.mobikwik-wallet { background: linear-gradient(135deg, #d32f2f, #c62828); }
    .wallet-icon.freecharge-wallet { background: linear-gradient(135deg, #fbc02d, #f57c00); }
    .wallet-icon.amazon-wallet { background: linear-gradient(135deg, #ff9900, #146eb4); }

    .pay-btn {
      height: 42px;
      font-size: 0.9rem;
      font-weight: 600;
      margin-top: 0.75rem;
    }

    .razorpay-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.75rem 1rem;
      border-top: 1px solid #e0e0e0;
      background: #f8f9fa;
      font-size: 0.75rem;
      color: #666;
      flex-shrink: 0;
    }

    .security-badges {
      display: flex;
      align-items: center;
      gap: 0.4rem;
    }

    .security-badges i {
      color: #28a745;
      font-size: 1rem;
    }

    .processing-overlay,
    .success-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(255, 255, 255, 0.98);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
    }

    .processing-content,
    .success-content {
      text-align: center;
      padding: 2rem;
    }

    .spinner {
      width: 50px;
      height: 50px;
      border: 3px solid #f3f3f3;
      border-top: 3px solid #667eea;
      border-radius: 50%;
      animation: spin 1s linear infinite;
      margin: 0 auto 1rem;
    }

    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }

    .processing-content h4 {
      color: #333;
      margin-bottom: 0.5rem;
      font-size: 1rem;
      font-weight: 600;
    }

    .processing-content p {
      color: #666;
      font-size: 0.8rem;
    }

    .success-icon {
      font-size: 4rem;
      color: #28a745;
      margin-bottom: 1rem;
      animation: scaleIn 0.5s ease;
    }

    @keyframes scaleIn {
      from { transform: scale(0); }
      to { transform: scale(1); }
    }

    .success-content h3 {
      color: #28a745;
      margin-bottom: 0.75rem;
      font-size: 1.25rem;
      font-weight: 600;
    }

    .payment-id {
      font-family: monospace;
      background: #f8f9fa;
      padding: 0.5rem 0.75rem;
      border-radius: 6px;
      margin: 0.75rem 0;
      font-size: 0.8rem;
      color: #666;
    }

    .success-message {
      color: #666;
      margin-bottom: 1rem;
      font-size: 0.85rem;
    }

    ::ng-deep .mat-mdc-tab-labels {
      justify-content: space-around;
    }

    ::ng-deep .mat-mdc-tab {
      min-width: 100px !important;
    }

    ::ng-deep .mat-mdc-tab-group {
      margin: 0 !important;
    }

    ::ng-deep .mat-mdc-tab-body {
      padding: 0 !important;
      margin: 0 !important;
    }

    ::ng-deep .mat-mdc-form-field {
      background: #ffffff;
      border-radius: 8px;
    }

    ::ng-deep .mat-mdc-text-field-wrapper {
      background: #ffffff !important;
    }

    .w-100 {
      width: 100%;
    }

    .text-center {
      text-align: center;
    }

    .text-muted {
      color: #999;
      font-size: 0.875rem;
    }

    .mt-3 {
      margin-top: 1rem;
    }

    .mb-2 {
      margin-bottom: 0.5rem;
    }
  `]
})
export class MockRazorpayDialogComponent {
  // Payment method data
  selectedTabIndex = 0;
  upiId = '';
  cardNumber = '';
  cardholderName = '';
  expiryDate = '';
  cvv = '';
  selectedBank = '';

  // Processing states
  isProcessing = false;
  showSuccess = false;
  processingMessage = '';
  generatedPaymentId = '';

  // Banks data
  popularBanks = ['State Bank of India', 'HDFC Bank', 'ICICI Bank', 'Axis Bank'];
  allBanks = [
    'State Bank of India', 'HDFC Bank', 'ICICI Bank', 'Axis Bank',
    'Punjab National Bank', 'Bank of Baroda', 'Canara Bank', 'Union Bank',
    'Bank of India', 'Indian Bank', 'Central Bank', 'Yes Bank'
  ];

  constructor(
    public dialogRef: MatDialogRef<MockRazorpayDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: MockRazorpayDialogData
  ) {}

  formatCardNumber(): void {
    this.cardNumber = this.cardNumber.replace(/\s/g, '').replace(/(\d{4})/g, '$1 ').trim();
  }

  formatExpiry(): void {
    this.expiryDate = this.expiryDate.replace(/\D/g, '');
    if (this.expiryDate.length >= 2) {
      this.expiryDate = this.expiryDate.slice(0, 2) + '/' + this.expiryDate.slice(2, 4);
    }
  }

  isCardFormValid(): boolean {
    return this.cardNumber.replace(/\s/g, '').length >= 13 &&
           this.cardholderName.length >= 3 &&
           this.expiryDate.length === 5 &&
           this.cvv.length === 3;
  }

  payWithUpiApp(appName: string): void {
    this.processPayment(`UPI (${appName})`, 'Opening ' + appName + '...');
  }

  payWithUpiId(): void {
    if (this.upiId) {
      this.processPayment('UPI', 'Verifying UPI ID...');
    }
  }

  payWithCard(): void {
    if (this.isCardFormValid()) {
      this.processPayment('Card', 'Processing card payment...');
    }
  }

  payWithNetBanking(bank: string): void {
    if (bank) {
      this.processPayment('Net Banking', 'Redirecting to ' + bank + '...');
    }
  }

  payWithWallet(wallet: string): void {
    this.processPayment('Wallet', 'Opening ' + wallet + '...');
  }

  private processPayment(method: string, message: string): void {
    this.isProcessing = true;
    this.processingMessage = message;

    // Simulate payment processing
    setTimeout(() => {
      this.processingMessage = 'Authorizing payment...';
    }, 1000);

    setTimeout(() => {
      this.processingMessage = 'Verifying with bank...';
    }, 2000);

    setTimeout(() => {
      this.processingMessage = 'Payment successful!';
    }, 3000);

    setTimeout(() => {
      this.isProcessing = false;
      this.showSuccess = true;
      this.generatedPaymentId = this.generatePaymentId();
    }, 3500);

    // Auto close after 2 seconds on success page
    setTimeout(() => {
      this.onSuccessClose();
    }, 5500);
  }

  private generatePaymentId(): string {
    const prefix = 'pay_';
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let id = prefix;
    for (let i = 0; i < 14; i++) {
      id += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return id;
  }

  onClose(): void {
    if (!this.isProcessing) {
      this.dialogRef.close(null);
    }
  }

  onSuccessClose(): void {
    const response: PaymentResponse = {
      success: true,
      paymentId: this.generatedPaymentId,
      orderId: this.data.orderId,
      amount: this.data.amount,
      paymentMethod: this.getSelectedPaymentMethod()
    };
    this.dialogRef.close(response);
  }

  private getSelectedPaymentMethod(): string {
    const methods = ['UPI', 'Card', 'Net Banking', 'Wallet'];
    return methods[this.selectedTabIndex] || 'UPI';
  }
}
