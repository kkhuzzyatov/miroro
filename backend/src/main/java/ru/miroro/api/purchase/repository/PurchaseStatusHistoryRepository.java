package ru.miroro.api.purchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.miroro.api.purchase.entity.PurchaseStatusHistoryEntity;

public interface PurchaseStatusHistoryRepository extends JpaRepository<PurchaseStatusHistoryEntity, Integer> {}
