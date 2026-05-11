package ru.miroro.api.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantDto {

    @JsonProperty("variant_id")
    private Integer variantId;

    @JsonProperty("size_id")
    private Integer sizeId;

    @JsonProperty("color_id")
    private Integer colorId;

    private Integer quantity;
}
