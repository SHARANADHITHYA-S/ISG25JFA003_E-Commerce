import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CategoryService } from '../../../../core/services/category.service';
import { CategoryResponseDTO } from '../../../../core/models/category';

@Component({
  selector: 'app-category-filter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './category-filter.component.html',
  styleUrls: ['./category-filter.component.scss']
})
export class CategoryFilterComponent implements OnInit {
  categories: CategoryResponseDTO[] = [];
  selectedCategoryId: number | null = null;

  @Output() categorySelected = new EventEmitter<number | null>();

  constructor(private categoryService: CategoryService, private router: Router) { }

  ngOnInit(): void {
    this.categoryService.getAllCategories().subscribe((data: CategoryResponseDTO[]) => {
      this.categories = data;
    });
  }

  selectCategory(categoryId: number | null): void {
    this.selectedCategoryId = categoryId;
    this.categorySelected.emit(categoryId);
    this.router.navigate(['/products'], { queryParams: { categoryId: categoryId } });
  }
}
