package ru.miroro.api.session.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import ru.miroro.api.session.model.Session;

public interface SessionRepository {

    void save(Session session);

    Optional<Session> findByToken(String token);

    boolean existsByToken(String token);

    void deleteByToken(String token);

    void deleteExpired(LocalDateTime now);
}
