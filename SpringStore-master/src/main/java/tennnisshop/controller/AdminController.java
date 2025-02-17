package tennnisshop.controller;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tennnisshop.dto.AnalyticsDTO;
import tennnisshop.entity.*;
import tennnisshop.service.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
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
    private final ProductDetailsService productDetailsService;


    public AdminController(OrderService orderService, UserService userService, ProductService productService, AnalyticsService analyticsService, ModelMapper modelMapper, ProductDetailsService productDetailsService) {
        this.orderService = orderService;
        this.userService = userService;
        this.productService = productService;
        this.analyticsService = analyticsService;
        this.modelMapper = modelMapper;
        this.productDetailsService = productDetailsService;
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
    public String toShop(Model model) {
        model.addAttribute("products", productService.findAll());
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
            if (productService.findProductByTitle(title) != null) {
                return "redirect:/admin/create?error=Название товара уже существует";
            }

            ProductDetails productDetails = new ProductDetails();
            productDetails.setProductName(title);
            productDetails.setPrice(price);
            productDetails.setDescription(description);
            productDetails.setBrand(brand);
            productDetails.setWarrantyPeriod(warrantyPeriod);

            Product product = new Product();
            product.setTitle(title);
            product.setPrice(price);
            product.setCategory(category);
            product.setImgURL("im1.jpg");

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
    public ResponseEntity<String> updateQuantity(@PathVariable Long id, @RequestParam int quantity) {
        productService.updateQuantity(id, quantity);
        return ResponseEntity.ok("Изменения сохранены");
    }

    @GetMapping("/edit-product/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        ProductDetails productDetails = productDetailsService.findByProductId(id);

        model.addAttribute("product", product);
        model.addAttribute("productDetails", productDetails);

        return "/admin/edit-product";
    }

    @PostMapping("/edit-product/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @RequestParam("details_id") Long details_id,
            @RequestParam("title") String title,
            @RequestParam("price") int price,
            @RequestParam("description") String description,
            @RequestParam("brand") String brand,
            @RequestParam("category") String category,
            @RequestParam("warrantyPeriod") Integer warrantyPeriod
    ) {
        ProductDetails productDetails = new ProductDetails();
        productDetails.setId(details_id);
        productDetails.setProductName(title);
        productDetails.setPrice(price);
        productDetails.setDescription(description);
        productDetails.setBrand(brand);
        productDetails.setWarrantyPeriod(warrantyPeriod);

        productDetailsService.update(productDetails);


        Product product = new Product();
        product.setId(id);
        product.setTitle(title);
        product.setPrice(price);
        product.setCategory(category);
        product.setImgURL("im1.jpg");

        productService.update(product);

        return "redirect:/admin/admin-products";
    }






    //Экспорт в Excel
    @GetMapping("/export")
    public void exportAnalysisData(
            @RequestParam String exportTypes,
            HttpServletResponse response) throws IOException {

        // Создаем Excel Workbook
        Workbook workbook = new XSSFWorkbook();

        // Разбираем, какие данные экспортировать
        List<String> exportTypeList = Arrays.asList(exportTypes.split(","));

        // Экспорт пользователей
        if (exportTypeList.contains("users-export")) {
            Sheet userSheet = workbook.createSheet("Пользователи");
            List<User> users = userService.findAll();
            fillUserSheet(userSheet, users);
        }

        // Экспорт товаров
        if (exportTypeList.contains("products-export")) {
            Sheet productSheet = workbook.createSheet("Товары");
            List<Product> products = productService.findAll();
            fillProductSheet(productSheet, products);
        }

        // Экспорт заказов
        if (exportTypeList.contains("orders-export")) {
            Sheet orderSheet = workbook.createSheet("Заказы");
            List<Order> orders = orderService.getAllOrders();
            fillOrderSheet(orderSheet, orders);
        }

        // Экспорт аналитики
        if (exportTypeList.contains("analytics-export")) {
            Sheet analyticsSheet = workbook.createSheet("Аналитика");
            List<Analytics> analyticsList = analyticsService.getAllAnalytics();
            fillAnalyticsSheet(analyticsSheet, analyticsList);
        }

        // Устанавливаем заголовки ответа
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=export_data.xlsx");

        // Записываем Excel-файл в поток
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private void fillUserSheet(Sheet sheet, List<User> users) {
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Username");
            header.createCell(1).setCellValue("Password");
            header.createCell(2).setCellValue("Enabled");
            header.createCell(3).setCellValue("Authorities");

            int rowIndex = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(user.getUsername());
                row.createCell(1).setCellValue(user.getPassword());
                row.createCell(2).setCellValue(user.isEnabled());
                row.createCell(3).setCellValue(user.getAuthorities().toString());
            }
    }

    private void fillProductSheet(Sheet sheet, List<Product> products) {
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Название");
            header.createCell(2).setCellValue("Цена");
            header.createCell(3).setCellValue("Категория");
            header.createCell(4).setCellValue("Количество");

            int rowIndex = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getTitle());
                row.createCell(2).setCellValue(product.getPrice());
                row.createCell(3).setCellValue(product.getCategory());
                row.createCell(4).setCellValue(product.getQuantity());
            }
    }

    private void fillOrderSheet(Sheet sheet, List<Order> orders) {
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Дата");
            header.createCell(2).setCellValue("Статус");
            header.createCell(3).setCellValue("Пользователь");
            header.createCell(4).setCellValue("Товары");

            int rowIndex = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(order.getId());
                row.createCell(1).setCellValue(order.getDate().toString());
                row.createCell(2).setCellValue(order.getStatus());
                row.createCell(3).setCellValue(order.getUser().getUsername());
            }
    }

    private void fillAnalyticsSheet(Sheet sheet, List<Analytics> analyticsList) {
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Product ID");
            header.createCell(2).setCellValue("Total Sales");
            header.createCell(3).setCellValue("Total Revenue");
            header.createCell(4).setCellValue("Last Sold");

            int rowIndex = 1;
            for (Analytics analytics : analyticsList) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(analytics.getId());
                row.createCell(1).setCellValue(analytics.getProductId());
                row.createCell(2).setCellValue(analytics.getTotalSales());
                row.createCell(3).setCellValue(analytics.getTotalRevenue());
                row.createCell(4).setCellValue(analytics.getLastSold().toString());
            }
    }
}
