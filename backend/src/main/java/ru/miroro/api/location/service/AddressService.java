package ru.miroro.api.location.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.miroro.api.location.dto.AddressDto;
import ru.miroro.api.location.entity.Address;
import ru.miroro.api.location.mapper.AddressMapper;
import ru.miroro.integration.cdek.deliverypoints.CdekDeliveryPointService;

@RequiredArgsConstructor
@Service
public class AddressService {

    private final CdekDeliveryPointService deliveryPointService;

    public List<AddressDto> getAddressesByCityAndName(String cityUuid, String name) {

        List<Address> allAddresses = deliveryPointService.getAddresses(cityUuid);

        return allAddresses.stream()
                .filter(address -> name == null
                        || name.isBlank()
                        || address.getAddress().toLowerCase().contains(name.toLowerCase()))
                .map(AddressMapper::toDto)
                .toList();
    }
}
