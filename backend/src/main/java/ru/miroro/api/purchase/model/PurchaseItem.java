package ru.miroro.api.purchase.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItem {

    private Integer id;
    private String productName;
    private String sizeName;
    private String colorName;
    private BigDecimal price;
}
