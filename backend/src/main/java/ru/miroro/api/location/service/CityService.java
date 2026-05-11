package ru.miroro.api.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.miroro.api.location.repository.CityRepository;

@RequiredArgsConstructor
@Service
public class CityService {

    private final CityRepository cityRepository;
}
