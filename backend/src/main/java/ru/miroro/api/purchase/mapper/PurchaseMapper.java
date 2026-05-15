package ru.miroro.api.purchase.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.miroro.api.location.entity.Address;
import ru.miroro.api.location.repository.AddressRepository;
import ru.miroro.api.purchase.dto.PurchaseItemResponseDto;
import ru.miroro.api.purchase.dto.PurchaseResponseDto;
import ru.miroro.api.purchase.entity.PurchaseEntity;
import ru.miroro.api.purchase.entity.PurchaseItemEntity;
import ru.miroro.api.purchase.entity.PurchaseStatusEntity;
import ru.miroro.api.purchase.entity.VariantEntity;
import ru.miroro.api.purchase.repository.StatusOfPurchaseRepository;
import ru.miroro.api.user.entity.User;
import ru.miroro.api.user.repository.UserRepository;

@RequiredArgsConstructor
@Component
public class PurchaseMapper {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final StatusOfPurchaseRepository statusRepository;

    public PurchaseResponseDto toDto(PurchaseEntity purchase) {

        User user = userRepository.findById(purchase.getUserId()).orElseThrow();

        Address address = addressRepository.findById(purchase.getAddressId()).orElseThrow();

        PurchaseStatusEntity status =
                statusRepository.findById(purchase.getStatusId()).orElseThrow();

        return new PurchaseResponseDto(
                purchase.getId(),
                user.getUsername(),
                status.getName(),
                address.getAddress(),
                purchase.getItems().stream().map(this::toDto).toList());
    }

    public PurchaseItemResponseDto toDto(PurchaseItemEntity item) {

        VariantEntity variant = item.getProductItem().getVariant();

        return new PurchaseItemResponseDto(
                item.getId(),
                variant.getProduct().getName(),
                variant.getSize().getName(),
                variant.getColor().getName(),
                item.getPrice());
    }
}
