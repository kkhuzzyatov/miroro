import styles from './ProductPage.module.css';
import { useParams, useSearchParams, useNavigate } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";

import { ProductImageGallery } from "./components/ProductImageGallery";
import { ProductInfo } from "./components/ProductInfo";
import SizeSelector from "./components/SizeSelector";
import ColorSelector from "./components/ColorSelector";
import TopBar from "./components/TopBar";

export interface ProductVariant {
  variant_id: number;
  size_id: number;
  color_id: number;
  quantity: number;
}

export interface ProductImage {
  path: string;
  is_main: boolean;
  color_id: number;
}

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  variants: ProductVariant[];
  images: ProductImage[];
}

type CartItem = {
  productId: number;
  variantId: number;
  quantity: number;
};

function getCartFromCookie(): CartItem[] {
  const raw = document.cookie
    .split("; ")
    .find(row => row.startsWith("cart="));

  if (!raw) return [];

  try {
    return JSON.parse(decodeURIComponent(raw.split("=")[1]))
      .map((i: any) => ({
        productId: Number(i.productId),
        variantId: Number(i.variantId),
        quantity: Number(i.quantity),
      }))
      .filter((i: CartItem) => !isNaN(i.variantId));
  } catch {
    return [];
  }
}

function setCartCookie(cart: CartItem[]) {
  document.cookie = `cart=${encodeURIComponent(
    JSON.stringify(cart)
  )}; path=/; max-age=31536000`;
}

function findItem(
  cart: CartItem[],
  productId: number,
  variantId: number
) {
  return cart.find(
    i =>
      i.productId === productId &&
      i.variantId === variantId
  );
}

