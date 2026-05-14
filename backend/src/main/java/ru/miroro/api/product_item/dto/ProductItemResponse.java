package ru.miroro.api.product_item.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"productItemId", "productName", "sizeName", "colorName", "colorHex", "isSold"})
public class ProductItemResponse {

    private Integer productItemId;
    private String productName;
    private String sizeName;
    private String colorName;
    private String colorHex;
    private Boolean isSold;
}
