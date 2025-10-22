import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { User } from '../models/user';
import { NotificationService } from './notification.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/user';

  constructor(private http: HttpClient, private notificationService: NotificationService) { }

  getUserById(userId: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${userId}`).pipe(
      catchError(this.handleError)
    );
  }

  updateUser(userId: number, user: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${userId}`, user).pipe(
      tap(() => this.notificationService.showSuccess('User details updated successfully')),
      catchError(err => {
        this.notificationService.showError('Failed to update user details');
        return this.handleError(err);
      })
    );
  }

  private handleError(error: any) {
    console.error('An API error occurred in UserService', error);
    console.error('Full error object from interceptor:', error); // Log the full error object
    let errorMessage = 'Something went wrong with the API call.';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else if (error.status) {
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
      if (error.error && error.error.message) { // Check for server-provided error message
        errorMessage += `\nServer Message: ${error.error.message}`;
      }
    }
    return throwError(() => new Error(errorMessage));
  }
}
