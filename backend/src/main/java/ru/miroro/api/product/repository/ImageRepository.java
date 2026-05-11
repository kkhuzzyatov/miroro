package ru.miroro.api.product.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.miroro.api.product.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    // =====================================================
    // FIND
    // =====================================================

    List<Image> findByProductId(Integer productId);

    // =====================================================
    // DELETE
    // =====================================================

    @Modifying
    @Query("delete from Image i where i.product.id = :productId")
    void deleteByProductId(@Param("productId") Integer productId);
}
