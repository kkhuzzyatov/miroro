import styles from './ProductPage.module.css';
import { useParams, useSearchParams, useNavigate } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";

import { fetchProductById } from "../../api_client/products";
import type { Product } from "../../api_client/products";

import { fetchSizes } from "../../api_client/sizes";
import type { Size } from "../../api_client/sizes";

import { fetchColors } from "../../api_client/colors";
import type { Color } from "../../api_client/colors";

import { ProductImageGallery } from "./components/ProductImageGallery";
import { ProductInfo } from "./components/ProductInfo";
import SizeSelector from "./components/SizeSelector";
import ColorSelector from "./components/ColorSelector";
import TopBar from "./components/TopBar";

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
  const [sizes, setSizes] = useState<Size[]>([]);
  const [colors, setColors] = useState<Color[]>([]);
  const [loading, setLoading] = useState(true);

  const [selectedSize, setSelectedSize] = useState<number | null>(null);
  const [selectedColor, setSelectedColor] = useState<number | null>(null);

  const [cart, setCart] = useState<CartItem[]>([]);

  useEffect(() => {
    if (!id) return;

    (async () => {
      try {
        setLoading(true);

        const [productRes, sizesRes, colorsRes] = await Promise.all([
          fetchProductById(Number(id)),
          fetchSizes(),
          fetchColors(),
        ]);

        const loadedProduct: Product = Array.isArray(productRes)
          ? productRes[0]
          : productRes;

        setProduct(loadedProduct);
        setSizes(sizesRes);
        setColors(colorsRes);
        setCart(getCartFromCookie());

        let initialSize: number | null = null;
        let initialColor: number | null = null;

        if (variantIdParam && loadedProduct.variants.length) {
          const variant = loadedProduct.variants.find(
            v => v.id === Number(variantIdParam)
          );

          if (variant && variant.quantity > 0) {
            initialSize = variant.size_id;
            initialColor = variant.color_id;
          }
        }

        if (initialSize === null) {
          const availableSize =
            [...new Set(loadedProduct.variants.map(v => v.size_id))]
              .find(sizeId =>
                loadedProduct.variants.some(
                  v =>
                    v.size_id === sizeId &&
                    v.quantity > 0
                )
              ) ?? null;

          initialSize = availableSize;

          initialColor =
            loadedProduct.variants
              .filter(
                v =>
                  v.size_id === availableSize &&
                  v.quantity > 0
              )
              .map(v => v.color_id)[0] ?? null;
        }

        setSelectedSize(initialSize);
        setSelectedColor(initialColor);
      } finally {
        setLoading(false);
      }
    })();
  }, [id, variantIdParam]);

  const filteredImages = useMemo(() => {
    if (!product) return [];

    if (selectedColor === null) {
      return product.images;
    }

    const imagesByColor = product.images.filter(
      img => img.color_id === selectedColor
    );

    return imagesByColor.length > 0
      ? imagesByColor
      : product.images;
  }, [product, selectedColor]);

  const selectedVariant = useMemo(() => {
    if (
      !product ||
      selectedSize === null ||
      selectedColor === null
    ) {
      return null;
    }

    return product.variants.find(
      v =>
        v.size_id === selectedSize &&
        v.color_id === selectedColor
    ) ?? null;
  }, [product, selectedSize, selectedColor]);

  const stock = selectedVariant?.quantity ?? 0;
  const currentVariantId = selectedVariant?.id ?? null;

  const cartItem = useMemo(() => {
    if (!product || currentVariantId === null) {
      return null;
    }

    return findItem(
      cart,
      product.id,
      currentVariantId
    );
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

    const item = findItem(
      newCart,
      product.id,
      currentVariantId
    );

    if (item) {
      item.quantity += 1;
    } else {
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

    const item = findItem(
      newCart,
      product.id,
      currentVariantId
    );

    if (!item) return;

    item.quantity -= 1;

    const filtered =
      item.quantity <= 0
        ? newCart.filter(i => i !== item)
        : newCart;

    updateCart(filtered);
  };

  const handleSelectSize = (sizeId: number) => {
    if (!product) return;

    setSelectedSize(sizeId);

    const hasCurrentColorVariant =
      selectedColor !== null &&
      product.variants.some(
        v =>
          v.size_id === sizeId &&
          v.color_id === selectedColor &&
          v.quantity > 0
      );

    if (hasCurrentColorVariant) {
      return;
    }

    const fallbackColor =
      product.variants.find(
        v =>
          v.size_id === sizeId &&
          v.quantity > 0
      )?.color_id ?? null;

    setSelectedColor(fallbackColor);
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!product) {
    return <div>Product not found</div>;
  }

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
          <span>
            {inCart ? "В корзине" : "В корзину"}
          </span>

          {inCart && (
            <div
              className={styles.cartCounter}
              onClick={(e) => e.stopPropagation()}
            >
              <button
                disabled={qty <= 1}
                onClick={handleMinus}
              >
                -
              </button>

              <span>{qty}</span>

              {qty < stock && (
                <button onClick={handleAdd}>
                  +
                </button>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}