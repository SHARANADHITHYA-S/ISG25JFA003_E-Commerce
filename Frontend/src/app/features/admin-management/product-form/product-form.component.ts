import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Router, ActivatedRoute } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { CategoryService } from '../../../core/services/category.service';
import { ProductRequestDTO, ProductResponseDTO } from '../../../core/models/product';
import { CategoryResponseDTO } from '../../../core/models/category';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.scss']
})
export class ProductFormComponent implements OnInit {
  product: ProductRequestDTO = {
    name: '',
    description: '',
    price: 0,
    isActive: true, // Default to active for new products
    image_url: '',
    quantity: 0,
    categoryId: 0 // Will be selected from the dropdown
  };
  categories: CategoryResponseDTO[] = [];
  isEditMode = false;
  productId: number | null = null;

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
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

  fetchCategories(): void {
    this.categoryService.getAllCategories().subscribe((data: CategoryResponseDTO[]) => {
      this.categories = data;
    });
  }

  loadProduct(id: number): void {
    this.productService.getProductById(id).subscribe((data: ProductResponseDTO) => {
      // Map the response DTO to the request DTO format for the form
      this.product = {
        name: data.name,
        description: data.description,
        price: data.price,
        isActive: data.is_active,
        image_url: data.image_url,
        quantity: data.quantity,
        categoryId: data.category_id
      };
    });
  }

  onSubmit(): void {
    if (this.isEditMode && this.productId) {
      this.productService.updateProduct(this.productId, this.product).subscribe(() => {
        this.router.navigate(['/admin/products']);
      });
    } else {
      this.productService.createProduct(this.product).subscribe(() => {
        this.router.navigate(['/admin/products']);
      });
    }
  }
}