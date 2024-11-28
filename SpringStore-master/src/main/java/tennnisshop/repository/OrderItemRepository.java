package tennnisshop.repository;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import tennnisshop.entity.Order;
import tennnisshop.entity.OrderItem;
import tennnisshop.entity.Product;


import java.util.List;

@Repository
public class OrderItemRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper для преобразования строк результата в объекты OrderItem
    private final RowMapper<OrderItem> orderItemMapper = (rs, rowNum) -> {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(rs.getLong("id"));

        // Связанные объекты (загружаются по ID)
        Order order = new Order();
        order.setId(rs.getLong("order_id"));
        orderItem.setOrder(order);

        Product product = new Product();
        product.setId(rs.getLong("product_id"));
        orderItem.setProduct(product);

        return orderItem;
    };

    // Сохранение нового OrderItem
    public void save(OrderItem orderItem) {
        String sql = "INSERT INTO order_items (order_id, product_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, orderItem.getOrder().getId(), orderItem.getProduct().getId());
    }

    // Получение OrderItem по ID
    public OrderItem findById(Long id) {
        String sql = "SELECT * FROM order_items WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, orderItemMapper, id);
    }

    // Получение всех записей OrderItem
    public List<OrderItem> findAll() {
        String sql = "SELECT * FROM order_items";
        return jdbcTemplate.query(sql, orderItemMapper);
    }

    // Удаление OrderItem по ID
    public void deleteById(Long id) {
        String sql = "DELETE FROM order_items WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    public List<OrderItem> findAllByOrderId(Long orderId) {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        return jdbcTemplate.query(sql, orderItemMapper, orderId);
    }

}
