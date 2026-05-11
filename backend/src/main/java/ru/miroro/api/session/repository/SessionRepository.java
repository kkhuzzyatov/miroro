package ru.miroro.api.session.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.miroro.api.session.mapper.SessionRowMapper;
import ru.miroro.api.session.model.Session;

@RequiredArgsConstructor
@Repository
public class SessionRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SessionRowMapper rowMapper = new SessionRowMapper();

    public void save(Session session) {
        String sql = """
            INSERT INTO session (username, role, token, expires_at)
            VALUES (?, ?, ?, ?)
            """;
        jdbcTemplate.update(sql, session.getUsername(), session.getRole(), session.getToken(), session.getExpiresAt());
    }

    public Optional<Session> findByToken(String token) {
        String sql = """
            SELECT session_id, username, role, token, expires_at
            FROM session
            WHERE token = ?
            """;
        List<Session> sessions = jdbcTemplate.query(sql, rowMapper, token);
        return sessions.stream().findFirst();
    }

    public boolean existsByToken(String token) {
        String sql = """
            SELECT EXISTS(
                SELECT 1
                FROM session
                WHERE token = ?
            )
            """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, token));
    }

    public void deleteByToken(String token) {
        String sql = "DELETE FROM session WHERE token = ?";
        jdbcTemplate.update(sql, token);
    }

    public void deleteExpired(LocalDateTime now) {
        String sql = "DELETE FROM session WHERE expires_at < ?";
        jdbcTemplate.update(sql, now);
    }
}
