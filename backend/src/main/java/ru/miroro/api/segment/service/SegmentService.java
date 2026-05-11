package ru.miroro.api.segment.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.miroro.api.segment.model.Segment;
import ru.miroro.api.segment.repository.SegmentRepository;

@RequiredArgsConstructor
@Service
@Transactional
public class SegmentService {

    private final SegmentRepository segmentRepository;

    public Segment create(Segment segment) {

        if (segmentRepository.existsByName(segment.getName())) {
            throw new IllegalArgumentException("message: Сегмент уже существует");
        }

        return segmentRepository.save(segment);
    }

    @Transactional(readOnly = true)
    public List<Segment> findAll() {
        return segmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Segment findById(int id) {
        return segmentRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("message: Сегмент не найден"));
    }

    @Transactional(readOnly = true)
    public Segment findByName(String name) {
        return segmentRepository
                .findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("message: Сегмент не найден"));
    }

    public Segment update(int id, Segment request) {

        Segment segment = segmentRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("message: Сегмент не найден"));

        segment.setName(request.getName());

        return segmentRepository.save(segment);
    }

    public void deleteById(int id) {

        if (!segmentRepository.existsById(id)) {
            throw new IllegalArgumentException("message: Сегмент не найден");
        }

        segmentRepository.deleteById(id);
    }

    public void deleteByName(String name) {

        Segment segment = segmentRepository
                .findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("message: Сегмент не найден"));

        segmentRepository.delete(segment);
    }
}
