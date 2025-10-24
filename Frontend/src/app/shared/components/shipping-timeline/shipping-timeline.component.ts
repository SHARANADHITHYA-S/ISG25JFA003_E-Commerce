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
    const currentIndex = this.timelineEvents.findIndex(e => e.isCurrent);
    const eventIndex = this.timelineEvents.indexOf(event);
    if (eventIndex <= currentIndex) {
      return 'paid';
    } else if (eventIndex === currentIndex + 1) {
      return 'processing';
    } else {
      return 'pending';
    }
  }
}
