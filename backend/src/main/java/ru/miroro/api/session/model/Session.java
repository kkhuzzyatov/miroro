package ru.miroro.api.session.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

    private Long sessionId;
    private String email;
    private String role;
    private String token;
    private LocalDateTime expiresAt;
}
