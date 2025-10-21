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

  constructor(private authService: AuthService, private router: Router, private route: ActivatedRoute) { }

  login() {
    this.authService.login(this.username, this.password).subscribe(() => {
      const userRole = this.authService.getUserRole();
      if (userRole === 'ROLE_ADMIN') {
        this.router.navigate(['/admin/products']);
      } else {
        const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/products';
        this.router.navigate([returnUrl]);
      }
    });
  }
}
