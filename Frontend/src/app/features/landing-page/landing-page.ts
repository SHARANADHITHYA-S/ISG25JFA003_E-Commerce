import { Component } from '@angular/core';
import { HeroComponent } from './hero/hero';
import { FeaturesComponent } from './features/features';
import { FeaturedProductsComponent } from './featured-products/featured-products';
import { CategoriesComponent } from './categories/categories';
import { FooterComponent } from '../../shared/components/footer/footer';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [
    HeroComponent,
    FeaturesComponent,
    FeaturedProductsComponent,
    CategoriesComponent,
    FooterComponent
  ],
  templateUrl: './landing-page.component.html'
})
export class LandingPageComponent {}