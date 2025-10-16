import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

// Define an interface for the Category object for type safety
interface Category {
  id: number;
  name: string;
  count: string;
  image: string;
}

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './categories.html'
})
export class CategoriesComponent {
  // An array of category data to display
  categories: Category[] = [
    {
      id: 1,
      name: 'Electronics',
      count: '2,400+ items',
      image: 'https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080',
    },
    {
      id: 2,
      name: 'Gaming',
      count: '1,200+ items',
      image: 'https://images.unsplash.com/photo-1550745165-9bc0b252726a?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080',
    },
    {
      id: 3,
      name: 'Fashion',
      count: '3,500+ items',
      image: 'https://images.unsplash.com/photo-1483985988355-763728e1935b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080',
    },
    {
      id: 4,
      name: 'Home & Decor',
      count: '1,800+ items',
      image: 'https://images.unsplash.com/photo-1556020685-ae41abfc9365?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080',
    },
  ];
}