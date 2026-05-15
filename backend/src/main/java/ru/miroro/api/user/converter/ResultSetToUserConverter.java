package ru.miroro.api.user.converter;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.stereotype.Component;
import ru.miroro.api.user.entity.User;

@Component
public class ResultSetToUserConverter {

    public User convert(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"), rs.getString("username"), rs.getString("password_hash"), rs.getString("role"));
    }
}
