package ru.miroro.integration.cdek.deliverypoints;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CdekDeliveryPoint {
    private String code;
    private String name;
    private String uuid;

    @JsonProperty("address_comment")
    private String addressComment;

    private String workTime;
    private String email;
    private String type;
    private boolean takeOnly;
    private boolean isHandout;
    private boolean isReception;
    private boolean isDressingRoom;
    private boolean isLtl;
    private boolean haveCashless;
    private boolean haveCash;
    private boolean haveFastPaymentSystem;
    private boolean allowedCod;

    private CdekLocation location;

    @Data
    public static class CdekLocation {
        @JsonProperty("country_code")
        private String countryCode;

        private String region;
        private String city;
        private String address;

        @JsonProperty("address_full")
        private String addressFull;
    }
}
