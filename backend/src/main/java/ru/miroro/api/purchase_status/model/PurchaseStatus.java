package ru.miroro.api.purchase_status.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "purchase_status")
public class PurchaseStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_status_id")
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;
}
