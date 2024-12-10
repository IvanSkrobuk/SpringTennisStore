package tennnisshop.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import tennnisshop.entity.Order;
import tennnisshop.entity.Product;
import tennnisshop.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Order> findAllByUser(User user) {
        String sql = "SELECT * FROM orders WHERE username = ?";
        return jdbcTemplate.query(sql, new Object[]{user.getUsername()}, this::mapRowToOrder);
    }

    public Order save(Order order) {
        if (order.getId() == null) {
            // Сохранение нового заказа с текущей датой
            String sql = "INSERT INTO orders (username, date, status) VALUES (?, ?, ?) RETURNING id";
            Long id = jdbcTemplate.queryForObject(
                    sql,
                    new Object[]{
                            order.getUser().getUsername(),
                            java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()),
                            order.getStatus()
                    },
                    Long.class
            );
            order.setId(id);
        } else {
            // Обновление существующего заказа
            String sql = "UPDATE orders SET username = ?, date = ?, status =? WHERE id = ?";
            jdbcTemplate.update(
                    sql,
                    order.getUser().getUsername(),
                    java.sql.Timestamp.valueOf(order.getDate()),
                    order.getStatus(),
                    order.getId()
            );
        }
        return order;
    }



    // Аналог метода JpaRepository: getOne (загружает заказ по ID)
    public Order getOne(Long id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, this::mapRowToOrder);
    }

    // Аналог метода JpaRepository: findAll
    public List<Order> findAll() {
        String sql = "SELECT * FROM orders";
        return jdbcTemplate.query(sql, this::mapRowToOrder);
    }

    // Аналог метода JpaRepository: deleteById
    public void deleteById(Long id) {
        String deleteOrderItemsSql = "DELETE FROM order_items WHERE order_id = ?";
        jdbcTemplate.update(deleteOrderItemsSql, id);

        String deleteOrderSql = "DELETE FROM orders WHERE id = ?";
        jdbcTemplate.update(deleteOrderSql, id);
    }

    // Метод для маппинга строки из базы данных в объект Order
    private Order mapRowToOrder(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setDate(rs.getTimestamp("date").toLocalDateTime());
        order.setStatus(rs.getString("status"));
        User user = new User();
        user.setUsername(rs.getString("username"));
        order.setUser(user);

        return order;
    }

    // Метод для обновления статуса заказа
    public void updateOrderStatus(Long orderId, String status) {
        String sql = "UPDATE shop.orders SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, orderId);
    }

}
