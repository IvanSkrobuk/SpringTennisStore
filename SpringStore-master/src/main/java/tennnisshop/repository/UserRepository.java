
package tennnisshop.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import tennnisshop.entity.Authority;
import tennnisshop.entity.OrderItem;
import tennnisshop.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Найти пользователя по имени пользователя
    public Optional<User> findById(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new Object[]{username}, this::mapRowToUser);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    // Сохранить пользователя
    public void save(User user) {

            String sql = "INSERT INTO users (username, password, enabled) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.isEnabled());

            for (Authority authority : user.getAuthorities()) {
                String authoritySql = "INSERT INTO authorities (username, authority) VALUES (?, ?)";
                jdbcTemplate.update(authoritySql, user.getUsername(), authority.getAuthority());
            }

    }


    // Удалить пользователя по имени пользователя
    public void deleteById(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        jdbcTemplate.update(sql, username);

    }

    // Получить всех пользователей

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEnabled(rs.getBoolean("enabled"));

        String authoritySql = "SELECT * FROM authorities WHERE username = ?";
        List<Authority> authorities = jdbcTemplate.query(authoritySql, new Object[]{user.getUsername()}, (resultSet, rowNum1) -> {
            Authority authority = new Authority();
            authority.setAuthority(resultSet.getString("authority"));
            authority.setUser(user);
            return authority;
        });

        user.setAuthorities(authorities);

        return user;
    }

    public void updateStatus(String username, boolean enabled) {
        String sql = "UPDATE users SET enabled = ? WHERE username = ?";
        int rowsUpdated = jdbcTemplate.update(sql, enabled, username);

        if (rowsUpdated == 0) {
            throw new IllegalArgumentException("User with username '" + username + "' not found.");
        }
    }

}
