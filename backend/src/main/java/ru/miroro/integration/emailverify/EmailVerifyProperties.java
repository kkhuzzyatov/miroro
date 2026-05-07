package ru.miroro.integration.emailverify;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "emailverify")
public class EmailVerifyProperties {
    private String key;
}
