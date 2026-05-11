package ru.miroro.api.location.repository;

import ru.miroro.api.location.entity.Address;

public interface AddressRepository {
    Address findByName(String name);

    int save(String name, String cityUuid);
}
