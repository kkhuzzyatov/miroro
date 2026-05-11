package ru.miroro.api.size.repository;

import java.util.List;
import ru.miroro.api.size.model.Size;

public interface SizeRepository {

    List<Size> findAll();

    Size findById(int sizeId);

    Size findByName(String name);

    void save(Size size);

    int update(Size size);

    int deleteById(int sizeId);

    int deleteByName(String name);

    Integer findIdByName(String name);
}
