package tennnisshop.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import tennnisshop.entity.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Найти продукт по ID
    public Optional<Product> findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new Object[]{id}, this::mapRowToProduct));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Найти продукт по названию
    public Product findOneByTitle(String title) {
        String sql = "SELECT * FROM products WHERE title = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{title}, this::mapRowToProduct);
    }

    // Найти все продукты в диапазоне цен
    public List<Product> findAllByPriceBetween(int min, int max) {
        String sql = "SELECT * FROM products WHERE price BETWEEN ? AND ?";
        return jdbcTemplate.query(sql, new Object[]{min, max}, this::mapRowToProduct);
    }

    // Удалить продукт по ID
    public void deleteById(Long id) {
        String deleteOrderItemsSql = "DELETE FROM order_items WHERE product_id = ?";
        jdbcTemplate.update(deleteOrderItemsSql, id);

        String deleteAnalyticsSql = "DELETE FROM analytics WHERE product_id = ?";
        jdbcTemplate.update(deleteAnalyticsSql, id);

        String deleteProductSql = "DELETE FROM products WHERE id = ?";
        jdbcTemplate.update(deleteProductSql, id);

    }


    // Сохранить продукт (вставка или обновление)
    public void save(Product product) {
        if (product.getId() == null) {
            // Вставка нового продукта
            String sql = "INSERT INTO products (title, price, img_url) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, product.getTitle(), product.getPrice(), product.getImgURL());
        } else {
            // Обновление существующего продукта
            String sql = "UPDATE products SET title = ?, price = ?, img_url = ? WHERE id = ?";
            jdbcTemplate.update(sql, product.getTitle(), product.getPrice(), product.getImgURL(), product.getId());
        }
    }


    // Получить все продукты с пагинацией
    public Page<Product> getAllProducts(Pageable pageable) {
        String countSql = "SELECT COUNT(*) FROM products";
        int total = jdbcTemplate.queryForObject(countSql, Integer.class);

        String sql = "SELECT * FROM products LIMIT ? OFFSET ?";
        List<Product> products = jdbcTemplate.query(sql, new Object[]{
                pageable.getPageSize(),
                pageable.getOffset()
        }, this::mapRowToProduct);

        return new PageImpl<>(products, pageable, total);
    }

    // Маппер для преобразования строки ResultSet в объект Product
    private Product mapRowToProduct(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setTitle(rs.getString("title"));
        product.setPrice(rs.getInt("price"));
        product.setImgURL(rs.getString("img_url")); // Маппинг нового поля
        return product;
    }

}
