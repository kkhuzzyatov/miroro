package ru.miroro.api.user.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.miroro.api.user.dto.UserDtoRequest;
import ru.miroro.api.user.entity.User;
import ru.miroro.api.user.repository.UserRepository;
import ru.miroro.integration.emailverify.EmailVerifyService;

@RequiredArgsConstructor
@Service
public class UserService {

    private static final int BCRYPT_COST = 12;

    private final UserRepository userRepository;
    private final EmailVerifyService emailVerifyService;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void create(UserDtoRequest dto) {
        validateEmail(dto.getEmail());

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPassword());
        user.setRole("customer");
        user.setAddressId(dto.getAddressId());

        userRepository.save(user);
    }

    public User update(Long userId, UserDtoRequest dto) {

        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        if (dto.getPassword() != null) {
            user.setPasswordHash(hashPassword(dto.getPassword()));
        }

        if (dto.getAddressId() != null) {
            user.setAddressId(dto.getAddressId());
        }

        userRepository.update(user);
        return user;
    }

    public void deleteByEmail(String email) {
        userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("User not found"));
        userRepository.deleteByEmail(email);
    }

    private void validateEmail(String email) {
        Map<String, Object> emailCheck = emailVerifyService.validateEmail(email);
        if (!"valid".equals(emailCheck.get("status"))) {
            throw new EntityNotFoundException("Email is not valid: " + emailCheck.get("reason"));
        }
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray());
    }
}
