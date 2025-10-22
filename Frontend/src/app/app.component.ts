import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { ToastModule } from 'primeng/toast';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [RouterOutlet, NgbModule, NavbarComponent, ToastModule],
    template: `
        <app-navbar></app-navbar>
        <router-outlet></router-outlet>
    `,
})
export class AppComponent { }
