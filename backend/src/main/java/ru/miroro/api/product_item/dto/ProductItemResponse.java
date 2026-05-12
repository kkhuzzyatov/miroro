package ru.miroro.api.product_item.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemResponse {
    private Integer productItemId;
    private String productName;
    private String sizeName;
    private String colorName;
    private String colorHex;
    private Boolean isSold;
}
