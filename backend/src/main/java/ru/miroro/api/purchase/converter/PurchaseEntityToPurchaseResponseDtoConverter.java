package ru.miroro.api.purchase.converter;

import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.miroro.api.location.entity.Address;
import ru.miroro.api.location.repository.AddressRepository;
import ru.miroro.api.purchase.dto.PurchaseResponseDto;
import ru.miroro.api.purchase.entity.PurchaseEntity;
import ru.miroro.api.purchase.entity.PurchaseStatusEntity;
import ru.miroro.api.purchase.repository.StatusOfPurchaseRepository;
import ru.miroro.api.user.entity.User;
import ru.miroro.api.user.repository.UserRepository;

@Component
public class PurchaseEntityToPurchaseResponseDtoConverter implements Converter<PurchaseEntity, PurchaseResponseDto> {

    private final PurchaseItemEntityToPurchaseItemResponseDtoConverter itemConverter;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final StatusOfPurchaseRepository statusRepository;

    public PurchaseEntityToPurchaseResponseDtoConverter(
            PurchaseItemEntityToPurchaseItemResponseDtoConverter itemConverter,
            UserRepository userRepository,
            AddressRepository addressRepository,
            StatusOfPurchaseRepository statusRepository) {
        this.itemConverter = itemConverter;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.statusRepository = statusRepository;
    }

    @Override
    public PurchaseResponseDto convert(PurchaseEntity entity) {

        if (entity == null) {
            return null;
        }

        User user = userRepository.findById(entity.getUserId()).orElseThrow();
        Address address = addressRepository.findById(entity.getAddressId()).orElseThrow();
        PurchaseStatusEntity status =
                statusRepository.findById(entity.getStatusId()).orElseThrow();

        return new PurchaseResponseDto(
                entity.getId(),
                user.getUsername(),
                status.getName(),
                address.getAddress(),
                entity.getItems() == null
                        ? List.of()
                        : entity.getItems().stream().map(itemConverter::convert).toList());
    }
}
