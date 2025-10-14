export interface Order {
    id: number;
    userId: number;
    orderDate: Date;
    createdAt: Date;
    updatedAt: Date;
    status: OrderStatus;
    total: number;
    customerId?: number;
    items: OrderItem[];
    shippingAddressId: number;
    paymentMethodId: number;
}

export interface OrderItem {
    id: number;
    productId: number;
    productName: string;
    quantity: number;
    price: number;
    subtotal: number;
}

export type OrderStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'CANCELLED';
