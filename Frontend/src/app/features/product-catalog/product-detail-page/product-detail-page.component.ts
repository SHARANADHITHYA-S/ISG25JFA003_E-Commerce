import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { ProductResponseDTO } from '../../../core/models/product';
import { CartService } from '../../../core/services/cart.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product-detail-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-detail-page.component.html',
  styleUrls: ['./product-detail-page.component.scss']
})
export class ProductDetailPageComponent implements OnInit {
  product: ProductResponseDTO | undefined;
  quantity: number = 1;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService
  ) { }

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
      this.cartService.addCartItem(this.product.id, this.quantity).subscribe(() => {
        alert('Product added to cart!');
      });
    }
  }
}