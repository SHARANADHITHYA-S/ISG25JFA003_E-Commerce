import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface Address {
  id: number;
  address_line1: string;
  address_line2: string;
  city: string;
  state: string;
  street: string;
  postal_code: string;
  country: string;
  phone: string;
  created_at: string;
  updated_at: string;
  default: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AddressService {
  private apiUrl = 'http://localhost:8080/api/addresses';

  constructor(private http: HttpClient) { }

  getAddressesByUserId(userId: number): Observable<Address[]> {
    return this.http.get<Address[]>(`${this.apiUrl}/user/${userId}`).pipe(
      catchError(this.handleError)
    );
  }

  addAddress(userId: number, address: Partial<Address>): Observable<Address> {
    return this.http.post<Address>(`${this.apiUrl}/user/${userId}`, address).pipe(
      catchError(this.handleError)
    );
  }

  updateAddress(userId: number, addressId: number, address: Partial<Address>): Observable<Address> {
    return this.http.put<Address>(`${this.apiUrl}/${userId}/${addressId}`, address).pipe(
      catchError(this.handleError)
    );
  }

  getAddress(addressId: number): Observable<Address> {
    return this.http.get<Address>(`${this.apiUrl}/${addressId}`).pipe(
      catchError(this.handleError)
    );
  }

  deleteAddress(addressId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${addressId}`).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: any) {
    console.error('An API error occurred in AddressService', error);
    let errorMessage = 'Something went wrong with the API call.';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else if (error.status) {
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    } else if (error.message) {
      errorMessage = error.message;
    }
    return throwError(() => new Error(errorMessage));
  }
}