export default function ProductPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const variantIdParam = searchParams.get("variant_id");

  const [product, setProduct] = useState<Product | null>(null);
  const [sizes, setSizes] = useState<any[]>([]);
  const [colors, setColors] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  const [selectedSize, setSelectedSize] = useState<number | null>(null);
  const [selectedColor, setSelectedColor] = useState<number | null>(null);

  const [cart, setCart] = useState<CartItem[]>([]);

  useEffect(() => {
    if (!id) return;

    (async () => {
      setLoading(true);

      const [productRes, sizesRes, colorsRes] = await Promise.all([
        fetch(`/api/products?id=${id}`).then(r => r.json()),
        fetch("/api/sizes").then(r => r.json()),
        fetch("/api/colors").then(r => r.json()),
      ]);

      const product: Product = Array.isArray(productRes)
        ? productRes[0]
        : productRes;

      setProduct(product);
      setSizes(sizesRes);
      setColors(colorsRes);
      setCart(getCartFromCookie());

      let initialSize: number | null = null;
      let initialColor: number | null = null;

      if (variantIdParam && product.variants.length) {
        const variant = product.variants.find(
          v => v.variant_id === Number(variantIdParam)
        );

        if (variant && variant.quantity > 0) {
          initialSize = variant.size_id;
          initialColor = variant.color_id;
        }
      }

      if (initialSize === null) {
        const availableSize =
          [...new Set(product.variants.map(v => v.size_id))]
            .find(size_id =>
              product.variants.some(
                v => v.size_id === size_id && v.quantity > 0
              )
            ) ?? null;

        initialSize = availableSize;

        initialColor =
          product.variants
            .filter(v => v.size_id === availableSize && v.quantity > 0)
            .map(v => v.color_id)[0] ?? null;
      }

      setSelectedSize(initialSize);
      setSelectedColor(initialColor);

      setLoading(false);
    })();
  }, [id, variantIdParam]);

  const filteredImages = useMemo(() => {
    if (!product) return [];

    // если цвет не выбран — показываем все
    if (selectedColor === null) return product.images;

    // фильтрация по color_id
    const imagesByColor = product.images.filter(
      img => img.color_id === selectedColor
    );

    // fallback: если нет изображений для цвета — показываем все
    return imagesByColor.length > 0 ? imagesByColor : product.images;
  }, [product, selectedColor]);

  const selectedVariant = useMemo(() => {
    if (!product || selectedSize === null || selectedColor === null) return null;

    return product.variants.find(
      v =>
        v.size_id === selectedSize &&
        v.color_id === selectedColor
    ) ?? null;
  }, [product, selectedSize, selectedColor]);

  const stock = selectedVariant?.quantity ?? 0;
  const currentVariantId = selectedVariant?.variant_id ?? null;

  const cartItem = useMemo(() => {
    if (!product || currentVariantId === null) return null;

    return findItem(cart, product.id, currentVariantId);
  }, [cart, product, currentVariantId]);

  const qty = cartItem?.quantity ?? 0;
  const inCart = !!cartItem;

  const updateCart = (newCart: CartItem[]) => {
    setCart(newCart);
    setCartCookie(newCart);
  };

  const handleAdd = () => {
    if (!product || currentVariantId === null) return;
    if (qty >= stock) return;

    const newCart = [...cart];
    const item = findItem(newCart, product.id, currentVariantId);

    if (item) item.quantity += 1;
    else {
      newCart.push({
        productId: product.id,
        variantId: currentVariantId,
        quantity: 1,
      });
    }

    updateCart(newCart);
  };

  const handleMinus = () => {
    if (!product || currentVariantId === null) return;

    const newCart = [...cart];
    const item = findItem(newCart, product.id, currentVariantId);

    if (!item) return;

    item.quantity -= 1;

    const filtered =
      item.quantity <= 0
        ? newCart.filter(i => i !== item)
        : newCart;

    updateCart(filtered);
  };

  const handleSelectSize = (size_id: number) => {
    if (!product) return;

    setSelectedSize(size_id);

    // проверяем существует ли текущий цвет для нового размера
    const currentColorVariant = product.variants.find(
      v =>
        v.size_id === size_id &&
        v.color_id === selectedColor &&
        v.quantity > 0
    );

    // если существует — НЕ меняем цвет
    if (currentColorVariant) {
      return;
    }

    // иначе берем первый доступный цвет
    const firstAvailableColor =
      product.variants.find(
        v =>
          v.size_id === size_id &&
          v.quantity > 0
      )?.color_id ?? null;

    setSelectedColor(firstAvailableColor);
  };

  if (loading) return <div>Loading...</div>;
  if (!product) return <div>Product not found</div>;

  return (
    <div className={styles.productPage}>

      <TopBar
        productId={product.id}
        currentVariantId={currentVariantId}
      />

      <ProductImageGallery images={filteredImages} />

      <div className={styles.productContent}>
        <ProductInfo
          name={product.name}
          price={product.price}
          description={product.description}
        />

        <div className={styles.selectorsContainer}>
          <SizeSelector
            sizes={sizes}
            variants={product.variants}
            selectedSize={selectedSize}
            onSelect={handleSelectSize}
          />
        </div>

        <div className={styles.selectorsContainer}>
          {selectedSize !== null && (
            <ColorSelector
              colors={colors}
              variants={product.variants}
              selectedSize={selectedSize}
              selectedColor={selectedColor}
              onSelect={setSelectedColor}
            />
          )}
        </div>

        <div className={styles.scrollSpacer} />
      </div>

      <div className={styles.cartBar}>
        <div
          className={`${styles.cartButton} ${inCart ? styles.inCart : ""}`}
          onClick={() => {
            if (currentVariantId === null) return;

            if (inCart) {
              navigate("/cart");
            } else {
              handleAdd();
            }
          }}
        >
          <span>{inCart ? "В корзине" : "В корзину"}</span>

          {inCart && (
            <div
              className={styles.cartCounter}
              onClick={(e) => e.stopPropagation()}
            >
              <button disabled={qty <= 1} onClick={handleMinus}>
                -
              </button>

              <span>{qty}</span>

              {qty < stock && (
                <button onClick={handleAdd}>+</button>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}