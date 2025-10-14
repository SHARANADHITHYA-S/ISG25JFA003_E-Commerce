import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [RouterOutlet, MatToolbarModule, MatButtonModule, MatIconModule],
    template: `
        <mat-toolbar color="primary">
            <span>E-Commerce Store</span>
            <button mat-button routerLink="/orders">
                <mat-icon>shopping_basket</mat-icon>
                Orders
            </button>
        </mat-toolbar>
        <router-outlet></router-outlet>
    `,
    styles: [`
        mat-toolbar {
            margin-bottom: 20px;
        }
        
        button {
            margin-left: 20px;
        }
        
        mat-icon {
            margin-right: 5px;
        }
    `]
})
export class App {}
