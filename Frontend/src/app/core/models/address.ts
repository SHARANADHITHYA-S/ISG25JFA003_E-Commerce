export interface Address {
    id: number;
    userId: number;
    street: string;
    city: string;
    state: string;
    zipCode: string;
    country: string;
    isDefault: boolean;
    createdAt: Date;
    updatedAt: Date;
}
