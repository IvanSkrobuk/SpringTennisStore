package tennnisshop.service;

import org.springframework.transaction.annotation.Transactional;
import tennnisshop.entity.Authority;
import tennnisshop.entity.Order;
import tennnisshop.entity.OrderItem;
import tennnisshop.entity.User;
import tennnisshop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
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


    @Transactional
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
        int rlbck =0;


        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(savedOrder);
            orderItemService.addOrderItem(orderItem);

            System.out.println("Добавлен товар в заказ: ID=" + orderItem.getProduct().getId());
            Long productId = orderItem.getProduct().getId();
            int requestedQuantity = 1;
            int currentQuantity = productService.getQuantity(orderItem.getProduct());
            if (currentQuantity >= requestedQuantity) {
                productService.updateQuantity(productId, currentQuantity - requestedQuantity);
                int price = orderItem.getProduct().getPrice();
                rlbck +=1 ;
                productQuantities.put(productId, productQuantities.getOrDefault(productId, 0) + requestedQuantity);
                productRevenues.put(productId, productRevenues.getOrDefault(productId, 0.0) + price * requestedQuantity);
            } else {
                System.out.println("Недостаточно товара: ID=" + productId + ", доступно: " + (currentQuantity+rlbck));
                throw new IllegalStateException("Товара '" +productService.getProductById(productId).getTitle()+ " недостаточно на складе, в наличии: " + (currentQuantity+rlbck)+".");
            }
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

    public Map<String, Integer> calculateSalesAmountPerDay() {
        Map<String, Integer> salesPerDay = new LinkedHashMap<>();

        // Получаем текущую дату и дату 7 дней назад
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        // Получаем все заказы за последние 7 дней
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> !order.getDate().toLocalDate().isBefore(weekAgo))
                .collect(Collectors.toList());

        // Инициализируем карту для последних 7 дней
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            salesPerDay.put(date.format(DateTimeFormatter.ISO_DATE), 0);
        }

        // Рассчитываем сумму продаж для каждого дня
        for (Order order : orders) {
            String orderDate = order.getDate().toLocalDate().format(DateTimeFormatter.ISO_DATE);

            int totalOrderAmount = 0;
            List<OrderItem> orderItems = orderItemService.findByOrderId(order.getId());
            for (OrderItem item : orderItems) {
                totalOrderAmount += productService.getProductById(item.getProduct().getId()).getPrice();
            }

            salesPerDay.put(orderDate, salesPerDay.getOrDefault(orderDate, 0) + totalOrderAmount);
        }

        System.out.println(salesPerDay);
        return salesPerDay;
    }

    public void updateOrderStatus(Long orderId, String status) {
        orderRepository.updateOrderStatus(orderId, status);
    }

}
