import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from './auth.service';

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

  constructor(private http: HttpClient, private authService: AuthService) { }

  private getUserIdFromAuth(): number {
    const user = this.authService.getCurrentUser();
    if (user && user.id) {
      return user.id;
    }
    throw new Error('User not logged in or user ID not available.');
  }

  getAddressesByUserId(): Observable<Address[]> {
    let userId: number;
    try {
      userId = this.getUserIdFromAuth();
    } catch (error: any) {
      return throwError(() => new Error('Authentication required to load addresses. Please log in.'));
    }
    return this.http.get<Address[]>(`${this.apiUrl}/user/${userId}`).pipe(
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