package ru.miroro.api.product.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "variant")
public class Variant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variant_id")
    private Integer variantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "size_id")
    private Integer sizeId;

    @Column(name = "color_id")
    private Integer colorId;

    private Integer quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variant)) return false;
        return variantId != null && variantId.equals(((Variant) o).variantId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
