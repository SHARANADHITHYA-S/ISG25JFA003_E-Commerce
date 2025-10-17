import { Component, EventEmitter, Output } from '@angular/core';
import { CategoryFilterComponent } from '../category-filter/category-filter.component';

@Component({
  selector: 'app-category-filter-container',
  standalone: true,
  imports: [CategoryFilterComponent],
  template: '<app-category-filter (categorySelected)="onCategorySelected($event)"></app-category-filter>',
})
export class CategoryFilterContainerComponent {
  @Output() categorySelected = new EventEmitter<number | null>();

  onCategorySelected(categoryId: number | null): void {
    this.categorySelected.emit(categoryId);
  }
}