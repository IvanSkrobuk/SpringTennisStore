package tennnisshop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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