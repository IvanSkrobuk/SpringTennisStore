package tennnisshop.controller;


import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tennnisshop.entity.Analytics;
import tennnisshop.service.AnalyticsService;
import tennnisshop.service.OrderService;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SalesAPIController {

    private final OrderService orderService;
    private final AnalyticsService analyticsService;

    public SalesAPIController(OrderService orderService, AnalyticsService analyticsService) {
        this.orderService = orderService;
        this.analyticsService = analyticsService;
    }


    @GetMapping("/sales-per-day")
    public Map<String, Integer> getSalesPerDay() {
        return orderService.calculateSalesAmountPerDay();
    }

}