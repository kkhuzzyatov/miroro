package ru.miroro.api.session.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import ru.miroro.api.session.model.Session;

public class SessionRowMapper implements RowMapper<Session> {

    @Override
    public Session mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Session.builder()
                .sessionId(rs.getLong("session_id"))
                .email(rs.getString("email"))
                .role(rs.getString("role"))
                .token(rs.getString("token"))
                .expiresAt(rs.getTimestamp("expires_at").toLocalDateTime())
                .build();
    }
}
