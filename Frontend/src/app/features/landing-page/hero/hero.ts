import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-hero',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './hero.html'
})
export class HeroComponent {
  // URL for the main hero section image
  readonly heroImageUrl = 'https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080';

  private router = inject(Router);

  navigateToProducts(): void {
    this.router.navigate(['/products']);
  }

  navigateToDeals(): void {
    // Assuming 'deals' would also navigate to products, possibly with a filter
    this.router.navigate(['/products']);
  }
}