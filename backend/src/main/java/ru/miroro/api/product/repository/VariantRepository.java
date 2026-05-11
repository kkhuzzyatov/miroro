package ru.miroro.api.product.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import ru.miroro.api.product.model.Variant;

public interface VariantRepository extends JpaRepository<Variant, Integer> {

    List<Variant> findByProductId(Integer productId);

    @Modifying
    @Query("delete from Variant v where v.product.id = :productId")
    void deleteByProductId(@Param("productId") Integer productId);
}
