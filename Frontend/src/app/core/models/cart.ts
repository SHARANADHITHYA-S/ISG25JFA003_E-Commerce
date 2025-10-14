export interface CartItemRequest {
  productId: number;
  quantity: number;
}

export interface CartItemResponse {
  id: number;
  userId: number;
  cartId: number;
  productId: number;
  productName: string;
  price: number;
  quantity: number;
  totalPrice: number;
}

export interface CartResponse {
  id: number;
  userId: number;
  createdAt: string;
  updatedAt: string;
  totalPrice: number;
  items: CartItemResponse[];
}