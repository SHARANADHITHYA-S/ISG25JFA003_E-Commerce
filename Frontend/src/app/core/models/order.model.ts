export interface Order {
    id: number;
    userId: number;
    orderDate: Date;
    createdAt: Date;
    updatedAt: Date;
    status: string;
    total: number;
    customerId?: number;
    items: OrderItem[];
}

export interface OrderItem {
    id: number;
    productId: number;
    productName: string;
    quantity: number;
    price: number;
    subtotal: number;
}
