package ru.miroro.api.session.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import ru.miroro.api.session.model.SessionData;

@RequiredArgsConstructor
@Service
public class SessionStorageService {

    private static final String PREFIX = "session:";
    private static final Duration TTL = Duration.ofDays(7);

    private final StringRedisTemplate redisTemplate;

    public void save(String token, SessionData sessionData) {

        String value = sessionData.getUsername() + ":" + sessionData.getRole();

        redisTemplate.opsForValue().set(PREFIX + token, value, TTL);
    }

    public SessionData get(String token) {

        String value = redisTemplate.opsForValue().get(PREFIX + token);

        if (value == null) {
            return null;
        }

        String[] parts = value.split(":");

        return SessionData.builder().username(parts[0]).role(parts[1]).build();
    }

    public boolean exists(String token) {

        Boolean exists = redisTemplate.hasKey(PREFIX + token);

        return Boolean.TRUE.equals(exists);
    }

    public void delete(String token) {

        redisTemplate.delete(PREFIX + token);
    }
}
