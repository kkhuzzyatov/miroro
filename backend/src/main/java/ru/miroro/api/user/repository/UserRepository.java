package ru.miroro.api.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.miroro.api.user.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    void deleteByUsername(String username);
}
