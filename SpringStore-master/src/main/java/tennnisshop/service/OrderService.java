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

        // Сохранение заказа, чтобы получить ID
        Order savedOrder = orderRepository.save(order); // После сохранения у order будет ID

        double orderPrice = 0.0;
        Map<Long, Integer> productQuantities = new HashMap<>();
        Map<Long, Double> productRevenues = new HashMap<>();

        // Логика добавления позиций в заказ
        for (OrderItem orderItem : orderItems) {
            // Привязка OrderItem к заказу
            orderItem.setOrder(savedOrder); // Устанавливаем ID заказа в OrderItem
            orderItemService.addOrderItem(orderItem); // Сохраняем OrderItem

            // Подсчёт аналитики
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
        return savedOrder; // Возвращаем сохранённый заказ
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
        for (Authority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return getAllOrders();
            }
        }
        return getOrderByUser(user);
    }


    /////////////////////
    public Map<String, Integer> calculateSalesAmountPerDay() {
        // Заранее заданный порядок дней недели
        List<String> daysOfWeekOrder = List.of(
                "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
        );

        // Используем HashMap для подсчёта
        Map<String, Integer> salesPerDay = new HashMap<>();

        // Получаем все заказы из репозитория
        List<Order> orders = orderRepository.findAll();

        for (Order order : orders) {
            // Получаем день недели в виде строки
            String dayOfWeek = order.getDate().getDayOfWeek().toString();

            int totalOrderAmount = 0;

            // Загружаем связанные OrderItem для текущего заказа
            List<OrderItem> orderItems = orderItemService.findByOrderId(order.getId());
            for (OrderItem item : orderItems) {
                totalOrderAmount += productService.getProductById(item.getProduct().getId()).getPrice();
            }

            // Суммируем продажи по дням недели
            salesPerDay.put(dayOfWeek, salesPerDay.getOrDefault(dayOfWeek, 0) + totalOrderAmount);
        }

        // Сортируем результат по заранее заданному порядку
        Map<String, Integer> sortedSalesPerDay = new LinkedHashMap<>();
        for (String day : daysOfWeekOrder) {
            sortedSalesPerDay.put(day, salesPerDay.getOrDefault(day, 0));
        }

        System.out.println(sortedSalesPerDay);
        return sortedSalesPerDay;
    }


}
