import { Component, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { ProductResponseDTO } from '../../../core/models/product';
import { CartService } from '../../../core/services/cart.service';
import { FormsModule } from '@angular/forms';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-product-detail-page',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastModule],
  providers: [MessageService],
  templateUrl: './product-detail-page.component.html',
  styleUrls: ['./product-detail-page.component.scss']
})
export class ProductDetailPageComponent implements OnInit {
  product: ProductResponseDTO | undefined;
  quantity: number = 1;

  constructor(
    private messageService: MessageService,
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
    private location: Location
  ) { }

  goBack(): void {
    this.location.back();
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.productService.getProductById(Number(id)).subscribe((data: ProductResponseDTO) => {
        this.product = data;
      });
    }
  }

  addToCart(): void {
    if (this.product) {
      this.cartService.addCartItem(this.product.id, this.quantity).subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: '🛒 Product added to cart successfully!',
            life: 1000
          });
        },
        error: (err) => {
          console.error('Error adding to cart:', err);
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to add product to cart',
            life: 5000
          });
        }
      });
    }
  }
} 
