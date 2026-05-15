package ru.miroro.api.purchase.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.miroro.api.purchase.dto.CreatePurchaseRequest;
import ru.miroro.api.purchase.entity.PurchaseEntity;

@Component
public class CreatePurchaseRequestToPurchaseEntityConverter
        implements Converter<CreatePurchaseRequest, PurchaseEntity> {

    @Override
    public PurchaseEntity convert(CreatePurchaseRequest request) {

        if (request == null) {
            return null;
        }

        PurchaseEntity entity = new PurchaseEntity();
        entity.setAddressId(request.getAddressId());
        return entity;
    }
}
