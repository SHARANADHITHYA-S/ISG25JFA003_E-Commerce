import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const errorInterceptor: HttpInterceptorFn = (
    req: HttpRequest<unknown>,
    next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {
    const router = inject(Router);
    const authService = inject(AuthService);

    return next(req).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status === 401) {
                authService.logout();
                router.navigate(['/login']);
            } else if (error.status === 404 && req.url.includes('/cart')) {
                // Suppress the alert for a 404 on the cart endpoint
                return throwError(() => new Error('Cart not found'));
            }

            const errorMessage = error.error?.message || 'An unknown error occurred';
            console.error(errorMessage);
            return throwError(() => new Error(errorMessage));
        })
    );
};
