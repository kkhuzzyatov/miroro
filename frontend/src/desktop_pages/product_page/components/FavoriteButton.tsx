import { useMemo, useState } from "react";
import styles from "./FavoriteButton.module.css";

import emptyHeart from "./../../../assets/heart/empty_heart.png";
import fullHeart from "./../../../assets/heart/full_heart.png";

type FavoriteItem = {
  productId: number;
  variantId: number;
};

type Props = {
  productId: number;
  variantId: number | null;
};

const COOKIE_NAME = "favorite";

export default function FavoriteButton({
  productId,
  variantId
}: Props) {

  // --- cookie helpers ---

  const readCookie = (): FavoriteItem[] => {
    const raw = document.cookie
      .split("; ")
      .find(row => row.startsWith(`${COOKIE_NAME}=`));

    if (!raw) return [];

    try {
      return JSON.parse(decodeURIComponent(raw.split("=")[1]))
        .map((i: any) => ({
          productId: Number(i.productId),
          variantId: Number(i.variantId),
        }))
        .filter((i: FavoriteItem) => !isNaN(i.variantId));
    } catch {
      return [];
    }
  };

  const writeCookie = (data: FavoriteItem[]) => {
    document.cookie = `${COOKIE_NAME}=${encodeURIComponent(
      JSON.stringify(data)
    )}; path=/; max-age=31536000`;
  };

  const findItem = (
    list: FavoriteItem[],
    productId: number,
    variantId: number
  ) => {
    return list.find(
      i =>
        i.productId === productId &&
        i.variantId === variantId
    );
  };

  const toggle = (
    list: FavoriteItem[],
    productId: number,
    variantId: number
  ): FavoriteItem[] => {
    const exists = findItem(list, productId, variantId);

    if (exists) {
      return list.filter(
        i =>
          !(
            i.productId === productId &&
            i.variantId === variantId
          )
      );
    }

    return [...list, { productId, variantId }];
  };

  const [favorite, setFavorite] = useState<FavoriteItem[]>(() => readCookie());

  // --- derived state ---

  const inFavorite = useMemo(() => {
    if (variantId === null) return false;

    return !!findItem(
      favorite,
      productId,
      variantId
    );
  }, [favorite, productId, variantId]);

  // --- actions ---

  const handleClick = () => {
    if (variantId === null) return;

    const updated = toggle(
      favorite,
      productId,
      variantId
    );

    setFavorite(updated);
    writeCookie(updated);
  };

  // --- render ---

  return (
    <img
      src={inFavorite ? fullHeart : emptyHeart}
      className={styles.heartIcon}
      onClick={handleClick}
      alt="favorite"
    />
  );
}