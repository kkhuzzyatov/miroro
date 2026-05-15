package ru.miroro.api.user.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.miroro.api.user.dto.UserDtoRequest;
import ru.miroro.api.user.entity.User;

@Component
public class UserDtoToUserConverter implements Converter<UserDtoRequest, User> {

    @Override
    public User convert(UserDtoRequest dto) {

        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(dto.getPassword());
        return user;
    }
}
