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
      <div class="mb-3">
        <label for="imageUrl" class="form-label">Image URL</label>
        <input id="imageUrl" type="url" class="form-control" [(ngModel)]="category.imageUrl" name="imageUrl" placeholder="https://example.com/image.jpg">
        <small class="form-text text-muted">Enter the URL of the category image</small>
      </div>
      <div class="mb-3" *ngIf="category.imageUrl">
        <label class="form-label">Image Preview</label>
        <div class="border rounded p-2 text-center bg-light">
          <img [src]="category.imageUrl" [alt]="category.name" class="img-fluid" style="max-height: 200px; object-fit: contain;" 
               onerror="this.style.display='none'; this.nextElementSibling.style.display='block';">
          <div style="display: none;" class="text-danger">
            <i class="bi bi-exclamation-triangle"></i> Invalid image URL
          </div>
        </div>
      </div>
      <button type="submit" class="btn btn-success">{{ isEditMode ? 'Update' : 'Create' }}</button>
      <button type="button" class="btn btn-secondary ms-2" (click)="onCancel()">Cancel</button>
    </form>
  </div>
  `
})
export class CategoryFormComponent implements OnInit {
  category: CategoryRequestDTO = { name: '', description: '', imageUrl: '' };
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
        description: data.description,
        imageUrl: data.imageUrl || ''
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

  onCancel(): void {
    this.router.navigate(['/admin/categories']);
  }
}


