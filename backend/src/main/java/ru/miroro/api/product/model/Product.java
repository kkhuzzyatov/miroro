package ru.miroro.api.product.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Integer id;
    private String name;
    private String description;
    private Integer price;

    @JsonProperty("segment_id")
    private Integer segmentId;

    private List<Variant> variants;
    private List<Image> images;
}
