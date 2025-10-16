import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

// Define an interface for the Product object for type safety
interface Product {
  id: number;
  name: string;
  price: number;
  rating: number;
  image: string;
}

@Component({
  selector: 'app-featured-products',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './featured-products.html'
})
export class FeaturedProductsComponent {
  // An array of product data to display
  products: Product[] = [
    {
      id: 1,
      name: 'Wireless Headphones',
      price: 199.99,
      rating: 4.5,
      image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080',
    },
    {
      id: 2,
      name: 'MacBook Pro',
      price: 1299.99,
      rating: 5,
      image: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080',
    },
    {
      id: 3,
      name: 'Smart Watch',
      price: 299.99,
      rating: 4,
      image: 'https://images.unsplash.com/photo-1546868871-7041f2a55e12?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080',
    },
    {
      id: 4,
      name: 'Professional Camera',
      price: 899.99,
      rating: 4.8,
      image: 'https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080',
    },
  ];

  // Helper function to create an array for the star rating loop
  getStarArray(count: number): number[] {
    return Array(count).fill(0);
  }

  // Helper function to determine how many stars should be filled
  getFullStars(rating: number): number {
    return Math.floor(rating);
  }
}