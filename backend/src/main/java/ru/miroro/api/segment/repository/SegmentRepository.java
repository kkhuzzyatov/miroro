package ru.miroro.api.segment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.miroro.api.segment.model.Segment;

public interface SegmentRepository extends JpaRepository<Segment, Integer> {

    Optional<Segment> findByName(String name);

    boolean existsByName(String name);
}
