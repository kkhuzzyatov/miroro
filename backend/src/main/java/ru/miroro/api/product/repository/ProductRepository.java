package ru.miroro.api.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miroro.api.product.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {}
