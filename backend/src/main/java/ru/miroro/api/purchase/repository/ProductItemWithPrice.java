package ru.miroro.api.purchase.repository;

import java.math.BigDecimal;

public record ProductItemWithPrice(Integer productItemId, BigDecimal price) {}
