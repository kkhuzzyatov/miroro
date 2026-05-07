package ru.miroro.api.user.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
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
        insert into users (name, email, password_hash, role)
        values (?, ?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                    ps.setString(1, user.getName());
                    ps.setString(2, user.getEmail());
                    ps.setString(3, user.getPasswordHash());
                    ps.setString(4, user.getRole());

                    return ps;
                },
                keyHolder);
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

    public Optional<User> findByEmail(String email) {
        String sql = """
                select *
                from users
                where email = ?
                """;

        List<User> users = jdbcTemplate.query(sql, userMapper, email);
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
                set name = ?,
                    email = ?,
                    password_hash = ?,
                    role = ?,
                    address_id = ?
                where user_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                user.getAddressId(),
                user.getId());
    }

    public int deleteById(Long id) {
        String sql = """
                delete from users
                where user_id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }

    public int deleteByEmail(String email) {
        String sql = """
                delete from users
                where email = ?
                """;

        return jdbcTemplate.update(sql, email);
    }
}
