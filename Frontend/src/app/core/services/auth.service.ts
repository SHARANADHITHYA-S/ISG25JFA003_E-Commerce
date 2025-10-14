import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { User } from '../models/user';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = 'http://localhost:8080/api/auth';

    constructor(private http: HttpClient) {}

    login(email: string, password: string): Observable<{ token: string, user: User }> {
        return this.http.post<{ token: string, user: User }>(`${this.apiUrl}/login`, { email, password });
    }

    register(user: Partial<User>, password: string): Observable<User> {
        return this.http.post<User>(`${this.apiUrl}/register`, { ...user, password });
    }

    logout(): void {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    }

    isLoggedIn(): boolean {
        return !!localStorage.getItem('token');
    }

    getCurrentUser(): User | null {
        const userStr = localStorage.getItem('user');
        return userStr ? JSON.parse(userStr) : null;
    }
}
