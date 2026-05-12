package ru.miroro.api.purchase.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PurchaseResponseDto {

    private Integer purchaseId;

    private String userEmail;

    private String status;

    private String targetAddress;

    private List<PurchaseItemResponseDto> purchaseItems;
}
