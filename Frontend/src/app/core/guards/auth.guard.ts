import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {
    constructor(private authService: AuthService, private router: Router) {}

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
        if (!this.authService.isLoggedIn()) {
            this.router.navigate(['/login'], {
                queryParams: { returnUrl: state.url }
            });
            return false;
        }

        // Check for admin routes
        if (state.url.startsWith('/admin')) {
            const role = this.authService.getUserRole();
            if (role !== 'ROLE_ADMIN') {
                this.router.navigate(['/']);
                return false;
            }
        }

        return true;
    }
}
