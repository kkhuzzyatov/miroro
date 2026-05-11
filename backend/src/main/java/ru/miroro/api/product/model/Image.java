package ru.miroro.api.product.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private String path;

    @Column(name = "color_id")
    private Integer colorId;

    @Column(name = "is_main")
    private Boolean isMain;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Image)) return false;

        Image image = (Image) o;

        return imageId != null && imageId.equals(image.imageId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
