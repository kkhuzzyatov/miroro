package ru.miroro.integration.cdek.access_token;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Service
public class CdekTokenService {
    private final RestTemplate restTemplate;

    @Value("${cdek.auth.url}")
    private String authUrl;

    @Value("${cdek.auth.client-id}")
    private String clientId;

    @Value("${cdek.auth.client-secret}")
    private String clientSecret;

    private String accessToken;
    private Instant expirationTime;

    private final ReentrantLock lock = new ReentrantLock();

    @PostConstruct
    public void init() {
        refreshToken();
    }

    @Scheduled(fixedDelay = 55 * 60 * 1000)
    public void scheduledRefresh() {
        refreshToken();
    }

    public String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(expirationTime.minusSeconds(60))) {
            refreshToken();
        }
        return accessToken;
    }

    private void refreshToken() {
        lock.lock();
        try {
            log.info("Refreshing CDEK OAuth token...");

            String url = String.format(
                    "%s/oauth/token?grant_type=client_credentials&client_id=%s&client_secret=%s",
                    authUrl, clientId, clientSecret);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            try {
                ResponseEntity<CdekTokenResponse> response =
                        restTemplate.exchange(url, HttpMethod.POST, request, CdekTokenResponse.class);

                CdekTokenResponse body = response.getBody();

                if (body == null || body.getAccessToken() == null) {
                    throw new IllegalStateException("Failed to obtain CDEK token");
                }

                this.accessToken = body.getAccessToken();
                this.expirationTime = Instant.now().plusSeconds(body.getExpiresIn());

                log.info("CDEK token successfully refreshed. Expires at: {}", expirationTime);
            } catch (Exception e) {
                log.error("Failed to refresh CDEK token", e);
            }
        } finally {
            lock.unlock();
        }
    }
}
