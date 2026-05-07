package ru.miroro.api.session.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.miroro.api.session.model.Session;
import ru.miroro.api.session.repository.SessionRepository;
import ru.miroro.api.user.entity.User;
import ru.miroro.api.user.repository.UserRepository;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public SessionService(SessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24) // каждые 24 часа
    public void cleanupExpiredSessions() {
        sessionRepository.deleteExpired(LocalDateTime.now());
    }

    public Session login(String email, String rawPassword) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Неверная почта или пароль"));

        boolean passwordValid = rawPassword.equals(user.getPasswordHash());

        if (!passwordValid) {
            throw new IllegalArgumentException("Неверная почта или пароль");
        }

        Session session = Session.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusWeeks(1))
                .build();

        sessionRepository.save(session);
        return session;
    }

    public void logout(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        sessionRepository.deleteByToken(token);
    }

    public Optional<Session> getSessionByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return sessionRepository.findByToken(token).filter(session -> session.getExpiresAt()
                .isAfter(LocalDateTime.now()));
    }

    public boolean isValid(String token) {
        return sessionRepository.existsByToken(token);
    }
}
