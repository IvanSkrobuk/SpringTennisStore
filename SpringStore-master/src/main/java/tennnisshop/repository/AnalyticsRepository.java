package tennnisshop.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import tennnisshop.entity.Analytics;

import java.util.List;
import java.util.Optional;

@Repository
public class AnalyticsRepository {

    private final JdbcTemplate jdbcTemplate;

    public AnalyticsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper для преобразования ResultSet в объект Analytics
    private final RowMapper<Analytics> analyticsRowMapper = (rs, rowNum) -> {
        Analytics analytics = new Analytics();
        analytics.setId(rs.getLong("id"));
        analytics.setProductId(rs.getLong("product_id"));
        analytics.setTotalSales(rs.getInt("total_sales"));
        analytics.setTotalRevenue(rs.getDouble("total_revenue"));
        analytics.setLastSold(rs.getTimestamp("last_sold").toLocalDateTime());
        return analytics;
    };

    // Найти аналитику по productId
    public Optional<Analytics> findByProductId(Long productId) {
        String sql = "SELECT * FROM analytics WHERE product_id = ?";
        try {
            Analytics analytics = jdbcTemplate.queryForObject(sql, analyticsRowMapper, productId);
            return Optional.ofNullable(analytics);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Обновить аналитику
    public void updateAnalytics(Long productId, Integer salesIncrement, Double revenueIncrement) {
        String sql = "UPDATE analytics SET total_sales = total_sales + ?, total_revenue = total_revenue + ?, last_sold = CURRENT_TIMESTAMP WHERE product_id = ?";
        jdbcTemplate.update(sql, salesIncrement, revenueIncrement, productId);
    }

    // Создать новую аналитику
    public void createAnalytics(Long productId) {
        String sql = "INSERT INTO analytics (product_id, total_sales, total_revenue) VALUES (?, 0, 0.00)";
        jdbcTemplate.update(sql, productId);
    }

    // Получить все записи аналитики
    public List<Analytics> findAll() {
        String sql = "SELECT * FROM analytics";
        return jdbcTemplate.query(sql, analyticsRowMapper);
    }
}
