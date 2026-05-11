package ru.miroro.api.purchase_status.repository;

import java.util.List;
import ru.miroro.api.purchase_status.model.PurchaseStatus;

public interface PurchaseStatusRepository {

    List<PurchaseStatus> findAll();
}
