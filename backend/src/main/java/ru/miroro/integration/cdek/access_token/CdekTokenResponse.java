package ru.miroro.integration.cdek.access_token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CdekTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private long expiresIn;

    private String scope;
    private String jti;
}
