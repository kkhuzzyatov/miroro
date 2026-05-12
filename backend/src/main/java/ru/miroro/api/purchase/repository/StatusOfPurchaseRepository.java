package ru.miroro.api.purchase.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.miroro.api.purchase.entity.PurchaseStatusEntity;

public interface StatusOfPurchaseRepository extends JpaRepository<PurchaseStatusEntity, Integer> {

    Optional<PurchaseStatusEntity> findByName(String name);
}
