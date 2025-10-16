import { ProductResponseDTO } from './product';

export interface CategoryResponseDTO {
  id: number;
  name: string;
  description: string;
  products: ProductResponseDTO[];
}

export interface CategoryRequestDTO {
  name: string;
  description: string;
}