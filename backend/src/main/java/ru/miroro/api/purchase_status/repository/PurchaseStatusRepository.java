package ru.miroro.api.purchase_status.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.miroro.api.purchase_status.model.PurchaseStatus;

public interface PurchaseStatusRepository extends JpaRepository<PurchaseStatus, Integer> {}
