package tennnisshop.entity;

import javax.persistence.*;


public class Product {

    private Long id;

    private String title;

    private int price;

    private String imgURL;

    private String category;

    private int quantity;

    public Product() {}

    public Product(Long id, String title, int price, String imgURL, String category, int quantity) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.imgURL = imgURL;
        this.category = category;
        this.quantity = quantity;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product [" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price=" + price +
                ']';
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
