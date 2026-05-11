package ru.miroro.api.user.repository;

import java.util.List;
import java.util.Optional;
import ru.miroro.api.user.entity.User;

public interface UserRepository {

    void save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    int update(User user);

    int deleteById(Long id);

    int deleteByUsername(String username);
}
