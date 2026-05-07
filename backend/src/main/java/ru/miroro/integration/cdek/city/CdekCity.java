package ru.miroro.integration.cdek.city;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CdekCity {
    @JsonProperty("city_uuid")
    private String cityUuid;

    private long code;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("country_code")
    private String countryCode;
}
