import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../../core/services/product.service';
import { CategoryService } from '../../../core/services/category.service';
import { ProductResponseDTO } from '../../../core/models/product';
import { CategoryResponseDTO } from '../../../core/models/category';

@Component({
  selector: 'app-product-crud',
  templateUrl: './product-crud.component.html',
  styleUrls: ['./product-crud.component.scss']
})
export class ProductCrudComponent implements OnInit {
  products: ProductResponseDTO[] = [];
  categories: CategoryResponseDTO[] = [];
  selectedCategoryId: number | null = null;

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService
  ) { }

  ngOnInit(): void {
    this.fetchProducts();
    this.fetchCategories();
  }

  fetchProducts(): void {
    this.productService.getAllProducts().subscribe((data: ProductResponseDTO[]) => {
      this.products = data;
    });
  }

  fetchCategories(): void {
    this.categoryService.getAllCategories().subscribe((data: CategoryResponseDTO[]) => {
      this.categories = data;
    });
  }

  filterProductsByCategory(): void {
    if (this.selectedCategoryId) {
      this.productService.getProductsByCategoryId(this.selectedCategoryId).subscribe((data: ProductResponseDTO[]) => {
        this.products = data;
      });
    } else {
      this.fetchProducts();
    }
  }

  onDelete(id: number): void {
    if (confirm('Are you sure you want to delete this product?')) {
      this.productService.deleteProduct(id).subscribe(() => {
        this.fetchProducts(); // Refresh the list after successful deletion
      });
    }
  }
}