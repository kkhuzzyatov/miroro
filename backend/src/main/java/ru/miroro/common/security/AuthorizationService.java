package ru.miroro.common.security;

import org.springframework.stereotype.Component;
import ru.miroro.api.session.model.Session;
import ru.miroro.api.session.service.SessionService;

@Component
public class AuthorizationService {

    private final SessionService sessionService;

    public AuthorizationService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public Session checkAdmin(String token) {
        Session session =
                sessionService.getSessionByToken(token).orElseThrow(() -> new SecurityException("Не авторизован"));

        if (!"admin".equals(session.getRole())) {
            throw new SecurityException("Доступ запрещён");
        }

        return session;
    }

    public Session checkAuthorized(String token) {
        Session session =
                sessionService.getSessionByToken(token).orElseThrow(() -> new SecurityException("Не авторизован"));

        return session;
    }
}
