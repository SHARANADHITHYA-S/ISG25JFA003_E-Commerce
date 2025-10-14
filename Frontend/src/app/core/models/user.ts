export interface User {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
    role: 'USER' | 'ADMIN';
    createdAt: Date;
    updatedAt: Date;
}
