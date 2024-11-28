package tennnisshop.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


public class Order {


    private Long id;

    private List<OrderItem> orderItems;

    private User user;

    private LocalDateTime date;


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
}
