package tennnisshop.entity;

import java.time.LocalDateTime;
import java.util.List;


public class Order {


    private Long id;

    private List<OrderItem> orderItems;

    private User user;

    private LocalDateTime date;

    private String status;


    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }



    public Order() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "Order [" +
                "id=" + id +
                ", orderItems=" + orderItems +
                ", user=" + user.getUsername() +
                ']';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
