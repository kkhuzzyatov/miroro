package ru.miroro.api.purchase.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "purchase_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_status_id")
    private Integer id;

    private String name;
}
