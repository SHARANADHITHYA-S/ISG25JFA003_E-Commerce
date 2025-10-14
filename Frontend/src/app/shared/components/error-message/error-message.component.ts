import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'app-error-message',
    standalone: true,
    imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
    template: `
        <mat-card class="error-container" [class.full-page]="fullPage">
            <mat-card-content class="error-content">
                <mat-icon class="error-icon" color="warn">error</mat-icon>
                <h2>{{ title }}</h2>
                <p>{{ message }}</p>
                <div class="action-buttons">
                    <button mat-button color="primary" (click)="retryClick.emit()" *ngIf="showRetry">
                        <mat-icon>refresh</mat-icon>
                        Retry
                    </button>
                    <button mat-button color="accent" (click)="backClick.emit()" *ngIf="showBack">
                        <mat-icon>arrow_back</mat-icon>
                        Go Back
                    </button>
                </div>
            </mat-card-content>
        </mat-card>
    `,
    styles: [`
        .error-container {
            margin: 20px;
            text-align: center;
        }

        .error-container.full-page {
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            min-width: 300px;
            max-width: 500px;
        }

        .error-content {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 20px;
        }

        .error-icon {
            font-size: 48px;
            height: 48px;
            width: 48px;
            margin-bottom: 16px;
        }

        h2 {
            margin: 0 0 16px;
            color: #333;
        }

        p {
            color: #666;
            margin-bottom: 20px;
        }

        .action-buttons {
            display: flex;
            gap: 16px;
        }

        button mat-icon {
            margin-right: 8px;
        }
    `]
})
export class ErrorMessageComponent {
    @Input() title: string = 'Error';
    @Input() message: string = 'An error occurred';
    @Input() showRetry: boolean = true;
    @Input() showBack: boolean = true;
    @Input() fullPage: boolean = false;

    @Output() retryClick = new EventEmitter<void>();
    @Output() backClick = new EventEmitter<void>();
}
