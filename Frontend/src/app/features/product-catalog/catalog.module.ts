import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CatalogRoutingModule } from './catalog-routing.module';
import { ProductListPageComponent } from './product-list-page/product-list-page.component';
import { ProductDetailPageComponent } from './product-detail-page/product-detail-page.component';
import { CategoryFilterComponent } from '../../components/category-filter/category-filter.component';
import { ProductCardComponent } from '../../components/product-card/product-card.component';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [
    ProductListPageComponent,
    ProductDetailPageComponent,
    CategoryFilterComponent,
    ProductCardComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    HttpClientModule,
    CatalogRoutingModule
  ]
})
export class CatalogModule {}
