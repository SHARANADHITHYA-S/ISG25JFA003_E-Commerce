import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { forkJoin, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { OrderService, OrderResponseDTO } from '../../../core/services/order.service';
import { UserService } from '../../../core/services/user.service';

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
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './analytics-orders.component.html',
  styleUrls: ['./analytics-orders.component.scss']
})
export class AnalyticsOrdersComponent implements OnInit {
  orders: OrderAnalytics[] = [];
  loading = false;
  error: string | null = null;

  constructor(
    private orderService: OrderService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.fetchOrderAnalytics();
  }

  fetchOrderAnalytics(): void {
    this.loading = true;
    this.error = null;

    this.orderService.getAllOrdersForAdmin().subscribe({
      next: (orderData: OrderResponseDTO[]) => {
        if (orderData.length === 0) {
          this.orders = [];
          this.loading = false;
          return;
        }

        // Create an array of observables to fetch user data for each order
        const userRequests = orderData.map(order =>
          this.userService.getUserById(order.userId).pipe(
            map(user => ({
              orderId: order.id,
              customerName: user.name || user.email || `User ${order.userId}`,
              orderDate: order.placed_at,
              status: this.formatStatus(order.status),
              totalAmount: order.totalAmount,
              itemsCount: order.orderItems ? order.orderItems.length : 0
            })),
            catchError(() => of({
              orderId: order.id,
              customerName: `User ${order.userId}`, // Fallback if user fetch fails
              orderDate: order.placed_at,
              status: this.formatStatus(order.status),
              totalAmount: order.totalAmount,
              itemsCount: order.orderItems ? order.orderItems.length : 0
            }))
          )
        );

        // Execute all user requests in parallel
        forkJoin(userRequests).subscribe({
          next: (ordersWithUserData) => {
            this.orders = ordersWithUserData;
            this.loading = false;
          },
          error: (err) => {
            console.error('Error fetching user data:', err);
            // Fallback: show orders without user names
            this.orders = orderData.map(order => ({
              orderId: order.id,
              customerName: `User ${order.userId}`,
              orderDate: order.placed_at,
              status: this.formatStatus(order.status),
              totalAmount: order.totalAmount,
              itemsCount: order.orderItems ? order.orderItems.length : 0
            }));
            this.loading = false;
          }
        });
      },
      error: (err) => {
        console.error('Error fetching orders:', err);
        this.error = 'Failed to load order analytics. Please try again.';
        this.loading = false;
      }
    });
  }

  // Format status to match expected display format
  private formatStatus(status: string): string {
    if (!status) return 'Pending';
    
    // Convert PLACED to Placed, SHIPPED to Shipped, etc.
    return status.charAt(0).toUpperCase() + status.slice(1).toLowerCase();
  }

  getStatusClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'shipped': return 'status-shipped';
      case 'placed': return 'status-placed';
      case 'delivered': return 'status-delivered';
      case 'cancelled': return 'status-cancelled';
      case 'paid': return 'status-paid';
      case 'processing': return 'status-processing';
      case 'pending': return 'status-pending';
      default: return 'status-pending';
    }
  }

  getTotalRevenue(): number {
    // Only calculate revenue from orders that are paid, processing, shipped, or delivered
    // Exclude pending and cancelled orders
    const validStatuses = ['paid', 'processing', 'shipped', 'delivered'];
    return this.orders
      .filter(order => validStatuses.includes(order.status.toLowerCase()))
      .reduce((total, order) => total + order.totalAmount, 0);
  }

  getOrderCountByStatus(status: string): number {
    return this.orders.filter(order => order.status.toLowerCase() === status.toLowerCase()).length;
  }

  getTotalItems(): number {
    return this.orders.reduce((total, order) => total + order.itemsCount, 0);
  }

  // Get available status options for dropdown
  getStatusOptions(): string[] {
    return ['Pending', 'Paid', 'Processing', 'Shipped', 'Delivered'];
  }

  // Check if order can be updated (not cancelled)
  canUpdateStatus(status: string): boolean {
    return status.toLowerCase() !== 'cancelled';
  }

  // Update order status
  onStatusChange(orderId: number, newStatus: string): void {
    const statusUpper = newStatus.toUpperCase();
    
    this.orderService.updateOrderStatus(orderId, statusUpper).subscribe({
      next: () => {
        // Update the local order status
        const order = this.orders.find(o => o.orderId === orderId);
        if (order) {
          order.status = newStatus;
        }
      },
      error: (err) => {
        console.error('Error updating order status:', err);
        // Refresh data to revert any UI changes
        this.fetchOrderAnalytics();
      }
    });
  }
}
