package tennnisshop.dto;

import java.time.LocalDateTime;


public class AnalyticsDTO {
    private Long id;
    private String productName;
    private LocalDateTime lastSold;
    private int totalSales;
    private double totalRevenue;

    public AnalyticsDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public LocalDateTime getLastSold() {
        return lastSold;
    }

    public void setLastSold(LocalDateTime lastSold) {
        this.lastSold = lastSold;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
