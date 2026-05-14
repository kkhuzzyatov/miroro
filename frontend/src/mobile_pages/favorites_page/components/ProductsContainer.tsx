import styles from './ProductsContainer.module.css';
import { useEffect, useMemo, useState } from "react";

import { fetchProducts } from "../../../api_client/products";
import type { Product } from "../../../api_client/products";
import { fetchSizes } from "../../../api_client/sizes";
import type { Size } from "../../../api_client/sizes";
import { fetchColors } from "../../../api_client/colors";
import type { Color } from "../../../api_client/colors";

import { filterAvailable } from "../../../utils/filter";
import ProductCard from "../../../components/product-card/ProductCard";

type FavoriteItem = {
  productId: number;
  variantId: number;
};

function getFavorite(): FavoriteItem[] {
  const raw = document.cookie
    .split("; ")
    .find(r => r.startsWith("favorite="));

  if (!raw) return [];

  try {
    return JSON.parse(decodeURIComponent(raw.split("=")[1]))
      .map((i: any) => ({
        productId: Number(i.productId),
        variantId: Number(i.variantId),
      }))
      .filter((i: FavoriteItem) =>
        Number.isFinite(i.productId) &&
        Number.isFinite(i.variantId)
      );
  } catch {
    return [];
  }
}

export default function ProductsContainer() {
  const [products, setProducts] = useState<Product[]>([]);
  const [sizes, setSizes] = useState<Size[]>([]);
  const [colors, setColors] = useState<Color[]>([]);
  const [favorite, setFavorite] = useState<FavoriteItem[]>([]);

  useEffect(() => {
    fetchProducts().then(setProducts);
    fetchSizes().then(setSizes);
    fetchColors().then(setColors);
    setFavorite(getFavorite());
  }, []);

  const filteredProducts = useMemo(
    () => filterAvailable(products),
    [products]
  );

  const productMap = useMemo(() => {
    const map = new Map<number, Product>();
    for (const p of filteredProducts) {
      map.set(p.id, p);
    }
    return map;
  }, [filteredProducts]);

  const favoriteWithProducts = useMemo(() => {
    return favorite
      .map(item => {
        const product = productMap.get(item.productId);
        if (!product) return null;

        return {
          ...item,
          product,
        };
      })
      .filter(Boolean) as Array<FavoriteItem & { product: Product }>;
  }, [favorite, productMap]);

  return (
    <div className={styles.productsContainer}>
      {favoriteWithProducts.length === 0 ? (
        <div className={styles.empty}>Нет избранных товаров</div>
      ) : (
        favoriteWithProducts.map(item => (
          <ProductCard
            key={`${item.productId}-${item.variantId}`}
            product={item.product}
            sizes={sizes}
            colors={colors}
            selectedVariantId={item.variantId}
          />
        ))
      )}
    </div>
  );
}