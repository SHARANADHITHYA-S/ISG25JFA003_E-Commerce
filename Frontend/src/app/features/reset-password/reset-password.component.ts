import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss'],
  standalone: true,
  imports: [FormsModule, CommonModule]
})
export class ResetPasswordComponent implements OnInit {
  token = '';
  username = '';
  newPassword = '';
  confirmPassword = '';
  errorMessage = '';
  successMessage = '';
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'] || '';
      this.username = params['username'] || '';
      // The backend now returns email as username, so we can use it directly
    });
  }

  resetPassword() {
    if (!this.token || !this.username) {
      this.errorMessage = 'Invalid reset link. Please request a new password reset.';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match';
      return;
    }

    if (!this.newPassword || this.newPassword.length < 6) {
      this.errorMessage = 'Password must be at least 6 characters long';
      return;
    }

    if (this.newPassword.includes(' ')) {
      this.errorMessage = 'Password must not contain spaces';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.resetPassword(this.token, this.username, this.newPassword).subscribe({
      next: (response: any) => {
        this.successMessage = response; // Backend returns plain text message
        this.loading = false;
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      },
      error: (error: any) => {
        this.errorMessage = error.error?.message || 'Failed to reset password. Please try again.';
        this.loading = false;
      }
    });
  }
}
