import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [RouterOutlet, RouterLink, RouterLinkActive, NgbModule],
    template: `
        <div class="app-container">
            <nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
                <div class="container">
                    <a class="navbar-brand" routerLink="/">E-Commerce</a>
                    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div class="collapse navbar-collapse" id="navbarNav">
                        <ul class="navbar-nav">
                            <li class="nav-item">
                                <a class="nav-link" routerLink="/orders" routerLinkActive="active">Orders</a>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>
            <router-outlet></router-outlet>
        </div>
    `,
    styles: [`
        .app-container {
            min-height: 100vh;
            background-color: #f8f9fa;
        }
    `]
})
export class AppComponent { }
