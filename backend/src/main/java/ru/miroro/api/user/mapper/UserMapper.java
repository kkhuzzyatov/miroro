package ru.miroro.api.user.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import ru.miroro.api.user.entity.User;

public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer addressId = rs.getInt("address_id");
        if (rs.wasNull()) {
            addressId = null;
        }

        return new User(
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("role"),
                addressId);
    }
}
