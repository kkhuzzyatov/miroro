package ru.miroro.api.location.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.miroro.api.location.entity.Address;
import ru.miroro.integration.cdek.deliverypoints.CdekDeliveryPointService;

@RequiredArgsConstructor
@Service
public class AddressService {

    private final CdekDeliveryPointService deliveryPointService;

    public List<Address> getAddressesByCityAndName(String cityUuid, String name) {

        List<Address> allAddresses = deliveryPointService.getAddresses(cityUuid);

        if (name == null || name.isBlank()) {
            return allAddresses;
        }

        String lowerName = name.toLowerCase();

        return allAddresses.stream()
                .filter(address -> address.getAddress().toLowerCase().contains(lowerName))
                .toList();
    }
}
