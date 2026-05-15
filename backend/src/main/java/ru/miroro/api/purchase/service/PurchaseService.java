package ru.miroro.api.purchase.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.miroro.api.purchase.dto.*;
import ru.miroro.api.purchase.entity.*;
import ru.miroro.api.purchase.mapper.PurchaseMapper;
import ru.miroro.api.purchase.repository.*;

@RequiredArgsConstructor
@Service
@Transactional
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final StatusOfPurchaseRepository statusRepository;
    private final PurchaseItemRepository itemRepository;
    private final ItemOfProductItemRepository itemOfProductItemRepository;
    private final PurchaseStatusHistoryRepository historyRepository;
    private final PurchaseMapper purchaseMapper;

    @Transactional(readOnly = true)
    public List<PurchaseResponseDto> findAll() {

        return purchaseRepository.findAllFull().stream()
                .map(purchaseMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PurchaseResponseDto> findByUserId(int userId) {

        return purchaseRepository.findByUserIdFull(userId).stream()
                .map(purchaseMapper::toDto)
                .toList();
    }

    public void create(CreatePurchaseRequest request, int userId) {

        PurchaseStatusEntity status = statusRepository
                .findByName("ожидание передачи в пункт отправки")
                .orElseThrow();

        PurchaseEntity purchase = purchaseRepository.save(PurchaseEntity.builder()
                .userId(userId)
                .statusId(status.getId())
                .addressId(request.getAddressId())
                .build());

        historyRepository.save(
                new PurchaseStatusHistoryEntity(null, purchase.getId(), status.getId(), LocalDateTime.now()));

        List<Integer> reservedIds = new ArrayList<>();

        for (PurchaseVariantRequest item : request.getItems()) {

            List<ProductItemEntity> reserved =
                    itemOfProductItemRepository.findByVariant_IdAndIsSoldFalse(item.getVariantId());

            if (reserved.size() < item.getQuantity()) {
                throw new IllegalStateException("Недостаточно товара variant_id=" + item.getVariantId());
            }

            reserved = reserved.subList(0, item.getQuantity());

            for (ProductItemEntity pi : reserved) {

                itemRepository.save(PurchaseItemEntity.builder()
                        .purchase(purchase)
                        .productItem(pi)
                        .price(pi.getVariant().getProduct().getPrice())
                        .build());

                reservedIds.add(pi.getId());
            }
        }

        itemOfProductItemRepository.markSold(reservedIds);
    }

    public void changeStatus(int purchaseId, String newStatus) {

        PurchaseEntity purchase = purchaseRepository.findById(purchaseId).orElseThrow();

        int oldStatus = purchase.getStatusId();

        int newStatusId = statusRepository.findByName(newStatus).orElseThrow().getId();

        purchase.setStatusId(newStatusId);

        purchaseRepository.save(purchase);

        historyRepository.save(new PurchaseStatusHistoryEntity(null, purchaseId, oldStatus, LocalDateTime.now()));
    }
}
