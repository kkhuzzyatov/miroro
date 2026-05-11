package ru.miroro.api.location.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.miroro.api.location.entity.Address;
import ru.miroro.api.location.service.AddressService;
import ru.miroro.integration.cdek.city.CdekCityService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final CdekCityService cdekCityService;
    private final AddressService addressService;

    @GetMapping("/cities")
    public List<?> getCities(@RequestParam String name) {
        return cdekCityService.suggestCities(name);
    }

    @GetMapping("/delivery_points")
    public List<Address> getDeliveryPoints(
            @RequestParam("city_uuid") String cityUuid, @RequestParam(required = false) String name) {

        return addressService.getAddressesByCityAndName(cityUuid, name);
    }
}
