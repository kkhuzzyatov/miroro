package ru.miroro.api.location.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.miroro.api.location.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    Optional<Address> findByAddress(String address);
}
