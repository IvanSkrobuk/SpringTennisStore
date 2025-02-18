package tennnisshop.repository;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import tennnisshop.entity.ProductDetails;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProductDetailsRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductDetailsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Метод для маппинга строки из базы данных в объект ProductDetails
    private static class ProductDetailsRowMapper implements RowMapper<ProductDetails> {
        @Override
        public ProductDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            ProductDetails productDetails = new ProductDetails();
            productDetails.setId(rs.getLong("id"));
            productDetails.setProductId(rs.getLong("product_id"));
            productDetails.setProductName(rs.getString("product_name"));
            productDetails.setDescription(rs.getString("description"));
            productDetails.setBrand(rs.getString("brand"));
            productDetails.setPrice(rs.getInt("price"));
            productDetails.setWarrantyPeriod(rs.getInt("warranty_period"));
            return productDetails;
        }
    }

    // Сохранение ProductDetails
    public void save(ProductDetails productDetails) {
        String sql = "INSERT INTO ProductDetails (product_name, description, brand, price, warranty_period, product_id) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, productDetails.getProductName(), productDetails.getDescription(),
                productDetails.getBrand(), productDetails.getPrice(), productDetails.getWarrantyPeriod(), productDetails.getProductId());
    }

    // Получение всех значенинй
    public List<ProductDetails> findAll() {
        String sql = "SELECT * FROM ProductDetails";
        return jdbcTemplate.query(sql, new ProductDetailsRowMapper());
    }

    // Получение значения по ID
    public ProductDetails findById(Long id) {
        String sql = "SELECT * FROM ProductDetails WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new ProductDetailsRowMapper(), id);
    }

    // Получение деталей закза по ID заказа
    public ProductDetails findByProductId(Long productid) {
        String sql = "SELECT * FROM ProductDetails WHERE product_id = ?";
        return jdbcTemplate.queryForObject(sql, new ProductDetailsRowMapper(), productid);
    }

    // Обновление деталий товара
    public void update(ProductDetails productDetails) {
        String sql = "UPDATE ProductDetails SET product_name = ?, description = ?, brand = ?, price = ?, warranty_period = ? WHERE id = ?";
        jdbcTemplate.update(sql, productDetails.getProductName(), productDetails.getDescription(),
                productDetails.getBrand(), productDetails.getPrice(), productDetails.getWarrantyPeriod(), productDetails.getId());
    }

    // Удалить товар по id
    public void deleteById(Long id) {
        String sql = "DELETE FROM ProductDetails WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

}
