import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ProductService } from './product.service';
import { ProductResponseDTO, ProductRequestDTO } from '../models/product';

describe('ProductService', () => {
  let service: ProductService;
  let httpTestingController: HttpTestingController;
  const apiUrl = 'http://localhost:8080/api/products';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProductService]
    });
    service = TestBed.inject(ProductService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify(); // Ensure that no outstanding requests are uncaught
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve all products', () => {
    const mockProducts: ProductResponseDTO[] = [
      { id: 1, name: 'Product 1', description: 'Desc 1', price: 10, image_url: '', is_active: true, created_at: new Date(), updated_at: new Date(), category_id: 1, quantity: 10 },
      { id: 2, name: 'Product 2', description: 'Desc 2', price: 20, image_url: '', is_active: true, created_at: new Date(), updated_at: new Date(), category_id: 1, quantity: 20 }
    ];

    service.getAllProducts().subscribe(products => {
      expect(products).toEqual(mockProducts);
    });

    const req = httpTestingController.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mockProducts);
  });

  it('should retrieve a product by id', () => {
    const mockProduct: ProductResponseDTO = { id: 1, name: 'Product 1', description: 'Desc 1', price: 10, image_url: '', is_active: true, created_at: new Date(), updated_at: new Date(), category_id: 1, quantity: 10 };

    service.getProductById(1).subscribe(product => {
      expect(product).toEqual(mockProduct);
    });

    const req = httpTestingController.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProduct);
  });

  it('should retrieve products by category id', () => {
    const mockProducts: ProductResponseDTO[] = [
      { id: 1, name: 'Product 1', description: 'Desc 1', price: 10, image_url: '', is_active: true, created_at: new Date(), updated_at: new Date(), category_id: 1, quantity: 10 }
    ];

    service.getProductsByCategoryId(1).subscribe(products => {
      expect(products).toEqual(mockProducts);
    });

    const req = httpTestingController.expectOne(`${apiUrl}/category/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProducts);
  });

  it('should create a product', () => {
    const newProduct: ProductRequestDTO = { name: 'New Product', description: 'New Desc', price: 30, isActive: true, image_url: '', quantity: 30, categoryId: 1 };
    const mockProduct: ProductResponseDTO = { id: 3, ...newProduct, is_active: true, created_at: new Date(), updated_at: new Date(), category_id: newProduct.categoryId };

    service.createProduct(newProduct).subscribe(product => {
      expect(product).toEqual(mockProduct);
    });

    const req = httpTestingController.expectOne(`${apiUrl}/admin`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newProduct);
    req.flush(mockProduct);
  });

  it('should update a product', () => {
    const updatedProduct: ProductRequestDTO = { name: 'Updated Product', description: 'Updated Desc', price: 15, isActive: true, image_url: '', quantity: 15, categoryId: 1 };
    const mockProduct: ProductResponseDTO = { id: 1, ...updatedProduct, is_active: true, created_at: new Date(), updated_at: new Date(), category_id: updatedProduct.categoryId };

    service.updateProduct(1, updatedProduct).subscribe(product => {
      expect(product).toEqual(mockProduct);
    });

    const req = httpTestingController.expectOne(`${apiUrl}/admin/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedProduct);
    req.flush(mockProduct);
  });

  it('should delete a product', () => {
    service.deleteProduct(1).subscribe(response => {
      expect(response).toBe('Product deleted successfully');
    });

    const req = httpTestingController.expectOne(`${apiUrl}/admin/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush('Product deleted successfully');
  });
});
