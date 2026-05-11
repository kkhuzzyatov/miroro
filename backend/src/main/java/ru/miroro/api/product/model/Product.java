package ru.miroro.api.product.model;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer id;

    private String name;

    private String description;

    @Column(name = "current_price")
    private Integer price;

    @Column(name = "segment_id")
    private Integer segmentId;

    @OneToMany(mappedBy = "product", orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Variant> variants = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private Set<Image> images = new LinkedHashSet<>();
}
