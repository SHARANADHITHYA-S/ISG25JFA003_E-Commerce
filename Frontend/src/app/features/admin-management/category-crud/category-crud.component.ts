import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CategoryService } from '../../../core/services/category.service';
import { CategoryResponseDTO } from '../../../core/models/category';

@Component({
  selector: 'app-category-crud',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './category-crud.component.html',
  styleUrls: ['./category-crud.component.scss']
})
export class CategoryCrudComponent implements OnInit {
  categories: CategoryResponseDTO[] = [];

  constructor(private categoryService: CategoryService) { }

  ngOnInit(): void {
    this.fetchCategories();
  }

  fetchCategories(): void {
    this.categoryService.getAllCategories().subscribe((data: CategoryResponseDTO[]) => {
      this.categories = data;
    });
  }

  onDelete(id: number): void {
    if (confirm('Are you sure you want to delete this category? All related products will also be deleted.')) {
      this.categoryService.deleteCategory(id).subscribe(() => {
        this.categories = this.categories.filter(c => c.id !== id);
      });
    }
  }
}