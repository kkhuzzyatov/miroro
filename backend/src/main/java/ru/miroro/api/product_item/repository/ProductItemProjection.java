package ru.miroro.api.product_item.repository;

public interface ProductItemProjection {
    Integer getProductItemId();

    String getProductName();

    String getSizeName();

    String getColorName();

    String getColorHex();

    Boolean getIsSold();
}
