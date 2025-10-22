import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { environment } from '../../../environments/environment';

// Declare Razorpay type for TypeScript
declare var Razorpay: any;

export interface RazorpayOptions {
  key: string;
  amount: number; // Amount in paise (multiply by 100)
  currency: string;
  name: string;
  description: string;
  image?: string;
  order_id?: string;
  prefill?: {
    name?: string;
    email?: string;
    contact?: string;
  };
  notes?: any;
  theme?: {
    color?: string;
  };
  modal?: {
    ondismiss?: () => void;
  };
  handler?: (response: RazorpayPaymentResponse) => void;
}

export interface RazorpayPaymentResponse {
  razorpay_payment_id: string;
  razorpay_order_id?: string;
  razorpay_signature?: string;
}

@Injectable({
  providedIn: 'root'
})
export class RazorpayService {
  // Read key from environment
  private readonly RAZORPAY_KEY = environment.razorpayKey;
  
  constructor() {}

  /**
   * Opens Razorpay checkout
   * @param options Payment options
   * @returns Observable with payment response
   */
  public openCheckout(options: Partial<RazorpayOptions>): Observable<RazorpayPaymentResponse> {
    const subject = new Subject<RazorpayPaymentResponse>();

    // Basic validations and helpful errors
    if (!this.isRazorpayLoaded()) {
      subject.error({ error: 'Razorpay script not loaded. Please check internet connection and ensure checkout.js is present in index.html.' });
      subject.complete();
      return subject.asObservable();
    }

    if (!this.RAZORPAY_KEY) {
      subject.error({ error: 'Razorpay key missing. Set environment.razorpayKey.' });
      subject.complete();
      return subject.asObservable();
    }

    if (this.RAZORPAY_KEY.includes('your_key_here') || this.RAZORPAY_KEY.includes('1DP5mmOlF5G5ag')) {
      subject.error({ error: 'Invalid Razorpay key. Generate your own Test Key in Razorpay Dashboard and set environment.razorpayKey.' });
      subject.complete();
      return subject.asObservable();
    }

    const amountInPaise = options.amount || 0;
    if (!amountInPaise || amountInPaise < 100) {
      subject.error({ error: 'Invalid amount. Minimum payable amount is ₹1 (100 paise).' });
      subject.complete();
      return subject.asObservable();
    }

    const razorpayOptions: RazorpayOptions = {
      key: this.RAZORPAY_KEY,
      amount: amountInPaise,
      currency: options.currency || 'INR',
      name: options.name || 'E-Commerce Store',
      description: options.description || 'Payment for your order',
      image: options.image || '/logo.ico',
      order_id: options.order_id,
      prefill: {
        name: options.prefill?.name || '',
        email: options.prefill?.email || '',
        contact: options.prefill?.contact || ''
      },
      notes: options.notes || {},
      theme: {
        color: options.theme?.color || '#EE7B30'
      },
      modal: {
        ondismiss: () => {
          subject.error({ error: 'Payment cancelled by user' });
          subject.complete();
        }
      },
      handler: (response: RazorpayPaymentResponse) => {
        subject.next(response);
        subject.complete();
      }
    } as RazorpayOptions;

    try {
      const razorpay = new Razorpay(razorpayOptions);
      razorpay.on('payment.failed', (response: any) => {
        subject.error({
          error: 'Payment failed',
          reason: response?.error?.reason,
          description: response?.error?.description
        });
        subject.complete();
      });
      razorpay.open();
    } catch (error) {
      subject.error({ error: 'Failed to initialize Razorpay', details: error });
      subject.complete();
    }

    return subject.asObservable();
  }

  /**
   * Verify if Razorpay is loaded
   */
  public isRazorpayLoaded(): boolean {
    return typeof Razorpay !== 'undefined';
  }

  /**
   * Convert rupees to paise (Razorpay uses paise)
   */
  public convertToPaise(amount: number): number {
    return Math.round(amount * 100);
  }

  /**
   * Convert paise to rupees
   */
  public convertToRupees(paise: number): number {
    return paise / 100;
  }
}
