package ru.miroro.api.segment.repository;

import java.util.List;
import ru.miroro.api.segment.model.Segment;

public interface SegmentRepository {

    List<Segment> findAll();

    Segment findById(int segmentId);

    Segment findByName(String name);

    void save(Segment segment);

    int update(Segment segment);

    int deleteById(int segmentId);

    int deleteByName(String name);

    Integer findIdByName(String name);
}
