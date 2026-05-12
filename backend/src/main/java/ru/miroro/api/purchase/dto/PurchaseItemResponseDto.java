package ru.miroro.api.purchase.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PurchaseItemResponseDto {

    private Integer id;

    private String productName;

    private String sizeName;

    private String colorName;

    private Integer price;
}
