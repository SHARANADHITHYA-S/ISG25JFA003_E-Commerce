import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NavbarComponent } from './shared/components/navbar/navbar.component';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [RouterOutlet, RouterLink, RouterLinkActive, NgbModule, NavbarComponent],
    template: `
        <div class="app-container">
            <app-navbar></app-navbar>
            <router-outlet></router-outlet>
        </div>
    `,
    styles: [`
        .app-container {
            min-height: 100vh;
        }
    `]
})
export class AppComponent { }
