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
public class ImageDto {

    @JsonProperty("image_id")
    private Integer imageId;

    @JsonProperty("color_id")
    private Integer colorId;

    private String path;

    @JsonProperty("is_main")
    private boolean isMain;
}
