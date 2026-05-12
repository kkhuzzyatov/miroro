package ru.miroro.api.purchase.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "purchase_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseStatusHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_status_history_id")
    private Integer id;

    @Column(name = "purchase_id")
    private Integer purchaseId;

    @Column(name = "previous_status_id")
    private Integer previousStatusId;

    private LocalDateTime changedAt;
}
