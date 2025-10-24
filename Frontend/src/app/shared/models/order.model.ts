import { ProductResponseDTO } from '../../core/models/product';

export interface Order {
    id: number;
    status: OrderStatus;
    totalAmount: number;
    placed_at: Date;
    userId: number;
    addressId: number;
    paymentMethodId: number;
    orderItems: OrderItem[];
    deliveryDate?: string;
}

export interface OrderItem {
    id: number;
    productId: number;
    productName: string;
    quantity: number;
    price: number;
    subtotal: number;
    image_url: string;
    product: ProductResponseDTO; // Add product details
}

export type OrderStatus = 'PENDING' | 'PAID' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'COMPLETED' | 'CANCELLED';

export const ORDER_STATUS_HIERARCHY: Record<OrderStatus, number> = {
    PENDING: 1,
    PAID: 2,
    PROCESSING: 3,
    SHIPPED: 4,
    DELIVERED: 5,
    COMPLETED: 6,
    CANCELLED: 7,
};
