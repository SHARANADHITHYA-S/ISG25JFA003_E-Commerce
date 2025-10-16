import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { ProductResponseDTO, ProductRequestDTO } from '../../core/models/product';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = 'http://localhost:8080/api/products';

  constructor(private http: HttpClient) { }

  getAllProducts(): Observable<ProductResponseDTO[]> {
    return this.http.get<ProductResponseDTO[]>(this.apiUrl).pipe(
      tap(data => console.log('Products from backend:', data))
    );
  }

  getProductById(id: number): Observable<ProductResponseDTO> {
    return this.http.get<ProductResponseDTO>(`${this.apiUrl}/${id}`);
  }

  getProductsByCategoryId(categoryId: number): Observable<ProductResponseDTO[]> {
    return this.http.get<ProductResponseDTO[]>(`${this.apiUrl}/category/${categoryId}`);
  }

  createProduct(product: ProductRequestDTO): Observable<ProductResponseDTO> {
    return this.http.post<ProductResponseDTO>(`${this.apiUrl}/admin`, product);
  }

  updateProduct(id: number, product: ProductRequestDTO): Observable<ProductResponseDTO> {
    return this.http.put<ProductResponseDTO>(`${this.apiUrl}/admin/${id}`, product);
  }

  deleteProduct(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/admin/${id}`, { responseType: 'text' });
  }
}