package ru.miroro.api.session.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.miroro.api.session.model.SessionData;
import ru.miroro.api.user.entity.User;
import ru.miroro.api.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
@Transactional
public class SessionService {

    private final UserRepository userRepository;
    private final SessionStorageService sessionStorageService;

    public String login(String username, String rawPassword) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("message: Неверный username или пароль"));

        if (!rawPassword.equals(user.getPasswordHash())) {
            throw new IllegalArgumentException("message: Неверный username или пароль");
        }

        String token = UUID.randomUUID().toString();

        SessionData sessionData = SessionData.builder()
                .username(user.getUsername())
                .role(user.getRole())
                .build();

        sessionStorageService.save(token, sessionData);

        return token;
    }

    @Transactional(readOnly = true)
    public boolean isValid(String token) {

        if (token == null || token.isBlank()) {
            return false;
        }

        return sessionStorageService.exists(token);
    }

    public void logout(String token) {

        if (token == null || token.isBlank()) {
            return;
        }

        sessionStorageService.delete(token);
    }

    @Transactional(readOnly = true)
    public SessionData getSessionByToken(String token) {

        if (token == null || token.isBlank()) {
            return null;
        }

        return sessionStorageService.get(token);
    }
}
