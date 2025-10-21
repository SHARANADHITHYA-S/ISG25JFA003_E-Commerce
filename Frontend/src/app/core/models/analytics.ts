export interface AnalyticsData {
  totalSales: number;
  totalOrders: number;
  totalUsers: number;
  totalProducts: number;
  salesByCategory: CategorySales[];
  monthlySales: MonthlySales[];
  topSellingProducts: TopProduct[];
}

export interface CategorySales {
  categoryName: string;
  sales: number;
}

export interface MonthlySales {
  month: string;
  sales: number;
}

export interface TopProduct {
  productName: string;
  sales: number;
}
