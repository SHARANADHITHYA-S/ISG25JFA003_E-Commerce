export interface Cart {
    id: number;
    userId: number;
    items: CartItem[];
    total: number;
    createdAt: Date;
    updatedAt: Date;
}

export interface CartItem {
    id: number;
    productId: number;
    productName: string;
    quantity: number;
    price: number;
    subtotal: number;
}
