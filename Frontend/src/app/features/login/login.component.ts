import { Component } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  imports: [FormsModule, CommonModule]
})
export class LoginComponent {
  username = '';
  password = '';
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router, private route: ActivatedRoute) { }

  login() {
    this.errorMessage = '';
    this.authService.login(this.username, this.password).subscribe({
      next: () => {
        const userRole = this.authService.getUserRole();
        if (userRole === 'ROLE_ADMIN') {
          this.router.navigate(['/admin/products']);
        } else {
          const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/products';
          this.router.navigate([returnUrl]);
        }
      },
      error: (error) => {
        console.log('Login error:', error);
        this.errorMessage = error.error?.error || error.error || 'Invalid Username or Password.';
      }
    });
  }
}
