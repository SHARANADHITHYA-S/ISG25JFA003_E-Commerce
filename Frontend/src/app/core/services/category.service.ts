import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { CategoryResponseDTO, CategoryRequestDTO } from '../../core/models/category';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private apiUrl = 'http://localhost:8080/api/categories';

  constructor(private http: HttpClient) { }

  getAllCategories(): Observable<CategoryResponseDTO[]> {
    return this.http.get<CategoryResponseDTO[]>(this.apiUrl).pipe(
      tap(data => console.log('Categories from backend:', data))
    );
  }

  getCategoryById(id: number): Observable<CategoryResponseDTO> {
    return this.http.get<CategoryResponseDTO>(`${this.apiUrl}/${id}`);
  }

  createCategory(category: CategoryRequestDTO): Observable<CategoryResponseDTO> {
    return this.http.post<CategoryResponseDTO>(`${this.apiUrl}/admin`, category);
  }

  updateCategory(id: number, category: CategoryRequestDTO): Observable<CategoryResponseDTO> {
    return this.http.put<CategoryResponseDTO>(`${this.apiUrl}/admin/${id}`, category);
  }

  deleteCategory(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/admin/${id}`, { responseType: 'text' });
  }
}