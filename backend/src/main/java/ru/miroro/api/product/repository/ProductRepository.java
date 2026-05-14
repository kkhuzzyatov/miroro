package ru.miroro.api.product.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.miroro.api.product.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Override
    @EntityGraph(attributePaths = {"variants", "images"})
    List<Product> findAll();

    @EntityGraph(attributePaths = {"variants", "images"})
    List<Product> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = {"variants", "images"})
    Optional<Product> findById(Integer id);
}
