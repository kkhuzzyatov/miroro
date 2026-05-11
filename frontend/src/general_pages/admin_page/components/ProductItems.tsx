import { useEffect, useMemo, useState } from 'react';
import styles from './ProductItems.module.css';

interface Product {
  id: number;
  name: string;
  variants: Variant[];
}

interface Variant {
  variant_id: number;
  size_id: number;
  color_id: number;
}

interface Size {
  id: number;
  name: string;
}

interface Color {
  id: number;
  name: string;
  hex: string;
}

interface ProductItem {
  productItemId: number;
  productName: string;
  sizeName: string;
  colorName: string;
  colorHex: string;
  isSold: boolean;
}

export default function ProductItems() {
  const [products, setProducts] = useState<Product[]>([]);
  const [sizes, setSizes] = useState<Size[]>([]);
  const [colors, setColors] = useState<Color[]>([]);

  const [items, setItems] = useState<ProductItem[]>([]);

  const [selectedProductId, setSelectedProductId] = useState<number | null>(
    null,
  );

  const [selectedVariantId, setSelectedVariantId] = useState<number | null>(
    null,
  );

  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  useEffect(() => {
    loadInitialData();
    loadProductItems();
  }, []);

  async function fetchJson<T>(url: string): Promise<T> {
    const response = await fetch(url);

    if (!response.ok) {
      throw new Error(url);
    }

    return response.json();
  }

  async function loadInitialData() {
    try {
      const [productsData, sizesData, colorsData] = await Promise.all([
        fetchJson<Product[]>('/api/products'),
        fetchJson<Size[]>('/api/sizes'),
        fetchJson<Color[]>('/api/colors'),
      ]);

      setProducts(productsData);
      setSizes(sizesData);
      setColors(colorsData);
    } catch (error) {
      console.error(error);
    }
  }

  async function loadProductItems() {
    try {
      const data = await fetchJson<ProductItem[]>('/api/product-items');
      setItems(data);
    } catch (error) {
      console.error(error);
    }
  }

  const selectedProduct = useMemo(
    () => products.find((p) => p.id === selectedProductId),
    [products, selectedProductId],
  );

  const availableVariants = selectedProduct?.variants ?? [];

  function getSize(id: number) {
    return sizes.find((s) => s.id === id);
  }

  function getColor(id: number) {
    return colors.find((c) => c.id === id);
  }

  async function handleSave() {
    if (!selectedVariantId) {
      return;
    }

    try {
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
        throw new Error();
      }

      setSelectedProductId(null);
      setSelectedVariantId(null);
      setIsDropdownOpen(false);

      await loadProductItems();
    } catch (error) {
      console.error(error);
      alert('Ошибка сохранения товарной единицы');
    }
  }

  const selectedVariant = availableVariants.find(
    (v) => v.variant_id === selectedVariantId,
  );

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
              onClick={() => setIsDropdownOpen((prev) => !prev)}
            >
              {selectedVariant ? (
                <>
                  <span>
                    {getSize(selectedVariant.size_id)?.name}
                  </span>

                  <span>|</span>

                  <span
                    className={styles.colorSquare}
                    style={{
                      backgroundColor: `#${getColor(selectedVariant.color_id)?.hex}`,
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
                      key={variant.variant_id}
                      className={styles.variantOption}
                      onClick={() => {
                        setSelectedVariantId(variant.variant_id);
                        setIsDropdownOpen(false);
                      }}
                    >
                      <span>{size?.name}</span>

                      <span>|</span>

                      <span
                        className={styles.colorSquare}
                        style={{
                          backgroundColor: `#${color?.hex}`,
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
          disabled={!selectedVariantId}
          onClick={handleSave}
        >
          Сохранить
        </button>
      </div>

      <div className={styles.itemsContainer}>
        {items.map((item) => (
          <div
            key={item.productItemId}
            className={styles.productItem}
          >
            <span className={styles.productField}>
              ItemId: {item.productItemId}
            </span>

            <span className={styles.productField}>
              {item.productName}
            </span>

            <span className={styles.productField}>
              {item.sizeName}
            </span>

            <span className={styles.colorBlock}>
              <span
                className={styles.colorSquare}
                style={{
                  backgroundColor: `#${item.colorHex}`,
                }}
              />

              <span>{item.colorName}</span>
            </span>

            <span className={styles.productField}>
              {item.isSold ? 'sold' : 'available'}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}