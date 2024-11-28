package tennnisshop.service;

import tennnisshop.entity.Authority;
import tennnisshop.entity.Order;
import tennnisshop.entity.OrderItem;
import tennnisshop.entity.User;
import tennnisshop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private OrderRepository orderRepository;

    private UserService userService;

    private AnalyticsService analyticsService;

    private OrderItemService orderItemService;

    private ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setAnalyticsService(AnalyticsService analyticsService) {this.analyticsService = analyticsService;}

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    @Autowired
    public void setOrderItemService(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    public Order createOrderFromItems(User user, List<OrderItem> orderItems) {
        // Создание нового заказа
        Order order = new Order();
        order.setOrderItems(new ArrayList<>());
        order.setUser(user);
        order.setDate(LocalDateTime.now());
        order.setStatus("PENDING");

        Order savedOrder = orderRepository.save(order);

        double orderPrice = 0.0;
        Map<Long, Integer> productQuantities = new HashMap<>();
        Map<Long, Double> productRevenues = new HashMap<>();

        // Логика добавления позиций в заказ
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(savedOrder);
            orderItemService.addOrderItem(orderItem);
            productService.updateQuantity(orderItem.getProduct().getId(),orderItem.getProduct().getQuantity()-1);
            Long productId = orderItem.getProduct().getId();
            Double price = (double) orderItem.getProduct().getPrice();
            productQuantities.put(productId, productQuantities.getOrDefault(productId, 0) + 1);
            productRevenues.put(productId, productRevenues.getOrDefault(productId, 0.0) + price);
        }

        // Обновление аналитики для каждого продукта
        productQuantities.forEach((productId, quantity) -> {
            Double revenue = productRevenues.get(productId);
            analyticsService.updateAnalyticsAfterSale(productId, quantity, revenue);
        });

        orderItems.clear();
        return savedOrder;
    }



    public Order getOrderById(Long id) {
        return orderRepository.getOne(id);
    }

    public List<Order> getOrderByUser(User user) {
        return orderRepository.findAllByUser(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }

    public List<Order> getCustomOrders(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        List<Authority> authorities = user.getAuthorities();
        List<Order> orders;
        for (Authority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                orders = getAllOrders();
                orders.sort(Comparator.comparing(Order::getDate).reversed());
                return orders;
            }
        }
        orders = getOrderByUser(user);
        orders.sort(Comparator.comparing(Order::getDate).reversed());
        return orders;
    }

    /////////////////////
    public Map<String, Integer> calculateSalesAmountPerDay() {
        List<String> daysOfWeekOrder = List.of(
                "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
        );

        Map<String, Integer> salesPerDay = new HashMap<>();

        List<Order> orders = orderRepository.findAll();

        for (Order order : orders) {
            String dayOfWeek = order.getDate().getDayOfWeek().toString();

            int totalOrderAmount = 0;

            List<OrderItem> orderItems = orderItemService.findByOrderId(order.getId());
            for (OrderItem item : orderItems) {
                totalOrderAmount += productService.getProductById(item.getProduct().getId()).getPrice();
            }

            salesPerDay.put(dayOfWeek, salesPerDay.getOrDefault(dayOfWeek, 0) + totalOrderAmount);
        }

        Map<String, Integer> sortedSalesPerDay = new LinkedHashMap<>();
        for (String day : daysOfWeekOrder) {
            sortedSalesPerDay.put(day, salesPerDay.getOrDefault(day, 0));
        }

        System.out.println(sortedSalesPerDay);
        return sortedSalesPerDay;
    }

    public void updateOrderStatus(Long orderId, String status) {
        orderRepository.updateOrderStatus(orderId, status);
    }

}
