import { useEffect, useMemo, useState } from 'react';
import styles from './ProductItems.module.css';

import { fetchProducts } from '../../../api_client/products';
import type { Product } from '../../../api_client/products';

import { fetchSizes } from '../../../api_client/sizes';
import type { Size } from '../../../api_client/sizes';

import { fetchColors } from '../../../api_client/colors';
import type { Color } from '../../../api_client/colors';

interface ProductItem {
  productItemId: number;
  productName: string;
  sizeName: string;
  colorName: string;
  colorHex: string;
  isSold: boolean;
}

interface ProductItemGroup {
  key: string;
  productName: string;
  sizeName: string;
  colorName: string;
  colorHex: string;
  isSold: boolean;
  items: ProductItem[];
}

export default function ProductItems() {
  const [products, setProducts] = useState<Product[]>([]);
  const [sizes, setSizes] = useState<Size[]>([]);
  const [colors, setColors] = useState<Color[]>([]);

  const [items, setItems] = useState<ProductItem[]>([]);

  const [selectedProductId, setSelectedProductId] = useState<number | null>(null);
  const [selectedVariantId, setSelectedVariantId] = useState<number | null>(null);

  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [expandedGroups, setExpandedGroups] = useState<string[]>([]);

  useEffect(() => {
    loadInitialData();
    loadProductItems();
  }, []);

  async function fetchJson<T>(url: string): Promise<T> {
    const response = await fetch(url);
    if (!response.ok) throw new Error(url);
    return response.json();
  }

  async function loadInitialData() {
    const [productsData, sizesData, colorsData] = await Promise.all([
      fetchProducts(),
      fetchSizes(),
      fetchColors(),
    ]);

    setProducts(productsData);
    setSizes(sizesData);
    setColors(colorsData);
  }

  async function loadProductItems() {
    const data = await fetchJson<ProductItem[]>('/api/product-items');
    setItems(data);
  }

  const selectedProduct = useMemo(
    () => products.find((p) => p.id === selectedProductId),
    [products, selectedProductId],
  );

  const availableVariants = selectedProduct?.variants ?? [];

  const groupedItems = useMemo<ProductItemGroup[]>(() => {
    const map = new Map<string, ProductItemGroup>();

    for (const item of items) {
      const key = JSON.stringify({
        productName: item.productName,
        sizeName: item.sizeName,
        colorName: item.colorName,
        colorHex: item.colorHex,
        isSold: item.isSold,
      });

      if (!map.has(key)) {
        map.set(key, {
          key,
          productName: item.productName,
          sizeName: item.sizeName,
          colorName: item.colorName,
          colorHex: item.colorHex,
          isSold: item.isSold,
          items: [],
        });
      }

      map.get(key)!.items.push(item);
    }

    return Array.from(map.values());
  }, [items]);

  function toggleGroup(key: string) {
    setExpandedGroups((prev) =>
      prev.includes(key)
        ? prev.filter((k) => k !== key)
        : [...prev, key],
    );
  }

  function getSize(id: number) {
    return sizes.find((s) => s.id === id);
  }

  function getColor(id: number) {
    return colors.find((c) => c.id === id);
  }

  const selectedVariant = availableVariants.find(
    (v) => v.id === selectedVariantId,
  );

  async function handleSave() {
    if (selectedVariantId === null) return;

    const response = await fetch('/api/product-items', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        variantId: selectedVariantId,
      }),
    });

    if (!response.ok) {
      alert('Ошибка сохранения товарной единицы');
      return;
    }

    setSelectedProductId(null);
    setSelectedVariantId(null);
    setIsDropdownOpen(false);

    await loadProductItems();
  }

  return (
    <div className={styles.productItemsTab}>
      <div className={styles.addRow}>
        <select
          className={styles.productSelect}
          value={selectedProductId ?? ''}
          onChange={(e) => {
            setSelectedProductId(Number(e.target.value));
            setSelectedVariantId(null);
            setIsDropdownOpen(false);
          }}
        >
          <option value="" disabled>
            Продукт
          </option>

          {products.map((product) => (
            <option key={product.id} value={product.id}>
              {product.name}
            </option>
          ))}
        </select>

        {selectedProduct && availableVariants.length > 0 && (
          <div className={styles.variantSelect}>
            <div
              className={styles.variantPlaceholder}
              onClick={() => setIsDropdownOpen((p) => !p)}
            >
              {selectedVariant ? (
                <>
                  <span>{getSize(selectedVariant.size_id)?.name}</span>

                  <span>|</span>

                  <span
                    className={styles.colorSquare}
                    style={{
                      backgroundColor:
                        getColor(selectedVariant.color_id)?.hex ?? '#000000',
                    }}
                  />

                  <span>
                    {getColor(selectedVariant.color_id)?.name}
                  </span>
                </>
              ) : (
                'Вариация'
              )}
            </div>

            {isDropdownOpen && (
              <div className={styles.variantDropdown}>
                {availableVariants.map((variant) => {
                  const size = getSize(variant.size_id);
                  const color = getColor(variant.color_id);

                  return (
                    <div
                      key={variant.id}
                      className={styles.variantOption}
                      onClick={() => {
                        setSelectedVariantId(variant.id);
                        setIsDropdownOpen(false);
                      }}
                    >
                      <span>{size?.name}</span>
                      <span>|</span>

                      <span
                        className={styles.colorSquare}
                        style={{
                          backgroundColor: color?.hex ?? '#000000',
                        }}
                      />

                      <span>{color?.name}</span>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        )}

        <button
          className={styles.saveButton}
          disabled={selectedVariantId === null}
          onClick={handleSave}
        >
          Сохранить
        </button>
      </div>

      <div className={styles.itemsContainer}>
        {groupedItems.map((group) => {
          const isExpanded = expandedGroups.includes(group.key);

          return (
            <div key={group.key} className={styles.groupContainer}>
              <div
                className={styles.productItem}
                onClick={() => toggleGroup(group.key)}
              >
                <span>Количество: {group.items.length}</span>
                <span>{group.productName}</span>
                <span>{group.sizeName}</span>

                <span className={styles.colorBlock}>
                  <span
                    className={styles.colorSquare}
                    style={{
                      backgroundColor: group.colorHex?.startsWith('#')
                        ? group.colorHex
                        : `#${group.colorHex ?? '000000'}`,
                    }}
                  />
                  <span>{group.colorName}</span>
                </span>

                <span>{group.isSold ? 'sold' : 'available'}</span>
              </div>

              {isExpanded && (
                <div className={styles.groupItems}>
                  {group.items.map((item) => (
                    <div
                      key={item.productItemId}
                      className={styles.groupItem}
                    >
                      ItemId: {item.productItemId}
                    </div>
                  ))}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}