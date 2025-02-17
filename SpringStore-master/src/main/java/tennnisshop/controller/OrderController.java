package tennnisshop.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import tennnisshop.entity.Order;
import tennnisshop.entity.User;
import tennnisshop.service.OrderService;
import tennnisshop.service.ProductService;
import tennnisshop.service.UserService;
import tennnisshop.util.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private OrderService orderService;

    private UserService userService;

    private ShoppingCart cart;
    private ProductService productService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setCart(ShoppingCart cart) {
        this.cart = cart;
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("")
    public String showOrders(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("orders", orderService.getCustomOrders(principal));
        return "orders";
    }

    @GetMapping("/order-details/{id}")
    public String toOrderDetails(Model model, @PathVariable("id") Long id) {
        Order selectedOrder = orderService.getOrderById(id);
        model.addAttribute("selectedOrder", selectedOrder);
        model.addAttribute("orderItems", productService.findProductsByOrderId(selectedOrder.getId()));
        return "order-details";
    }

    @GetMapping("/create_order")
    public String createOrder(Principal principal, Model model) {
        try {
            User user = userService.findByUsername(principal.getName());
            Order orderFromItems = orderService.createOrderFromItems(user, cart.getOrderItems());
            return "redirect:/orders/";
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("orderItems", new ArrayList<>());
            return "cart";
        }
    }


    @GetMapping({"/remove/{id}", "/order-details/remove/{id}"})
    public String deleteOrderById(@PathVariable("id") Long id) {
        orderService.updateOrderStatus(id,"CANCELLED");
        return "redirect:/orders";
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }


}
