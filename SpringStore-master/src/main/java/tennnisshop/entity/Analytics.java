package tennnisshop.entity;


import java.time.LocalDateTime;

public class Analytics {
    private Long id;
    private Long productId;
    private Integer totalSales;
    private Double totalRevenue;
    private LocalDateTime lastSold;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getTotalSales() {
        return totalSales;
    }



    public void setTotalSales(Integer totalSales) {
        this.totalSales = totalSales;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public LocalDateTime getLastSold() {
        return lastSold;
    }

    public void setLastSold(LocalDateTime lastSold) {
        this.lastSold = lastSold;
    }


}
