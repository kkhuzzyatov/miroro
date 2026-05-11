package ru.miroro.api.color.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.miroro.api.color.model.Color;
import ru.miroro.api.color.repository.ColorRepository;

@RequiredArgsConstructor
@Service
@Transactional
public class ColorService {

    private final ColorRepository colorRepository;

    public Color create(Color color) {
        return colorRepository.save(color);
    }

    public Color update(int id, Color color) {

        Color existing =
                colorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("message: Цвет не найден"));

        existing.setName(color.getName());
        existing.setHex(color.getHex());

        return colorRepository.save(existing);
    }

    public void deleteById(int id) {

        if (!colorRepository.existsById(id)) {
            throw new EntityNotFoundException("message: Цвет не найден");
        }

        colorRepository.deleteById(id);
    }
}
