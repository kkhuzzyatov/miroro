import styles from './BasketItem.module.css';

type Props = {
  item: any;
  products: any[];
  sizes: any[];
  colors: any[];
  stockMap: Record<number, number>;
  onChange: (item: any, delta: number) => void;
  onRemove: (item: any) => void;
};

function findVariant(products: any[], productId: number, variantId: number) {
  const product = products.find(p => p.id === productId);
  if (!product) return null;

  return product.variants.find((v: any) => v.id === variantId) ?? null;
}

function getBasketImage(product: any, color_id?: number) {
  if (!product?.images || product.images.length === 0) {
    return "";
  }

  // 1. если есть цвет — ищем главное изображение этого цвета
  if (color_id != null) {
    const colorImages = product.images.filter(
      (img: any) => img.color_id === color_id
    );

    if (colorImages.length > 0) {
      const main = colorImages.find((img: any) => img.is_main);
      return main?.path ?? colorImages[0].path;
    }
  }

  // 2. fallback: любое главное изображение
  const main = product.images.find((img: any) => img.is_main);
  return main?.path ?? product.images[0].path ?? "";
}

export default function BasketItem({
  item,
  products,
  sizes,
  colors,
  stockMap,
  onChange,
  onRemove
}: Props) {
  const product = products.find(p => p.id === item.productId);
  const variant = findVariant(products, item.productId, item.variantId);

  const stock = item.variantId ? stockMap[item.variantId] ?? 0 : 0;

  const size = sizes.find(s => s.id === variant?.size_id);
  const color = colors.find(c => c.id === variant?.color_id);

  const imgPath = getBasketImage(product, variant?.color_id);

  return (
    <div className={styles.basketItem}>
      <img
        src={imgPath}
        className={styles.basketImg}
        alt=""
      />

      <div className={styles.basketInfo}>
        <div className={styles.price}>{product?.price} ₽</div>
        <div>{product?.name}</div>

        <div>Размер: {size?.name ?? '—'}</div>
        <div>Цвет: {color?.name ?? '—'}</div>

        <div className={styles.controls}>
          <button
            onClick={(e) => {
              e.stopPropagation();
              onChange(item, -1);
            }}
            disabled={item.quantity <= 1}
          >
            -
          </button>

          <span>{item.quantity}</span>

          <button
            onClick={(e) => {
              e.stopPropagation();
              onChange(item, +1);
            }}
            disabled={item.quantity >= stock}
          >
            +
          </button>

          <button
            className={styles.delete}
            onClick={(e) => {
              e.stopPropagation();
              onRemove(item);
            }}
          >
            Удалить
          </button>
        </div>
      </div>
    </div>
  );
}