import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CategoryService } from '../../../core/services/category.service';
import { CategoryRequestDTO, CategoryResponseDTO } from '../../../core/models/category';

@Component({
  selector: 'app-category-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
  <div class="container mt-4">
    <h3>{{ isEditMode ? 'Edit Category' : 'Add New Category' }}</h3>
    <form (ngSubmit)="onSubmit()">
      <div class="mb-3">
        <label for="name" class="form-label">Name</label>
        <input id="name" class="form-control" [(ngModel)]="category.name" name="name" required>
      </div>
      <div class="mb-3">
        <label for="description" class="form-label">Description</label>
        <textarea id="description" class="form-control" [(ngModel)]="category.description" name="description" required></textarea>
      </div>
      <button type="submit" class="btn btn-success">{{ isEditMode ? 'Update' : 'Create' }}</button>
    </form>
  </div>
  `
})
export class CategoryFormComponent implements OnInit {
  category: CategoryRequestDTO = { name: '', description: '' };
  isEditMode = false;
  categoryId: number | null = null;

  constructor(
    private categoryService: CategoryService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.categoryId = Number(id);
        this.loadCategory(this.categoryId);
      }
    });
  }

  loadCategory(id: number): void {
    this.categoryService.getCategoryById(id).subscribe((data: CategoryResponseDTO) => {
      this.category = {
        name: data.name,
        description: data.description
      };
    });
  }

  onSubmit(): void {
    if (this.isEditMode && this.categoryId) {
      this.categoryService.updateCategory(this.categoryId, this.category).subscribe(() => {
        this.router.navigate(['/admin/categories']);
      });
    } else {
      this.categoryService.createCategory(this.category).subscribe(() => {
        this.router.navigate(['/admin/categories']);
      });
    }
  }
}


