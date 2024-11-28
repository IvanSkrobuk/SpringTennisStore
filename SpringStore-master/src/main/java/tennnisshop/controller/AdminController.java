package tennnisshop.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import tennnisshop.dto.AnalyticsDTO;
import tennnisshop.entity.Analytics;
import tennnisshop.entity.User;
import tennnisshop.service.AnalyticsService;
import tennnisshop.service.OrderService;
import tennnisshop.service.ProductService;
import tennnisshop.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;
    private final AnalyticsService analyticsService;
    private final ModelMapper modelMapper;

    public AdminController(OrderService orderService, UserService userService, ProductService productService, AnalyticsService analyticsService, ModelMapper modelMapper) {
        this.orderService = orderService;
        this.userService = userService;
        this.productService = productService;
        this.analyticsService = analyticsService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("")
    public String showAnalytics(Model model) {
        List<Analytics> analytics = analyticsService.getAllAnalytics();

        List<AnalyticsDTO> analyticsWithProductNames = analytics.stream()
                .map(anal -> {
                    AnalyticsDTO dto = modelMapper.map(anal, AnalyticsDTO.class);
                    dto.setProductName(productService.getProductById(anal.getProductId()).getTitle());
                    return dto;
                })
                .collect(Collectors.toList());

        analyticsWithProductNames.sort((a1, a2) -> Double.compare(a2.getTotalRevenue(), a1.getTotalRevenue()));

        model.addAttribute("analytics", analyticsWithProductNames);
        return "admin/index";
    }

    @GetMapping("/admin-orders")
    public String showOrders(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("orders", orderService.getCustomOrders(principal));
        return "admin/admin-orders";
    }
    @GetMapping("/remove-order/{id}")
    public String deleteOrderById(@PathVariable("id") Long id) {
        orderService.deleteOrderById(id);
        return "redirect:/admin/admin-orders";
    }

    @GetMapping("/admin-users")
    public String showAllUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/admin-users";
    }
    @GetMapping("/admin-products")
    public String toShop(Model model, Pageable pageable) {
        model.addAttribute("products", productService.getAllProducts(pageable));
        return "admin/admin-products";
    }
    @GetMapping("/remove/{id}")
    public String deleteByProductId(@PathVariable("id") Long id) {
        productService.deleteProductById(id);
        return "redirect:/admin/admin-products";
    }
}
