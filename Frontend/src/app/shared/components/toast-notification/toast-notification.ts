import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
 
export interface Toast {
  id: number;
  type: 'success' | 'error' | 'info' | 'warning';
  message: string;
}
 
@Component({
  selector: 'app-toast-notification',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast-notification.html'
})
export class ToastNotificationComponent {
  toasts: Toast[] = [];
  private nextId = 1;
 
  show(type: 'success' | 'error' | 'info' | 'warning', message: string, duration = 3000): void {
    const toast: Toast = {
      id: this.nextId++,
      type,
      message
    };
 
    this.toasts.push(toast);
 
    setTimeout(() => {
      this.remove(toast.id);
    }, duration);
  }
 
  remove(id: number): void {
    this.toasts = this.toasts.filter(t => t.id !== id);
  }
 
  getIcon(type: string): string {
    switch(type) {
      case 'success': return 'check_circle';
      case 'error': return 'error';
      case 'warning': return 'warning';
      case 'info': return 'info';
      default: return 'info';
    }
  }
 
  getColorClass(type: string): string {
    switch(type) {
      case 'success': return 'bg-green-500';
      case 'error': return 'bg-red-500';
      case 'warning': return 'bg-yellow-500';
      case 'info': return 'bg-blue-500';
      default: return 'bg-gray-500';
    }
  }
}