export interface AnalyticsData {
  reportId: number;
  reportName: string;
  creationDate: Date;
  reportData: string;
}

export interface CategorySales {
  categoryName: string;
  sales: number;
}

export interface MonthlySales {
  month: string;
  sales: number;
}

export interface ProductSales {
  productName: string;
  sales: number;
}
