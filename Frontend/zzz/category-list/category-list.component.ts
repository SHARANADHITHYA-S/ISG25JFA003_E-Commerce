// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { RouterModule } from '@angular/router';

// import { CategoryService } from '../../core/services/category.service';
// import { CategoryResponseDTO } from '../../core/models/category';

// @Component({
//   selector: 'app-category-list',
//   standalone: true,
//   imports: [
//     CommonModule,   // <-- Fixes *ngFor, *ngIf, ngClass
//     RouterModule    // <-- Fixes routerLink
//   ],
//   templateUrl: './category-list.component.html',
//   styleUrls: ['./category-list.component.scss']
// })
// export class CategoryListComponent implements OnInit {
//   categories: CategoryResponseDTO[] = [];

//   constructor(private categoryService: CategoryService) { }

//   ngOnInit(): void {
//     this.fetchCategories();
//   }

//   fetchCategories(): void {
//     this.categoryService.getAllCategories().subscribe((data: CategoryResponseDTO[]) => {
//       this.categories = data;
//     });
//   }

//   onDelete(id: number): void {
//     if (confirm('Are you sure you want to delete this category? All products in this category will also be deleted.')) {
//       this.categoryService.deleteCategory(id).subscribe(() => {
//         this.categories = this.categories.filter(c => c.id !== id);
//       });
//     }
//   }
// }
