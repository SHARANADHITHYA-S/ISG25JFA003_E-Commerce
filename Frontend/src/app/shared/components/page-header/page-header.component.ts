import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'app-page-header',
    standalone: true,
    imports: [CommonModule, MatIconModule],
    template: `
        <div class="page-header">
            <div class="header-content">
                <mat-icon *ngIf="icon">{{icon}}</mat-icon>
                <h1>{{title}}</h1>
            </div>
            <p *ngIf="subtitle" class="subtitle">{{subtitle}}</p>
        </div>
    `,
    styles: [`
        .page-header {
            padding: 20px;
            background-color: #fff;
            border-bottom: 1px solid #eee;
            margin-bottom: 20px;
        }

        .header-content {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        h1 {
            margin: 0;
            font-size: 24px;
            color: #333;
            font-weight: 500;
        }

        .subtitle {
            margin: 8px 0 0;
            color: #666;
            font-size: 14px;
        }

        mat-icon {
            color: #1976d2;
        }
    `]
})
export class PageHeaderComponent {
    @Input() title: string = '';
    @Input() subtitle?: string;
    @Input() icon?: string;
}
