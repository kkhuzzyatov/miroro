package ru.miroro.integration.cdek.city;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.miroro.api.location.entity.City;
import ru.miroro.api.location.repository.CityRepository;
import ru.miroro.integration.cdek.access_token.CdekTokenService;

@Slf4j
@RequiredArgsConstructor
@Service
public class CdekCityService {
    private final RestTemplate restTemplate;
    private final CdekTokenService tokenService;
    private final CityRepository cityRepository;

    @Value("${cdek.auth.url}")
    private String apiBaseUrl;

    @Value("${cdek.auth.country-code}")
    private String countryCode;

    public List<CdekCity> suggestCities(String name) {

        String url = String.format("%s/location/suggest/cities?name=%s", apiBaseUrl, name);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenService.getAccessToken());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<CdekCity[]> response = restTemplate.exchange(url, HttpMethod.GET, request, CdekCity[].class);

        CdekCity[] cities = response.getBody();

        if (cities == null) {
            return List.of();
        }

        return Arrays.stream(cities)
                .filter(c -> countryCode.equalsIgnoreCase(c.getCountryCode()))
                .map(c -> {
                    normalizeCityName(c);
                    saveCityIfNeeded(c);
                    return c;
                })
                .collect(Collectors.toList());
    }

    private void normalizeCityName(CdekCity city) {
        String fullName = city.getFullName();
        int lastComma = fullName.lastIndexOf(",");
        if (lastComma != -1) {
            city.setFullName(fullName.substring(0, lastComma).trim());
        }
    }

    private void saveCityIfNeeded(CdekCity cdekCity) {
        try {
            City city = new City(cdekCity.getCityUuid(), cdekCity.getFullName(), null);

            cityRepository.save(city);

        } catch (DuplicateKeyException e) {
            log.debug("city '{}' already exists", cdekCity.getFullName());
        }
    }
}
