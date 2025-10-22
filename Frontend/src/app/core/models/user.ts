export interface User {
    id: number;
    name: string; // Changed from username to name
    email: string;
    role: 'USER' | 'ADMIN' | 'ROLE_ADMIN';
    created_at: string; // Changed from createdAt: Date to created_at: string
    updated_at: string; // Changed from updatedAt: Date to updated_at: string
}
