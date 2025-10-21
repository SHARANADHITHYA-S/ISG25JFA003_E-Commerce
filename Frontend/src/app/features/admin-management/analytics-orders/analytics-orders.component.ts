import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { AnalyticsService } from '../../../core/services/analytics.service';

interface OrderAnalytics {
  orderId: number;
  customerName: string;
  orderDate: string;
  status: string;
  totalAmount: number;
  itemsCount: number;
}

@Component({
  selector: 'app-analytics-orders',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  templateUrl: './analytics-orders.component.html',
  styleUrls: ['./analytics-orders.component.scss']
})
export class AnalyticsOrdersComponent implements OnInit {
  orders: OrderAnalytics[] = [];
  loading = false;
  error: string | null = null;

  constructor(private analyticsService: AnalyticsService) {}

  ngOnInit(): void {
    this.fetchOrderAnalytics();
  }

  fetchOrderAnalytics(): void {
    this.loading = true;
    this.error = null;

    // For now, using mock data since backend doesn't have this endpoint yet
    // In real implementation, this would call this.analyticsService.getOrderAnalytics()
    setTimeout(() => {
      this.orders = [
        {
          orderId: 1001,
          customerName: 'John Doe',
          orderDate: '2024-01-15',
          status: 'Shipped',
          totalAmount: 299.99,
          itemsCount: 2
        },
        {
          orderId: 1002,
          customerName: 'Jane Smith',
          orderDate: '2024-01-14',
          status: 'Placed',
          totalAmount: 149.99,
          itemsCount: 1
        },
        {
          orderId: 1003,
          customerName: 'Bob Johnson',
          orderDate: '2024-01-13',
          status: 'Shipped',
          totalAmount: 459.99,
          itemsCount: 3
        },
        {
          orderId: 1004,
          customerName: 'Alice Brown',
          orderDate: '2024-01-12',
          status: 'Placed',
          totalAmount: 89.99,
          itemsCount: 1
        }
      ];
      this.loading = false;
    }, 1000);
  }

  getStatusClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'shipped': return 'status-shipped';
      case 'placed': return 'status-placed';
      case 'delivered': return 'status-delivered';
      case 'cancelled': return 'status-cancelled';
      default: return 'status-pending';
    }
  }

  getTotalRevenue(): number {
    return this.orders.reduce((total, order) => total + order.totalAmount, 0);
  }

  getOrderCountByStatus(status: string): number {
    return this.orders.filter(order => order.status.toLowerCase() === status.toLowerCase()).length;
  }

  getTotalItems(): number {
    return this.orders.reduce((total, order) => total + order.itemsCount, 0);
  }
}
