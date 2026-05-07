import styles from "./ProductsContainer.module.css";
import { useEffect, useMemo, useState } from "react";

import { fetchProducts } from "../../../api/products";
import type { Product } from "../../../api/products";

import { fetchSizes } from "../../../api/sizes";
import type { Size } from "../../../api/sizes";

import { fetchColors } from "../../../api/colors";
import type { Color } from "../../../api/colors";

import { filterAvailable } from "../../../utils/filter";
import ProductCard from "../../../components/product-card/ProductCard";

/**
 * одна карточка = один цвет продукта
 */
type ProductColorGroup = {
  product: Product;
  variantId: number;
};

export default function ProductsContainer() {
  const [products, setProducts] = useState<Product[]>([]);
  const [sizes, setSizes] = useState<Size[]>([]);
  const [colors, setColors] = useState<Color[]>([]);

  useEffect(() => {
    fetchProducts().then(setProducts);
    fetchSizes().then(setSizes);
    fetchColors().then(setColors);
  }, []);

  const filtered = useMemo(() => filterAvailable(products), [products]);

  /**
   * группировка по цвету (без дубликатов)
   * берём любой variantId внутри цвета
   */
  const grouped = useMemo<ProductColorGroup[]>(() => {
    const result: ProductColorGroup[] = [];

    for (const product of filtered) {
      const usedColors = new Set<number>();

      for (const variant of product.variants) {
        if (usedColors.has(variant.color_id)) continue;

        usedColors.add(variant.color_id);

        result.push({
          product,
          variantId: variant.id
        });
      }
    }

    return result;
  }, [filtered]);

  return (
    <div className={styles.productsContainer}>
      {grouped.map(({ product, variantId }) => (
        <ProductCard
          key={`${product.id}-${variantId}`}
          product={product}
          sizes={sizes}
          colors={colors}
          selectedVariantId={variantId}
        />
      ))}
    </div>
  );
}