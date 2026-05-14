import styles from './BasketPage.module.css';
import { useEffect, useState, useMemo } from "react";

import { fetchProducts } from "../../api_client/products";
import type { Product } from "../../api_client/products";

import { fetchSizes } from "../../api_client/sizes";
import type { Size } from "../../api_client/sizes";

import { fetchColors } from "../../api_client/colors";
import type { Color } from "../../api_client/colors";

import BasketItem from "./components/BasketItem";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import AddressModal from "../../components/address_modal/AddressModal";

type CartItem = {
  productId: number;
  variantId: number;
  quantity: number;
};

function getCart(): CartItem[] {
  const raw = document.cookie
    .split("; ")
    .find(r => r.startsWith("cart="));

  if (!raw) return [];

  try {
    return JSON.parse(decodeURIComponent(raw.split("=")[1]))
      .map((i: any) => ({
        productId: Number(i.productId),
        variantId: Number(i.variantId),
        quantity: Number(i.quantity),
      }));
  } catch {
    return [];
  }
}

function setCartCookie(cart: CartItem[]) {
  document.cookie =
    `cart=${encodeURIComponent(JSON.stringify(cart))}; path=/; max-age=31536000`;
}

function getStockMap(products: Product[]) {
  const map: Record<number, number> = {};

  for (const p of products) {
    for (const v of p.variants) {
      map[v.id] = v.quantity;
    }
  }

  return map;
}

function getCookie(name: string): string | null {
  const match = document.cookie.match(
    new RegExp("(^| )" + name + "=([^;]+)")
  );
  return match ? decodeURIComponent(match[2]) : null;
}

export default function BasketPage() {
  const [cart, setCartState] = useState<CartItem[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [sizes, setSizes] = useState<Size[]>([]);
  const [colors, setColors] = useState<Color[]>([]);
  const [stockMap, setStockMap] = useState<Record<number, number>>({});

  const [isAddressModalOpen, setIsAddressModalOpen] = useState(false);

  const navigate = useNavigate();
  const isAuth = useAuth();

  const totalPrice = useMemo(() => {
    return cart.reduce((sum, item) => {
      const product = products.find(p => p.id === item.productId);
      return sum + (product?.price ?? 0) * item.quantity;
    }, 0);
  }, [cart, products]);

  useEffect(() => {
    load();
  }, []);

  async function load() {
    const [p, c, s] = await Promise.all([
      fetchProducts(),
      fetchColors(),
      fetchSizes()
    ]);

    setProducts(p);
    setColors(c);
    setSizes(s);

    setStockMap(getStockMap(p));

    const loadedCart = getCart();
    setCartState(loadedCart);
    setCartCookie(loadedCart);
  }

  function updateCart(newCart: CartItem[]) {
    setCartState(newCart);
    setCartCookie(newCart);
  }

  function resolveStock(variantId: number) {
    return stockMap[variantId] ?? 0;
  }

  function changeQty(item: CartItem, delta: number) {
    const updated = [...cart];

    const idx = updated.findIndex(
      i => i.productId === item.productId && i.variantId === item.variantId
    );

    if (idx === -1) return;

    const stock = resolveStock(item.variantId);
    const next = updated[idx].quantity + delta;

    if (next < 1 || next > stock) return;

    updated[idx].quantity = next;
    updateCart(updated);
  }

  function removeItem(item: CartItem) {
    updateCart(
      cart.filter(
        i => !(i.productId === item.productId && i.variantId === item.variantId)
      )
    );
  }

  function openProduct(item: CartItem) {
    navigate(`/product/${item.productId}?variant_id=${item.variantId}`);
  }

  async function handleOrder() {
    if (isAuth === false) {
      navigate("/login");
      return;
    }

    if (isAuth === null) return;
    if (cart.length === 0) return;

    const addressId = getCookie("address_id");

    if (!addressId) {
      setIsAddressModalOpen(true);
      return;
    }

    const items = [];

    for (const item of cart) {
      const stock = resolveStock(item.variantId);

      if (item.quantity > stock) {
        alert(`Доступно: ${stock}`);
        return;
      }

      items.push({
        variantId: item.variantId,
        quantity: item.quantity
      });
    }

    try {
      const res = await fetch("/api/purchases", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({
          addressId: Number(addressId),
          items
        })
      });

      if (!res.ok) {
        alert("Ошибка оформления заказа");
        return;
      }

      updateCart([]);
      alert("Заказ оформлен");
    } catch {
      alert("Ошибка сети");
    }
  }

  return (
    <div className={styles.basketPage}>
      {cart.length === 0 ? (
        <div className={styles.empty}>В корзине пусто</div>
      ) : (
        cart.map(item => (
          <div
            key={`${item.productId}-${item.variantId}`}
            onClick={() => openProduct(item)}
            className={styles.clickableItem}
          >
            <BasketItem
              item={item}
              products={products}
              sizes={sizes}
              colors={colors}
              stockMap={stockMap}
              onChange={changeQty}
              onRemove={removeItem}
            />
          </div>
        ))
      )}

      <div style={{ height: "300px" }} />

      {cart.length > 0 && (
        <div className={styles.total}>
          Сумма: {totalPrice.toLocaleString("ru-RU")} ₽
        </div>
      )}

      {cart.length > 0 && (
        <button className={styles.orderBtn} onClick={handleOrder}>
          Заказать
        </button>
      )}

      <button
        className={styles.myOrdersBtn}
        onClick={() => navigate("/account")}
      >
        Мои заказы
      </button>

      <AddressModal
        isOpen={isAddressModalOpen}
        onClose={() => setIsAddressModalOpen(false)}
        onConfirm={() => {}}
      />
    </div>
  );
}