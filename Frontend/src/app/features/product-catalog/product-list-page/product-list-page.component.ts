import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CategoryFilterComponent } from '../components/category-filter/category-filter.component';
import { ProductCardComponent } from '../components/product-card/product-card.component';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { ProductResponseDTO } from '../../../core/models/product';
import { NgbCarouselModule } from '@ng-bootstrap/ng-bootstrap';
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'app-product-list-page',
  standalone: true,
  imports: [CommonModule, RouterModule, CategoryFilterComponent, ProductCardComponent, NgbCarouselModule, ToastModule],
  templateUrl: './product-list-page.component.html',
  styleUrls: ['./product-list-page.component.scss']
})
export class ProductListPageComponent implements OnInit {
  products: ProductResponseDTO[] = [];
  productImages: string[] = [];
  currentCategoryId: number | null = null;
  loading = true;

  constructor(
    private productService: ProductService,
    private route: ActivatedRoute // Used to read URL parameters (category ID)
  ) { }

  ngOnInit(): void {
    // Subscribe to route changes to handle filtering when the category ID changes
    this.route.queryParamMap.subscribe(params => {
      const categoryIdParam = params.get('categoryId');
      this.currentCategoryId = categoryIdParam ? Number(categoryIdParam) : null;
      this.loadProducts();
    });
  }

  loadProducts(): void {
    this.loading = true;
    if (this.currentCategoryId) {
      // Load products by category ID
      this.productService.getProductsByCategoryId(this.currentCategoryId).subscribe({
        next: (data: ProductResponseDTO[]) => {
          this.products = data.filter(product => product.is_active);
          this.productImages = this.products.map(p => p.image_url);
          this.loading = false;
        },
        error: () => this.loading = false
      });
    } else {
      // Load all active products
      this.productService.getAllProducts().subscribe({
        next: (data: ProductResponseDTO[]) => {
          this.products = data.filter(product => product.is_active);
          this.productImages = this.products.map(p => p.image_url);
          this.loading = false;
        },
        error: () => this.loading = false
      });
    }
  }
  
  // This function is called by the CategoryFilterComponent when a category is selected
  onCategorySelected(categoryId: number | null): void {
      // Navigate using the Router to update the URL and trigger ngOnInit reload
      // Note: We use the Router here, but for this simplified component structure, 
      // we'll just call loadProducts directly to keep the dependencies simple.
      this.currentCategoryId = categoryId;
      this.loadProducts();
  }
}