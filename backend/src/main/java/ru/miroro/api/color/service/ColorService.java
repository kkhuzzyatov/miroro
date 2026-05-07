package ru.miroro.api.color.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.miroro.api.color.model.Color;
import ru.miroro.api.color.repository.ColorRepository;

@Service
@RequiredArgsConstructor
public class ColorService {

    private final ColorRepository colorRepository;

    public Color create(Color color) {
        colorRepository.save(color);

        Integer id = colorRepository.findIdByName(color.getName());

        color.setId(id);

        return color;
    }
}
