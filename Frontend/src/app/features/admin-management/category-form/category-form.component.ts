import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CategoryService } from '../../../core/services/category.service';
import { CategoryRequestDTO, CategoryResponseDTO } from '../../../core/models/category';

@Component({
  selector: 'app-category-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
  <div class="container mt-4">
    <h3>{{ isEditMode ? 'Edit Category' : 'Add New Category' }}</h3>
    <form [formGroup]="categoryForm" (ngSubmit)="onSubmit()">
      <div class="mb-3">
        <label for="name" class="form-label">Name <span class="text-danger">*</span></label>
        <input 
          id="name" 
          class="form-control" 
          formControlName="name"
          [class.is-invalid]="submitted && f['name'].errors">
        <div *ngIf="submitted && f['name'].errors" class="invalid-feedback">
          <div *ngIf="f['name'].errors['required']">Name is required</div>
          <div *ngIf="f['name'].errors['minlength']">Name must be at least 3 characters</div>
          <div *ngIf="f['name'].errors['maxlength']">Name cannot exceed 50 characters</div>
        </div>
      </div>
      <div class="mb-3">
        <label for="description" class="form-label">Description <span class="text-danger">*</span></label>
        <textarea 
          id="description" 
          class="form-control" 
          formControlName="description"
          rows="3"
          [class.is-invalid]="submitted && f['description'].errors"></textarea>
        <div *ngIf="submitted && f['description'].errors" class="invalid-feedback">
          <div *ngIf="f['description'].errors['required']">Description is required</div>
          <div *ngIf="f['description'].errors['minlength']">Description must be at least 10 characters</div>
          <div *ngIf="f['description'].errors['maxlength']">Description cannot exceed 500 characters</div>
        </div>
      </div>
      <div class="mb-3">
        <label for="imageUrl" class="form-label">Image URL <span class="text-danger">*</span></label>
        <input 
          id="imageUrl" 
          type="url" 
          class="form-control" 
          formControlName="imageUrl"
          placeholder="https://example.com/image.jpg"
          [class.is-invalid]="submitted && f['imageUrl'].errors">
        <small class="form-text text-muted">Enter the URL of the category image</small>
        <div *ngIf="submitted && f['imageUrl'].errors" class="invalid-feedback">
          <div *ngIf="f['imageUrl'].errors['required']">Image URL is required</div>
          <div *ngIf="f['imageUrl'].errors['pattern']">Please enter a valid URL (must start with http:// or https://)</div>
        </div>
      </div>
      <div class="mb-3" *ngIf="categoryForm.get('imageUrl')?.value">
        <label class="form-label">Image Preview</label>
        <div class="border rounded p-2 text-center bg-light">
          <img [src]="categoryForm.get('imageUrl')?.value" alt="Category preview" class="img-fluid" style="max-height: 200px; object-fit: contain;" 
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
  categoryForm!: FormGroup;
  isEditMode = false;
  categoryId: number | null = null;
  submitted = false;

  constructor(
    private categoryService: CategoryService,
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.categoryId = Number(id);
        this.loadCategory(this.categoryId);
      }
    });
  }

  initializeForm(): void {
    this.categoryForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
      imageUrl: ['', [Validators.required, Validators.pattern(/^https?:\/\/.+/)]]
    });
  }

  get f() {
    return this.categoryForm.controls;
  }

  loadCategory(id: number): void {
    this.categoryService.getCategoryById(id).subscribe((data: CategoryResponseDTO) => {
      this.categoryForm.patchValue({
        name: data.name,
        description: data.description,
        imageUrl: data.imageUrl || ''
      });
    });
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.categoryForm.invalid) {
      return;
    }

    const category: CategoryRequestDTO = this.categoryForm.value;

    if (this.isEditMode && this.categoryId) {
      this.categoryService.updateCategory(this.categoryId, category).subscribe(() => {
        this.router.navigate(['/admin/categories']);
      });
    } else {
      this.categoryService.createCategory(category).subscribe(() => {
        this.router.navigate(['/admin/categories']);
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/admin/categories']);
  }
}


