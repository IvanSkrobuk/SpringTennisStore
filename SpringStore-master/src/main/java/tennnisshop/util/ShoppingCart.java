package tennnisshop.util;

import tennnisshop.entity.OrderItem;
import tennnisshop.entity.Product;
import tennnisshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ShoppingCart {

    private List<OrderItem> orderItems;

    private ProductService productService;

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @PostConstruct
    public void init() {
        orderItems = new ArrayList<>();
    }

    public void addProductById(Long id) {
        Product product = productService.getProductById(id);
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItems.add(orderItem);
    }


    public int getTotalPrice() {
        return orderItems.stream()
                .mapToInt(orderItem -> {
                    Product product = productService.getProductById(orderItem.getProduct().getId());
                    return (product != null) ? product.getPrice() : 0;
                })
                .sum();
    }
    public void removeProductById(Long id) {
        Iterator<OrderItem> iterator = orderItems.iterator();
        while (iterator.hasNext()) {
            OrderItem orderItem = iterator.next();
            if (orderItem.getProduct().getId().equals(id)) {
                iterator.remove();
                break;
            }
        }
    }
    public void clearOrderItems() {
        orderItems.clear();
    }



}
