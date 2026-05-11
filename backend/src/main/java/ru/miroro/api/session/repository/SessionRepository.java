package ru.miroro.api.session.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.miroro.api.session.model.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByToken(String token);

    boolean existsByToken(String token);

    void deleteByToken(String token);

    @Modifying
    @Query("delete from Session s where s.expiresAt < :now")
    void deleteExpired(LocalDateTime now);
}
