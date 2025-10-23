import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface TimelineEvent {
  status: string;
  date: string;
  isCurrent: boolean;
  isCompleted: boolean;
}

@Component({
  selector: 'app-shipping-timeline',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './shipping-timeline.component.html',
  styleUrls: ['./shipping-timeline.component.scss']
})
export class ShippingTimelineComponent {
  @Input() timelineEvents: TimelineEvent[] = [];

  getMarkerClass(event: TimelineEvent): string {
    if (event.status === 'PAID') {
      return 'paid';
    } else if (event.status === 'PROCESSING') {
      return 'processing';
    } else if (event.isCompleted) {
      return 'completed';
    } else if (event.isCurrent) {
      return 'current';
    } else {
      return 'pending';
    }
  }
}
