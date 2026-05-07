package ru.miroro.api.product.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Variant {
    @JsonProperty("variant_id")
    private Integer variantId;

    @JsonProperty("size_id")
    private Integer sizeId;

    @JsonProperty("color_id")
    private Integer colorId;

    @JsonProperty("quantity")
    private Integer quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variant variant = (Variant) o;
        return Objects.equals(sizeId, variant.sizeId) && Objects.equals(colorId, variant.colorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sizeId, colorId);
    }
}
