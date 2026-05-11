package ru.miroro.api.color.repository;

import java.util.List;
import ru.miroro.api.color.model.Color;

public interface ColorRepository {

    List<Color> findAll();

    Color findById(int id);

    Color findByName(String name);

    void save(Color color);

    int update(Color color);

    int deleteById(int id);

    int deleteByName(String name);

    Integer findIdByName(String name);
}
