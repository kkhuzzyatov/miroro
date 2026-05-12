package ru.miroro.api.purchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.miroro.api.purchase.entity.PurchaseItemEntity;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItemEntity, Integer> {}
