package ru.miroro.api.user.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.miroro.api.user.dto.UserDtoRequest;
import ru.miroro.api.user.entity.User;
import ru.miroro.api.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    // ============================
    // FIND BY ID
    // ============================
    @Transactional(readOnly = true)
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    // ============================
    // FIND BY USERNAME
    // ============================
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // ============================
    // FIND ALL
    // ============================
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // ============================
    // CREATE USER
    // ============================
    public User create(UserDtoRequest dto) {

        User user = User.builder()
                .username(dto.getUsername())
                .passwordHash(hashPassword(dto.getPassword()))
                .role("customer")
                .build();

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("message: Этот username уже занят");
        }
    }

    // ============================
    // UPDATE USER
    // ============================
    public User update(Integer userId, UserDtoRequest dto) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("message: Пользователь не найден"));

        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            user.setUsername(dto.getUsername());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPasswordHash(hashPassword(dto.getPassword()));
        }

        return userRepository.save(user);
    }

    // ============================
    // DELETE BY USERNAME
    // ============================
    public void deleteByUsername(String username) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("message: Пользователь не найден"));

        userRepository.delete(user);
    }

    // ============================
    // PASSWORD HASH (stub)
    // ============================
    private String hashPassword(String password) {
        // TODO: заменить на BCryptPasswordEncoder
        return password;
    }
}
