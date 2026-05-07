package ru.miroro.api.product_item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
