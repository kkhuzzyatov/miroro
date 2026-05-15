package ru.miroro.api.user.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.miroro.api.user.dto.UserDtoRequest;
import ru.miroro.api.user.entity.User;

@Component
public class UserToUserDtoConverter implements Converter<User, UserDtoRequest> {

    @Override
    public UserDtoRequest convert(User user) {

        if (user == null) {
            return null;
        }

        UserDtoRequest dto = new UserDtoRequest();
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPasswordHash());
        return dto;
    }
}
