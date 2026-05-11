package ru.miroro.api.user.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.miroro.api.user.dto.UserDtoRequest;
import ru.miroro.api.user.entity.User;
import ru.miroro.api.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {

    private static final int BCRYPT_COST = 12;

    private final UserRepository userRepository;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void create(UserDtoRequest dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(dto.getPassword());
        user.setRole("customer");

        userRepository.save(user);
    }

    public User update(Long userId, UserDtoRequest dto) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NoSuchElementException("message: Пользователь не найден"));

        if (dto.getPassword() != null) {
            user.setPasswordHash(hashPassword(dto.getPassword()));
        }

        userRepository.update(user);
        return user;
    }

    public void deleteByUsername(String username) {
        userRepository
                .findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("message: Пользователь не найден"));
        userRepository.deleteByUsername(username);
    }

    private String hashPassword(String password) {
        return password;
    }
}
