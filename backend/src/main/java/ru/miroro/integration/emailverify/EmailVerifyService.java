package ru.miroro.integration.emailverify;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EmailVerifyService {

    private final EmailVerifyProperties properties;
    private final RestTemplate restTemplate;

    public EmailVerifyService(EmailVerifyProperties properties) {
        this.properties = properties;
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> validateEmail(String email) {
        String url = UriComponentsBuilder.fromUriString("https://app.emailverify.io/api/v1/validate")
                .queryParam("key", properties.getKey())
                .queryParam("email", email)
                .toUriString();

        return restTemplate.getForObject(url, Map.class);
    }
}
