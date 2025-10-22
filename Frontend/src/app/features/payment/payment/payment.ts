import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { PaymentService } from '../../../core/services/payment.service';
import { AuthService } from '../../../core/services/auth.service';
import { PaymentType } from '../../../core/models/payment.model';
import { environment } from '../../../../environments/environment';
import { ToastNotificationComponent } from '../../../shared/components/toast-notification/toast-notification';
 
declare var Razorpay: any;
 
@Component({
  selector: 'app-payment-page',
  standalone: true,
  imports: [CommonModule, RouterModule, ToastNotificationComponent],
  templateUrl: './payment.html',
  styleUrls: ['./payment.scss']
})
export class PaymentPageComponent implements OnInit {
  @ViewChild(ToastNotificationComponent) toast!: ToastNotificationComponent;
 
  orderId: number | null = null;
  orderAmount: number = 0;
  loading = false;
 
  constructor(
    private paymentService: PaymentService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}
 
  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.orderId = +params['orderId'];
      this.orderAmount = +params['amount'] || 100; // Default amount if not provided
      if (!this.orderId) {
        this.router.navigate(['/user/orders']);
      }
    });
 
    // Check Razorpay availability after component loads
    setTimeout(() => {
      this.checkRazorpayAvailability();
    }, 1000);
  }
 
  checkRazorpayAvailability(): void {
    if (typeof Razorpay === 'undefined') {
      this.toast?.show('error', 'Razorpay SDK not loaded. Please refresh the page.');
      console.error('Razorpay SDK not available');
    } else {
      console.log('Razorpay SDK loaded successfully');
    }
  }
 
  initiateRazorpayPayment(): void {
    if (!this.orderId) return;
 
    // Check if Razorpay is loaded
    if (typeof Razorpay === 'undefined') {
      this.toast?.show('error', 'Payment gateway not loaded. Please refresh the page.');
      return;
    }
 
    const user = this.authService.getCurrentUser();
   
    const options = {
      key: environment.razorpayKey,
      amount: Math.round(this.orderAmount * 100), // Convert to paise (INR)
      currency: 'INR',
      name: 'Digital Book Store',
      description: `Order #${this.orderId}`,
      image: '/assets/logo.png',
      handler: (response: any) => {
        console.log('Razorpay success:', response);
        this.handlePaymentSuccess(response);
      },
      prefill: {
        name: user?.name || 'Test User',
        //email: user?.email || 'test@example.com',
        contact: '9999999999'
      },
      theme: {
        color: '#111827'
      },
      modal: {
        ondismiss: () => {
          this.toast?.show('info', 'Payment cancelled');
        }
      },
      notes: {
        order_id: this.orderId!.toString()
      }
    };
 
    try {
      const rzp = new Razorpay(options);
      rzp.on('payment.failed', (response: any) => {
        console.error('Razorpay payment failed:', response);
        this.toast?.show('error', 'Payment failed: ' + (response.error.description || 'Unknown error'));
      });
      rzp.open();
    } catch (error) {
      console.error('Razorpay initialization error:', error);
      this.toast?.show('error', 'Failed to initialize payment gateway');
    }
  }
 
  handlePaymentSuccess(response: any): void {
    this.loading = true;
 
    const paymentDetails = {
      cardNumber: '1234567890123456',
      cardHolderName: 'Test User',
      expiryDate: '12/25',
      cvv: '123',
      pin: '1234'
    };
 
    const request = {
      orderId: this.orderId!,
      paymentType: PaymentType.CREDIT_CARD,
      paymentDetails
    };
 
    const userId = this.authService.getCurrentUser()?.id!;
 
    this.paymentService.validatePayment(request, userId).subscribe({
      next: () => {
        this.loading = false;
        this.toast?.show('success', 'Payment successful!');
        setTimeout(() => {
          this.router.navigate(['/user/order-confirmation'], {
            queryParams: { orderId: this.orderId }
          });
        }, 1000);
      },
      error: (error) => {
        this.loading = false;
        this.toast?.show('error', error.message || 'Payment failed');
      }
    });
  }
}