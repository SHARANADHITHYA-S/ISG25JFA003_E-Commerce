import { ProductResponseDTO } from './product';

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
  image_url: string;
  product: ProductResponseDTO; // Add product details
}

export interface CartResponse {
  id: number;
  userId: number;
  createdAt: string;
  updatedAt: string;
  totalPrice: number;
  items: CartItemResponse[];
}
