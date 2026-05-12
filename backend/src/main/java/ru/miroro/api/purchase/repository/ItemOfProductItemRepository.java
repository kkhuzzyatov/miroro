package ru.miroro.api.purchase.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import ru.miroro.api.purchase.entity.ProductItemEntity;

public interface ItemOfProductItemRepository extends JpaRepository<ProductItemEntity, Integer> {

    List<ProductItemEntity> findByVariant_IdAndIsSoldFalse(Integer variantId);

    @Modifying
    @Query("update ProductItemEntity p set p.isSold = true where p.id in :ids")
    void markSold(@Param("ids") List<Integer> ids);
}
