package ru.miroro.api.location.mapper;

import ru.miroro.api.location.dto.AddressDto;
import ru.miroro.api.location.dto.CityDto;
import ru.miroro.api.location.entity.Address;

public class AddressMapper {

    public static AddressDto toDto(Address entity) {
        if (entity == null) return null;

        return AddressDto.builder()
                .id(entity.getId())
                .address(entity.getAddress())
                .city(CityDto.builder()
                        .cityUuid(entity.getCity().getCityUuid())
                        .name(entity.getCity().getName())
                        .build())
                .build();
    }
}
