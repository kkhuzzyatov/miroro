package ru.miroro.api.location.repository;

import ru.miroro.api.location.entity.City;

public interface CityRepository {

    City findByName(String name);

    void save(City city);
}
