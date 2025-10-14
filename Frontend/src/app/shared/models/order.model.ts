export interface Order {
    id: number;
    status: OrderStatus;
    totalAmount: number;
    placed_at: Date;
    userId: number;
    addressId: number;
    paymentMethodId: number;
    orderItems: OrderItem[];
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

export const ORDER_STATUS_HIERARCHY: Record<OrderStatus, number> = {
    PENDING: 1,
    PROCESSING: 2,
    COMPLETED: 3,
    CANCELLED: 4,
};