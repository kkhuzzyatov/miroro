package ru.miroro.api.segment.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.miroro.api.segment.model.Segment;
import ru.miroro.api.segment.repository.SegmentRepository;

@RequiredArgsConstructor
@Service
public class SegmentService {

    private final SegmentRepository segmentRepository;

    public Segment create(Segment segment) {
        segmentRepository.save(segment);

        Integer id = segmentRepository.findIdByName(segment.getName());
        segment.setId(id);

        return segment;
    }

    public List<Segment> findAll() {
        return segmentRepository.findAll();
    }

    public Segment findById(int id) {
        return segmentRepository.findById(id);
    }

    public Segment findByName(String name) {
        return segmentRepository.findByName(name);
    }

    public Segment update(Segment segment) {
        segmentRepository.update(segment);
        return segmentRepository.findById(segment.getId());
    }

    public void deleteById(int id) {
        segmentRepository.deleteById(id);
    }

    public void deleteByName(String name) {
        segmentRepository.deleteByName(name);
    }
}
