package ru.miroro.api.session.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.miroro.api.session.model.Session;
import ru.miroro.api.session.repository.SessionRepository;
import ru.miroro.api.user.entity.User;
import ru.miroro.api.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
@Transactional
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    public void cleanupExpiredSessions() {
        sessionRepository.deleteExpired(LocalDateTime.now());
    }

    public Session login(String username, String rawPassword) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("message: Неверный username или пароль"));

        if (!rawPassword.equals(user.getPasswordHash())) {
            throw new IllegalArgumentException("message: Неверный username или пароль");
        }

        Session session = Session.builder()
                .username(user.getUsername())
                .role(user.getRole())
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusWeeks(1))
                .build();

        return sessionRepository.save(session);
    }

    public void logout(String token) {

        if (token == null || token.isBlank()) {
            return;
        }

        sessionRepository.deleteByToken(token);
    }

    @Transactional(readOnly = true)
    public Optional<Session> getSessionByToken(String token) {

        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return sessionRepository.findByToken(token).filter(s -> s.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Transactional(readOnly = true)
    public boolean isValid(String token) {

        return sessionRepository
                .findByToken(token)
                .map(s -> s.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElse(false);
    }
}
