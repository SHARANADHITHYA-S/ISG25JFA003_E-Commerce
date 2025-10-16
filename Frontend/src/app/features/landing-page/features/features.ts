import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

// Define an interface for the feature object for type safety
interface Feature {
  icon: string;
  title: string;
  description: string;
}

@Component({
  selector: 'app-features',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './features.html'
})
export class FeaturesComponent {
  // Array of feature objects to be displayed in the template
  features: Feature[] = [
    {
      icon: '🚚',
      title: 'Free Shipping',
      description: 'Free delivery on all orders over $50',
    },
    {
      icon: '🔒',
      title: 'Secure Payment',
      description: '100% secure and encrypted transactions',
    },
    {
      icon: '↩️',
      title: 'Easy Returns',
      description: 'Simple 30-day return and exchange policy',
    },
    {
      icon: '🎧',
      title: '24/7 Support',
      description: 'Dedicated customer service ready to help',
    },
  ];
}