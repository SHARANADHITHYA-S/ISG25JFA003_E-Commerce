import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { User } from '../models/user';
import { StorageService } from './storage.service';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = 'http://localhost:8080/api/auth';

    constructor(private http: HttpClient, private storageService: StorageService) {}

    private decodeToken(token: string): any {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));
            return JSON.parse(jsonPayload);
        } catch (e) {
            console.error("Error decoding token:", e);
            return null;
        }
    }

    login(username: string, password: string): Observable<{ token: string, user: User }> {
        return this.http.post<{ token: string, user: User }>(`${this.apiUrl}/login`, { username, password }).pipe(
            map(response => {
                console.log("Login response:", response);
                this.storageService.setItem('token', response.token);
                console.log("Token stored in localStorage after login:", this.storageService.getItem('token'));

                const decodedToken = this.decodeToken(response.token);
                console.log("Decoded token:", decodedToken);

                if (decodedToken) {
                    const user: User = {
                        id: decodedToken.userId,
                        username: decodedToken.sub, // 'sub' is the subject, which is the username
                        role: decodedToken.role,
                        firstName: '', // Assuming these are not in token
                        lastName: '',
                        createdAt: new Date(),
                        updatedAt: new Date()
                    };
                    this.storageService.setItem('user', user);
                } else {
                    this.storageService.removeItem('user');
                }
                return response;
            })
        );
    }

    register(user: Partial<User>, password: string): Observable<User> {
        return this.http.post<User>(`${this.apiUrl}/register`, { ...user, password });
    }

    logout(): void {
        this.storageService.removeItem('token');
        this.storageService.removeItem('user');
    }

    isLoggedIn(): boolean {
        console.log("Token from localStorage in isLoggedIn:", this.storageService.getItem('token'));
        return !!this.storageService.getItem('token');
    }

    getCurrentUser(): User | null {
        const user = this.storageService.getItem<User>('user');
        console.log("User object from StorageService:", user);
        return user;
    }

    getUserRole(): string | null {
        const token = this.storageService.getItem('token');
        if (token && typeof token === 'string') {
            const decoded = this.decodeToken(token);
            return decoded ? decoded.role : null;
        }
        return null;
    }

    forgotPassword(email: string): Observable<any> {
        return this.http.post(`${this.apiUrl}/forgot-password`, { email });
    }

    resetPassword(token: string, username: string, newPassword: string): Observable<any> {
        return this.http.post(`${this.apiUrl}/reset-password`, { token, username, newPassword }, { responseType: 'text' });
    }
}
