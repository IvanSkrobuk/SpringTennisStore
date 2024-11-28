package tennnisshop.service;



import org.springframework.stereotype.Service;
import tennnisshop.entity.OrderItem;
import tennnisshop.repository.OrderItemRepository;

import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItemRepository.save(orderItem);
    }

    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id);
    }

    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    public void deleteOrderItem(Long id) {
        orderItemRepository.deleteById(id);
    }
    public List<OrderItem> findByOrderId(Long orderId) {
        return orderItemRepository.findAllByOrderId(orderId);
    }

}
