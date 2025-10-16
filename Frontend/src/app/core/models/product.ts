export interface ProductResponseDTO {
  id: number;
  name: string;
  description: string;
  price: number;
  image_url: string;
  is_active: boolean;
  created_at: Date;
  updated_at: Date;
  category_id: number;
  quantity: number;
}

export interface ProductRequestDTO {
  name: string;
  description: string;
  price: number;
  isActive: boolean;
  image_url: string;
  quantity: number;
  categoryId: number;
}