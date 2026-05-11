package ru.miroro.api.size.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.miroro.api.size.model.Size;
import ru.miroro.api.size.repository.SizeRepository;

@RequiredArgsConstructor
@Service
public class SizeService {

    private final SizeRepository sizeRepository;

    public Size create(Size size) {
        sizeRepository.save(size);

        Integer id = sizeRepository.findIdByName(size.getName());
        size.setId(id);

        return size;
    }
}
