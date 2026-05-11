package ru.miroro.api.color.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.miroro.api.color.model.Color;

public interface ColorRepository extends JpaRepository<Color, Integer> {

    Optional<Color> findByName(String name);

    Integer findIdByName(String name);

    int deleteByName(String name);
}
