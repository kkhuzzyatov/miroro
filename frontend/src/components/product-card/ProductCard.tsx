import styles from './ProductCard.module.css';
import { useNavigate, useSearchParams } from "react-router-dom";
import { useEffect, useState } from "react";

import type { Product } from "../../api/products";
import type { Size } from "../../api/sizes";
import type { Color } from "../../api/colors";

import { truncate } from "../../utils/truncate";

type FavoriteItem = {
  productId: number;
  variantId: number;
};

function findVariant(product: Product, variantId: number) {
  return product.variants.find(v => v.id === variantId) ?? null;
}

/**
 * изображение (как было)
 */
function getProductImage(product: Product, color_id?: number): string {
  if (!product.images || product.images.length === 0) {
    return "";
  }

  if (color_id != null) {
    const colorImages = product.images.filter(img => img.color_id === color_id);

    if (colorImages.length > 0) {
      const main = colorImages.find(img => img.isMain);
      return main?.path ?? colorImages[0].path;
    }
  }

  const main = product.images.find(img => img.isMain);
  return main?.path ?? product.images[0].path;
}

/**
 * cookie helpers
 */
const COOKIE_NAME = "favorites";

function getFavorites(): FavoriteItem[] {
  const raw = document.cookie
    .split("; ")
    .find(row => row.startsWith(`${COOKIE_NAME}=`));

  if (!raw) return [];

  try {
    return JSON.parse(decodeURIComponent(raw.split("=")[1]));
  } catch {
    return [];
  }
}

function setFavorites(items: FavoriteItem[]) {
  document.cookie = `${COOKIE_NAME}=${encodeURIComponent(
    JSON.stringify(items)
  )}; path=/; max-age=31536000`;
}

function isFavorite(
  list: FavoriteItem[],
  productId: number,
  variantId: number
) {
  return list.some(
    i => i.productId === productId && i.variantId === variantId
  );
}

function toggleFavorite(
  list: FavoriteItem[],
  productId: number,
  variantId: number
): FavoriteItem[] {
  const exists = isFavorite(list, productId, variantId);

  if (exists) {
    return list.filter(
      i => !(i.productId === productId && i.variantId === variantId)
    );
  }

  return [...list, { productId, variantId }];
}

/**
 * берём "первый" variant (fallback логика)
 */
function getFirstVariant(product: Product) {
  return product.variants[0] ?? null;
}

export default function ProductCard({
  product,
  selectedVariantId
}: {
  product: Product;
  sizes: Size[];
  colors: Color[];
  selectedVariantId: number | null;
}) {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const variant =
    selectedVariantId != null
      ? findVariant(product, selectedVariantId)
      : null;

  const selectedcolor_id = variant?.color_id;

  const [favorite, setFavorite] = useState<FavoriteItem[]>(() => getFavorites());

  useEffect(() => {
    setFavorite(getFavorites());
  }, [product.id]);

  const goToProduct = () => {

    navigate(
      `/product/${product.id}${
        selectedVariantId != null
          ? `?variant_id=${selectedVariantId}`
          : ""
      }`
    );
  };

  const handleFavoriteClick = (e: React.MouseEvent) => {
    e.stopPropagation();

    const firstVariant = getFirstVariant(product);
    if (!firstVariant) return;

    const updated = toggleFavorite(
      getFavorites(),
      product.id,
      firstVariant.id
    );

    setFavorites(updated);
    setFavorite(updated);
  };

  const imageSrc = getProductImage(product, selectedcolor_id ?? undefined);

  return (
    <div className={styles.productCard} onClick={goToProduct}>
      <div className={styles.imageWrapper}>
        <img
          src={imageSrc}
          className={styles.productImage}
          alt={truncate(product.name)}
        />
      </div>

      <div className={styles.productInfo}>
        <div className={styles.productName}>
          {truncate(product.name)}
        </div>

        <div className={styles.productPrice}>
          {product.price} ₽
        </div>
      </div>
    </div>
  );
}