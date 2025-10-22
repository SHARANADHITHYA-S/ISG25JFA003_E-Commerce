import { Component } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss'],
  standalone: true,
  imports: [FormsModule, CommonModule]
})
export class ForgotPasswordComponent {
  email = '';
  errorMessage = '';
  successMessage = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) { }

  forgotPassword() {
    if (!this.email) {
      this.errorMessage = 'Email is required';
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.email)) {
      this.errorMessage = 'Please enter a valid email address';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.forgotPassword(this.email).subscribe({
      next: (response: any) => {
        this.successMessage = 'Password reset token has been sent to your email.';
        this.loading = false;
        // Show the message for 3 seconds before navigating
        setTimeout(() => {
          this.router.navigate(['/reset-password'], { queryParams: { token: response.resetToken, username: response.username } });
        }, 3000);
      },
      error: (error: any) => {
        this.errorMessage = error.error?.message || 'Failed to send reset token. Please try again.';
        this.loading = false;
      }
    });
  }
}
