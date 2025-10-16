import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { inject } from '@angular/core';
import { StorageService } from '../services/storage.service';
import { Router } from '@angular/router'; // Import Router
import { AuthService } from '../services/auth.service'; // Import AuthService

export const tokenInterceptor: HttpInterceptorFn = (
    req: HttpRequest<unknown>,
    next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {
    const storageService = inject(StorageService);
    const router = inject(Router); // Inject Router
    const authService = inject(AuthService); // Inject AuthService

    const token = storageService.getItem<string>('token');

    if (token) {
        req = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    return next(req).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status === 401 || error.status === 403) {
                // Token expired or unauthorized, redirect to login
                authService.logout(); // Clear any invalid token
                router.navigate(['/auth/login']);
            }
            return throwError(() => error);
        })
    );
};
