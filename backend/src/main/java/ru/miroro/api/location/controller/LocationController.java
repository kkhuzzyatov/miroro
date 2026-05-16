package ru.miroro.api.location.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.miroro.api.location.dto.AddressDto;
import ru.miroro.api.location.service.AddressService;
import ru.miroro.integration.cdek.city.CdekCityService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final CdekCityService cdekCityService;
    private final AddressService addressService;

    @GetMapping("/cities")
    public List<?> getCities(@RequestParam String name) {

        log.atInfo()
                .addKeyValue("endpoint", "GET /api/locations/cities")
                .addKeyValue("cityName", name)
                .log("Поиск городов");

        return cdekCityService.suggestCities(name);
    }

    @GetMapping("/delivery_points")
    public List<AddressDto> getDeliveryPoints(
            @RequestParam("city_uuid") String cityUuid, @RequestParam(required = false) String name) {

        log.atInfo()
                .addKeyValue("endpoint", "GET /api/locations/delivery_points")
                .addKeyValue("cityUuid", cityUuid)
                .addKeyValue("name", name)
                .log("Получение пунктов выдачи");

        return addressService.getAddressesByCityAndName(cityUuid, name);
    }
}
