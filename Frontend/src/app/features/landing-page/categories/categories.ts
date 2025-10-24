import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CategoryService } from '../../../core/services/category.service';
import { ProductService } from '../../../core/services/product.service';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';

interface CategoryWithCount {
  id: number;
  name: string;
  count: string;
  image: string;
  productCount: number;
}

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './categories.html'
})
export class CategoriesComponent implements OnInit {
  categories: CategoryWithCount[] = [];
  isLoading = true;

  constructor(
    private categoryService: CategoryService,
    private productService: ProductService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.isLoading = true;
    
    // Fetch both categories and products
    forkJoin({
      categories: this.categoryService.getAllCategories(),
      products: this.productService.getAllProducts()
    }).subscribe({
      next: ({ categories, products }) => {
        // Map categories with product counts
        this.categories = categories
          .slice(0, 4) // Show only first 4 categories
          .map(cat => {
            const productCount = products.filter(p => 
              p.category_id === cat.id && p.is_active
            ).length;
            
            return {
              id: cat.id,
              name: cat.name,
              count: `${productCount} item${productCount !== 1 ? 's' : ''}`,
              image: cat.imageUrl || 'https://images.unsplash.com/photo-1556740758-90de374c12ad?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080',
              productCount: productCount
            };
          });
        
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load categories:', err);
        this.isLoading = false;
      }
    });
  }

  navigateToCategory(categoryId: number): void {
    this.router.navigate(['/products'], { queryParams: { categoryId: categoryId } });
  }
}