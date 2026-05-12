package ru.miroro.api.product_item.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_item_id")
    private Integer id;

    @Column(name = "variant_id")
    private Integer variantId;

    @Column(name = "is_sold")
    private Boolean isSold;
}
