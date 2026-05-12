package ru.miroro.api.purchase.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.miroro.api.purchase.entity.PurchaseEntity;

public interface PurchaseRepository extends JpaRepository<PurchaseEntity, Integer> {

    @Query("""
        select distinct p
        from PurchaseEntity p
        left join fetch p.items i
        left join fetch i.productItem pi
        left join fetch pi.variant v
        left join fetch v.product
        left join fetch v.size
        left join fetch v.color
    """)
    List<PurchaseEntity> findAllFull();

    @Query("""
        select distinct p
        from PurchaseEntity p
        left join fetch p.items i
        left join fetch i.productItem pi
        left join fetch pi.variant v
        left join fetch v.product
        left join fetch v.size
        left join fetch v.color
        where p.userId = :userId
    """)
    List<PurchaseEntity> findByUserIdFull(Integer userId);
}
