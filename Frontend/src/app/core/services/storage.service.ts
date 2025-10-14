import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class StorageService {
    private prefix = 'ecommerce_';

    setItem(key: string, value: any): void {
        localStorage.setItem(this.prefix + key, JSON.stringify(value));
    }

    getItem<T>(key: string): T | null {
        const item = localStorage.getItem(this.prefix + key);
        return item ? JSON.parse(item) : null;
    }

    removeItem(key: string): void {
        localStorage.removeItem(this.prefix + key);
    }

    clear(): void {
        localStorage.clear();
    }

    setSessionItem(key: string, value: any): void {
        sessionStorage.setItem(this.prefix + key, JSON.stringify(value));
    }

    getSessionItem<T>(key: string): T | null {
        const item = sessionStorage.getItem(this.prefix + key);
        return item ? JSON.parse(item) : null;
    }

    removeSessionItem(key: string): void {
        sessionStorage.removeItem(this.prefix + key);
    }

    clearSession(): void {
        sessionStorage.clear();
    }
}
