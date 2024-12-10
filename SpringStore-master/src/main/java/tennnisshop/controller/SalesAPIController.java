package tennnisshop.controller;


import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tennnisshop.service.AnalyticsService;
import tennnisshop.service.OrderService;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SalesAPIController {

    private final OrderService orderService;

    public SalesAPIController(OrderService orderService) {
        this.orderService = orderService;
    }


    @GetMapping("/sales-per-day")
    public Map<String, Integer> getSalesPerDay() {
        return orderService.calculateSalesAmountPerDay();
    }

}