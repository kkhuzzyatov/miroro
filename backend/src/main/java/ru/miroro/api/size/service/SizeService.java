package ru.miroro.api.size.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.miroro.api.size.model.Size;
import ru.miroro.api.size.repository.SizeRepository;

@RequiredArgsConstructor
@Service
@Transactional
public class SizeService {

    private final SizeRepository sizeRepository;

    // ============================
    // FIND ALL
    // ============================

    @Transactional(readOnly = true)
    public List<Size> findAll() {
        return sizeRepository.findAll();
    }

    // ============================
    // FIND BY ID
    // ============================

    @Transactional(readOnly = true)
    public Size findById(int id) {
        return sizeRepository.findById(id).orElse(null);
    }

    // ============================
    // CREATE
    // ============================

    public Size create(Size size) {

        size.setId(null);

        return sizeRepository.save(size);
    }

    // ============================
    // UPDATE
    // ============================

    public boolean update(int id, Size size) {

        Size existing = sizeRepository.findById(id).orElse(null);

        if (existing == null) {
            return false;
        }

        existing.setName(size.getName());

        sizeRepository.save(existing);

        return true;
    }

    // ============================
    // DELETE
    // ============================

    public boolean deleteById(int id) {

        if (!sizeRepository.existsById(id)) {
            return false;
        }

        sizeRepository.deleteById(id);

        return true;
    }
}
