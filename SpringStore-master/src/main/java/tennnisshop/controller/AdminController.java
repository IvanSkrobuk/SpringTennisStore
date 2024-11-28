package tennnisshop.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tennnisshop.dto.AnalyticsDTO;
import tennnisshop.entity.Analytics;
import tennnisshop.entity.Product;
import tennnisshop.entity.ProductDetails;
import tennnisshop.entity.User;
import tennnisshop.service.AnalyticsService;
import tennnisshop.service.OrderService;
import tennnisshop.service.ProductService;
import tennnisshop.service.UserService;

import java.io.IOException;
import java.math.BigDecimal;
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

    @GetMapping("/create")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "/admin/create-product";
    }

    @PostMapping("/create")
    public String createProduct(
            @RequestParam("title") String title,
            @RequestParam("price") int price,
            @RequestParam("description") String description,
            @RequestParam("brand") String brand,
            @RequestParam("category") String category,
            @RequestParam("warrantyPeriod") Integer warrantyPeriod,
            @RequestParam(value = "file1", required = false) MultipartFile file1,
            @RequestParam(value = "file2", required = false) MultipartFile file2,
            @RequestParam(value = "file3", required = false) MultipartFile file3
    ) {
        try {
            // Создание нового объекта ProductDetails
            ProductDetails productDetails = new ProductDetails();
            productDetails.setProductName(title);
            productDetails.setPrice(price);
            productDetails.setDescription(description);
            productDetails.setBrand(brand);
            productDetails.setWarrantyPeriod(warrantyPeriod);

            // Создание нового продукта и установка связанных данных
            Product product = new Product();
            product.setTitle(title);
            product.setPrice(price);
            product.setCategory(category);
            product.setImgURL("im1.jpg");

            // Сохранение продукта и связанных изображений
            productService.saveProduct(product, productDetails, file1, file2, file3);

            return "redirect:/admin/admin-products";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }


    @PostMapping("/update-order-status/{orderId}")
    public String updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        try {
            orderService.updateOrderStatus(orderId, status);
            return "redirect:/admin/admin-orders";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @PostMapping("/update-active")
    public String updateActiveStatus(@RequestParam String username, @RequestParam boolean enabled) {
        userService.updateUserStatus(username, enabled);
        return "redirect:/admin/admin-users";
    }

    @PostMapping("/update-title/{id}")
    public String updateTitle(@PathVariable Long id, @RequestParam String title) {
        productService.updateTitle(id, title);
        return "redirect:/admin/admin-products";
    }

    @PostMapping("/update-price/{id}")
    public String updatePrice(@PathVariable Long id, @RequestParam int price) {
        productService.updatePrice(id, price);
        return "redirect:/admin/admin-products";
    }

    @PostMapping("/update-quantity/{id}")
    public String updateQuantity(@PathVariable Long id, @RequestParam int quantity) {
        productService.updateQuantity(id, quantity);
        return "redirect:/admin/admin-products";
    }



}
