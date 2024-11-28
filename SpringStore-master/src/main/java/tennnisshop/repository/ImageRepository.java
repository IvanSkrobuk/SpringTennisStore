package tennnisshop.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import tennnisshop.entity.Image;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ImageRepository {

    private final JdbcTemplate jdbcTemplate;

    public ImageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Сохранение нового изображения
    public void save(Image image) {
        String sql = "INSERT INTO images (name, file_name, size, file_type, bytes, product_id) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, image.getName(), image.getFileName(), image.getSize(),
                image.getFileType(), image.getBytes(), image.getProductId());
    }

    // Получение всех изображений
    public List<Image> findAll() {
        String sql = "SELECT * FROM images";
        return jdbcTemplate.query(sql, new ImageRowMapper());
    }

    // Получение изображения по ID
    public Image findById(Long id) {
        String sql = "SELECT * FROM images WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new ImageRowMapper(), id);
    }

    // Удаление изображения по ID
    public void deleteById(Long id) {
        String sql = "DELETE FROM images WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    public void deleteByProductId(Long id) {
        String sql = "DELETE FROM images WHERE product_id = ?";
        jdbcTemplate.update(sql, id);
    }

    // Получение изображений по ID продукта
    public List<Image> getImageByProductId(Long productId) {
        String sql = "SELECT * FROM images WHERE product_id = ?";
        return jdbcTemplate.query(sql, new ImageRowMapper(), productId);
    }
    public Image getFirstImageByProductId(Long productId) {
        String sql = "SELECT * FROM images WHERE product_id = ? LIMIT 1";
        return jdbcTemplate.queryForObject(sql, new ImageRowMapper(), productId);
    }


    // Маппер для сущности Image
    private static class ImageRowMapper implements RowMapper<Image> {
        @Override
        public Image mapRow(ResultSet rs, int rowNum) throws SQLException {
            Image image = new Image();
            image.setId(rs.getLong("id"));
            image.setProductId(rs.getLong("id"));
            image.setName(rs.getString("name"));
            image.setFileName(rs.getString("file_name"));
            image.setSize(rs.getLong("size"));
            image.setFileType(rs.getString("file_type"));
            image.setBytes(rs.getBytes("bytes"));
            // Связываем изображение с продуктом через ID (если нужно, можно использовать отдельный запрос для загрузки продукта)
            return image;
        }
    }
}
