
package tennnisshop.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import tennnisshop.entity.Authority;
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


    // Сохранить пользователя (вставка или обновление)
    public void save(User user) {
        if (user.getUsername() == null) {
            // Вставка нового пользователя
            String sql = "INSERT INTO users (username, password, enabled) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.isEnabled());

            // Сохранение списка authorities
            for (Authority authority : user.getAuthorities()) {
                String authoritySql = "INSERT INTO authorities (username, authority) VALUES (?, ?)";
                jdbcTemplate.update(authoritySql, user.getUsername(), authority.getAuthority());
            }
        } else {
            // Обновление существующего пользователя
            String sql = "UPDATE users SET password = ?, enabled = ? WHERE username = ?";
            jdbcTemplate.update(sql, user.getPassword(), user.isEnabled(), user.getUsername());

            String deleteAuthoritiesSql = "DELETE FROM authorities WHERE username = ?";
            jdbcTemplate.update(deleteAuthoritiesSql, user.getUsername());

            for (Authority authority : user.getAuthorities()) {
                String authoritySql = "INSERT INTO authorities (username, authority) VALUES (?, ?)";
                jdbcTemplate.update(authoritySql, user.getUsername(), authority.getAuthority());
            }
        }
    }

    // Удалить пользователя по имени пользователя
    public void deleteById(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        jdbcTemplate.update(sql, username);

    }

    // Получить всех пользователей
    public Page<User> findAll(Pageable pageable) {
        String sql = "SELECT * FROM users LIMIT ? OFFSET ?";
        List<User> users = jdbcTemplate.query(sql, new Object[]{pageable.getPageSize(), pageable.getOffset()}, this::mapRowToUser);
        String countSql = "SELECT COUNT(*) FROM users";
        int total = jdbcTemplate.queryForObject(countSql, Integer.class);
        return new PageImpl<>(users, pageable, total);
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

}
