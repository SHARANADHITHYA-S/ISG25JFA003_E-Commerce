export interface Payment {
  id?: number;
  paymentId?: number;
  orderId: number;
  amount: number;
  status?: string;
  paymentStatus?: PaymentStatus;
  paymentType?: PaymentType;
  transactionId?: string;
  transaction_reference?: string;
  paymentDate?: string;
  createdAt?: string;
  created_at?: string;
}
 
export enum PaymentType {
  CREDIT_CARD = 'CREDIT_CARD',
  DEBIT_CARD = 'DEBIT_CARD',
  UPI = 'UPI'
}
 
export enum PaymentStatus {
  PENDING = 'PENDING',
  SUCCESS = 'SUCCESS',
  FAILED = 'FAILED',
  COMPLETED = 'COMPLETED'
}
 
export interface ValidatePaymentRequest {
  orderId: number;
  paymentType: PaymentType;
  paymentDetails: CreditCardPayment | DebitCardPayment | UpiPayment;
}
 
export interface CreditCardPayment {
  cardNumber: string;
  cardHolderName: string;
  expiryDate: string;
  cvv: string;
  pin: string;
}
 
export interface DebitCardPayment {
  cardNumber: string;
  cardHolderName: string;
  expiryDate: string;
  cvv: string;
  pin: string;
}
 
export interface UpiPayment {
  upiId: string;
  pin: string;
}