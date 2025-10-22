import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { AnalyticsService } from '../../../core/services/analytics.service';

interface ProductAnalytics {
  productId: number;
  productName: string;
  stockQuantity: number;
  soldQuantity: number;
  category: string;
  price: number;
}

@Component({
  selector: 'app-analytics-products',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  templateUrl: './analytics-products.component.html',
  styleUrls: ['./analytics-products.component.scss']
})
export class AnalyticsProductsComponent implements OnInit {
  products: ProductAnalytics[] = [];
  loading = false;
  error: string | null = null;

  constructor(private analyticsService: AnalyticsService) {}

  ngOnInit(): void {
    this.fetchProductAnalytics();
  }

  fetchProductAnalytics(): void {
    this.loading = true;
    this.error = null;

    // For now, using mock data since backend doesn't have this endpoint yet
    // In real implementation, this would call this.analyticsService.getProductAnalytics()
    setTimeout(() => {
      this.products = [
        {
          productId: 1,
          productName: 'Wireless Headphones',
          stockQuantity: 45,
          soldQuantity: 23,
          category: 'Electronics',
          price: 199.99
        },
        {
          productId: 2,
          productName: 'Smart Watch',
          stockQuantity: 12,
          soldQuantity: 8,
          category: 'Electronics',
          price: 299.99
        },
        {
          productId: 3,
          productName: 'Running Shoes',
          stockQuantity: 78,
          soldQuantity: 45,
          category: 'Sports',
          price: 129.99
        },
        {
          productId: 4,
          productName: 'Coffee Maker',
          stockQuantity: 25,
          soldQuantity: 12,
          category: 'Home & Kitchen',
          price: 89.99
        }
      ];
      this.loading = false;
    }, 1000);
  }

  getStockStatus(quantity: number): string {
    if (quantity === 0) return 'Out of Stock';
    if (quantity <= 10) return 'Low Stock';
    return 'In Stock';
  }

  getStockStatusClass(quantity: number): string {
    if (quantity === 0) return 'status-out';
    if (quantity <= 10) return 'status-low';
    return 'status-good';
  }

  getTotalStock(): number {
    return this.products.reduce((total, product) => total + product.stockQuantity, 0);
  }

  getTotalSold(): number {
    return this.products.reduce((total, product) => total + product.soldQuantity, 0);
  }

  getLowStockCount(): number {
    return this.products.filter(product => product.stockQuantity <= 10).length;
  }
}
