package ru.miroro.api.product.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.miroro.api.product.model.Image;

@Component
public class ImageRowMapper implements RowMapper<Image> {

    @Override
    public Image mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Image.builder()
                .path(rs.getString("path"))
                .isMain(rs.getBoolean("is_main"))
                .colorId(rs.getObject("color_id", Integer.class))
                .build();
    }
}
