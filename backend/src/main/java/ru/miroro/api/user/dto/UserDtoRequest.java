package ru.miroro.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoRequest {
    private String name;
    private String email;
    private String password;
    private Integer addressId;
}
