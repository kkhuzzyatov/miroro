package ru.miroro.api.product.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Игнорировать null поля при сериализации
public class Image {
    private String path;

    @JsonProperty("color_id")
    private Integer colorId;

    @JsonProperty("is_main")
    private Boolean isMain;
}
