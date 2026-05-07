package ru.miroro.api.purchase.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {

    private Integer purchaseId;
    private String userEmail;
    private String status;
    private String targetAddress;
    private List<PurchaseItem> purchaseItems;
}
