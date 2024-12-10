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

        String deleteImagesSql = "DELETE FROM images WHERE product_id = ?";
        jdbcTemplate.update(deleteImagesSql, id);

        String deleteProductDetailsSql = "DELETE FROM ProductDetails WHERE product_id = ?";
        jdbcTemplate.update(deleteProductDetailsSql, id);

        String deleteProductSql = "DELETE FROM products WHERE id = ?";
        jdbcTemplate.update(deleteProductSql, id);

    }


    // Сохранить продукт (вставка или обновление)
    public Product save(Product product)  {
        Long productId = null;

        if (product.getId() == null) {
            String sql = "INSERT INTO products (title, price, img_url,category,quantity) VALUES (?, ?, ?, ?,?) RETURNING id";

            productId = jdbcTemplate.queryForObject(sql, Long.class, product.getTitle(), product.getPrice(), product.getImgURL(), product.getCategory(), product.getQuantity());
            product.setId(productId);
        } else {
            String sql = "UPDATE products SET title = ?, price = ?, img_url = ?, category = ?,quantity = ? WHERE id = ?";
            jdbcTemplate.update(sql, product.getTitle(), product.getPrice(), product.getImgURL(), product.getId(), product.getCategory(), product.getQuantity());
            productId = product.getId();
        }

        return product; // Возвращаем ID продукта
    }
    // Обновление существующего заказа
    public void update(Product product) {
        String sql = "UPDATE products SET title = ?, price = ?, img_url = ?, category = ?, quantity = ? WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(sql, product.getTitle(), product.getPrice(), product.getImgURL(), product.getCategory(),product.getQuantity(), product.getId());
        if (rowsAffected == 0) {
            throw new RuntimeException("Product with ID " + product.getId() + " not found.");
        }
    }


    public List<Product> findAll() {
        String sql = "SELECT * FROM products ORDER BY id";
        return jdbcTemplate.query(sql, this::mapRowToProduct);
    }

    // Получить все продукты с пагинацией
    public Page<Product> getAllProducts(Pageable pageable) {

        String countSql = "SELECT COUNT(*) FROM products";
        int total = jdbcTemplate.queryForObject(countSql, Integer.class);

        String sql = "SELECT * FROM products ORDER BY id ASC LIMIT ? OFFSET ?";

        List<Product> products = jdbcTemplate.query(sql, new Object[]{
                pageable.getPageSize(),
                pageable.getOffset()
        }, this::mapRowToProduct);
        return new PageImpl<>(products, pageable, total);
    }


    public Page<Product> findByTitle(String title, Pageable pageable) {

        int offset = pageable.getPageNumber() * pageable.getPageSize();
        int limit = pageable.getPageSize();

        String sql = "SELECT * FROM products WHERE LOWER(title) LIKE LOWER(?) LIMIT ? OFFSET ?";
        String searchTerm = "%" + title + "%";

        String countSql = "SELECT COUNT(*) FROM products WHERE LOWER(title) LIKE LOWER(?)";
        int total = jdbcTemplate.queryForObject(countSql, new Object[]{searchTerm}, Integer.class);
        List<Product> products = jdbcTemplate.query(sql, new Object[]{searchTerm, limit, offset}, this::mapRowToProduct);
        return new PageImpl<>(products, pageable, total);
    }

    public Page<Product> findByCategory(String category, Pageable pageable) {
        String query = "SELECT * FROM products WHERE category = ? LIMIT ? OFFSET ?";
        List<Product> products = jdbcTemplate.query(
                query,
                this::mapRowToProduct,
                category,
                pageable.getPageSize(),
                pageable.getOffset()
        );

        String countQuery = "SELECT COUNT(*) FROM products WHERE category = ?";
        int total = jdbcTemplate.queryForObject(countQuery, Integer.class, category);

        return new PageImpl<>(products, pageable, total);
    }

    public Page<Product> findByTitleContainingAndCategory(String title, String category, Pageable pageable) {
        String query = "SELECT * FROM products WHERE title LIKE ? AND category = ? LIMIT ? OFFSET ?";
        List<Product> products = jdbcTemplate.query(
                query,
                this::mapRowToProduct,
                "%" + title + "%",
                category,
                pageable.getPageSize(),
                pageable.getOffset()
        );

        String countQuery = "SELECT COUNT(*) FROM products WHERE title LIKE ? AND category = ?";
        int total = jdbcTemplate.queryForObject(countQuery, Integer.class, "%" + title + "%", category);

        return new PageImpl<>(products, pageable, total);
    }

    public List<String> findAllDistinctCategories() {
        String query = "SELECT DISTINCT category FROM products";
        return jdbcTemplate.query(query, (rs, rowNum) -> rs.getString("category"));
    }

    public List<Product> findProductsByOrderId(Long orderId) {
        String sql ="SELECT p.id, p.title, p.price, p.img_url, p.category, p.quantity FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?;";
        return jdbcTemplate.query(sql, new Object[]{orderId}, this::mapRowToProduct);
    }

    // Маппер для преобразования строки ResultSet в объект Product
    private Product mapRowToProduct(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setTitle(rs.getString("title"));
        product.setPrice(rs.getInt("price"));
        product.setImgURL(rs.getString("img_url"));
        product.setCategory(rs.getString("category"));
        product.setQuantity(rs.getInt("quantity"));
        return product;
    }

}
