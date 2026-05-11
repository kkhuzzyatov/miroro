package ru.miroro.api.user.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.miroro.api.user.entity.User;
import ru.miroro.api.user.mapper.UserMapper;

@RequiredArgsConstructor
@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    public void save(User user) {
        String sql = """
        insert into users (username, password_hash, role)
        values (?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                        ps.setString(1, user.getUsername());
                        ps.setString(2, user.getPasswordHash());
                        ps.setString(3, user.getRole());

                        return ps;
                    },
                    keyHolder);
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("Этот username уже занят");
        }
    }

    public Optional<User> findById(Long id) {
        String sql = """
                select *
                from users
                where user_id = ?
                """;

        List<User> users = jdbcTemplate.query(sql, userMapper, id);
        return users.stream().findFirst();
    }

    public Optional<User> findByUsername(String username) {
        String sql = """
                select *
                from users
                where username = ?
                """;

        List<User> users = jdbcTemplate.query(sql, userMapper, username);
        return users.stream().findFirst();
    }

    public List<User> findAll() {
        String sql = """
                select *
                from users
                """;

        return jdbcTemplate.query(sql, userMapper);
    }

    public int update(User user) {
        String sql = """
                update users
                set username = ?,
                    password_hash = ?,
                    role = ?
                where user_id = ?
                """;

        return jdbcTemplate.update(sql, user.getUsername(), user.getPasswordHash(), user.getRole(), user.getId());
    }

    public int deleteById(Long id) {
        String sql = """
                delete from users
                where user_id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }

    public int deleteByUsername(String username) {
        String sql = """
                delete from users
                where username = ?
                """;

        return jdbcTemplate.update(sql, username);
    }
}
