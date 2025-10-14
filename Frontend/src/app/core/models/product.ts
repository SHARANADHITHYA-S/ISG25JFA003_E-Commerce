export interface Product {
    id: number;
    name: string;
    description: string;
    price: number;
    stockQuantity: number;
    categoryId: number;
    imageUrl?: string;
    createdAt: Date;
    updatedAt: Date;
}
