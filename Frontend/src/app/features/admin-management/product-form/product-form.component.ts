import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Router, ActivatedRoute } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { CategoryService } from '../../../core/services/category.service';
import { ProductRequestDTO, ProductResponseDTO } from '../../../core/models/product';
import { CategoryResponseDTO } from '../../../core/models/category';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.scss']
})
export class ProductFormComponent implements OnInit {
  productForm!: FormGroup;
  categories: CategoryResponseDTO[] = [];
  isEditMode = false;
  productId: number | null = null;
  submitted = false;

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder
  ) { }

  ngOnInit(): void {
    this.initializeForm();
    this.fetchCategories();
    
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.productId = Number(id);
        this.loadProduct(this.productId);
      }
    });
  }

  initializeForm(): void {
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]],
      price: [0, [Validators.required, Validators.min(0.01), Validators.max(999999)]],
      isActive: [true],
      image_url: ['', [Validators.required, Validators.pattern(/^https?:\/\/.+/)]],
      quantity: [0, [Validators.required, Validators.min(0), Validators.max(999999)]],
      categoryId: [0, [Validators.required, Validators.min(1)]]
    });
  }

  get f() {
    return this.productForm.controls;
  }

  fetchCategories(): void {
    this.categoryService.getAllCategories().subscribe((data: CategoryResponseDTO[]) => {
      this.categories = data;
    });
  }

  loadProduct(id: number): void {
    this.productService.getProductById(id).subscribe((data: ProductResponseDTO) => {
      this.productForm.patchValue({
        name: data.name,
        description: data.description,
        price: data.price,
        isActive: data.is_active,
        image_url: data.image_url,
        quantity: data.quantity,
        categoryId: data.category_id
      });
    });
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.productForm.invalid) {
      return;
    }

    const product: ProductRequestDTO = this.productForm.value;

    if (this.isEditMode && this.productId) {
      this.productService.updateProduct(this.productId, product).subscribe(() => {
        this.router.navigate(['/admin/products']);
      });
    } else {
      this.productService.createProduct(product).subscribe(() => {
        this.router.navigate(['/admin/products']);
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/admin/products']);
  }
}