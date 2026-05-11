package ru.miroro.api.location.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.miroro.api.location.entity.City;

public interface CityRepository extends JpaRepository<City, String> {

    Optional<City> findByName(String name);
}
