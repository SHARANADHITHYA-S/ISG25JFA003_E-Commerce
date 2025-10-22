export interface User {
    id: number;
    username: string;
    firstName: string;
    lastName: string;
    role: 'USER' | 'ADMIN' | 'ROLE_ADMIN';
    createdAt: Date;
    updatedAt: Date;
}
