package ru.miroro.api.product_item.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductItemRequest {
    private Integer variantId;
}
