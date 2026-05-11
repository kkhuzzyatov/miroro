package ru.miroro.api.size.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.miroro.api.size.model.Size;

public interface SizeRepository extends JpaRepository<Size, Integer> {

    Optional<Size> findByName(String name);

    void deleteByName(String name);

    boolean existsByName(String name);
}
