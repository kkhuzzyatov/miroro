import { ChangeEvent, FormEvent, useEffect, useMemo, useState } from 'react';
import styles from './ProductsTab.module.css';

type Segment = {
  id: number;
  name: string;
};

type Size = {
  id: number;
  name: string;
};

type Color = {
  id: number;
  name: string;
  hex: string;
};

type Variant = {
  size_id: number;
  color_id: number;
};

type ProductImage = {
  path: string;
  is_main: boolean;
  color_id: number | null;
};

type Product = {
  id: number;
  name: string;
  description: string;
  price: number;
  segment_id: number;
  variants: Variant[];
  images: ProductImage[];
};

type ImageRow = {
  file: File | null;
  preview: string;
  is_main: boolean;
  color_id: number | null;
  existing?: boolean;
  path?: string;
};

export default function ProductsTab() {
  const [segments, setSegments] = useState<Segment[]>([]);
  const [sizes, setSizes] = useState<Size[]>([]);
  const [colors, setColors] = useState<Color[]>([]);
  const [products, setProducts] = useState<Product[]>([]);

  const [editedId, setEditedId] = useState<number | null>(null);

  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [price, setPrice] = useState('');
  const [segmentId, setSegmentId] = useState('');

  const [variants, setVariants] = useState<Variant[]>([]);
  const [images, setImages] = useState<ImageRow[]>([]);

  const [openedProducts, setOpenedProducts] = useState<number[]>([]);

  useEffect(() => {
    loadAll();
  }, []);

  async function loadAll() {
    await Promise.all([
      loadSegments(),
      loadSizes(),
      loadColors(),
      loadProducts(),
    ]);
  }

  async function loadSegments() {
    const response = await fetch('/api/segments');
    const data = await response.json();
    setSegments(data);
  }

  async function loadSizes() {
    const response = await fetch('/api/sizes');
    const data = await response.json();
    setSizes(data);
  }

  async function loadColors() {
    const response = await fetch('/api/colors');
    const data = await response.json();
    setColors(data);
  }

  async function loadProducts() {
    const response = await fetch('/api/products');
    const data = await response.json();
    setProducts(data);
  }

  function getSegmentName(id: number) {
    return segments.find((s) => s.id === id)?.name ?? `ID ${id}`;
  }

  function getSizeName(id: number) {
    return sizes.find((s) => s.id === id)?.name ?? `ID ${id}`;
  }

  function getColor(id: number) {
    return colors.find((c) => c.id === id);
  }

  function addVariant() {
    setVariants((prev) => [
      ...prev,
      {
        size_id: 0,
        color_id: 0,
      },
    ]);
  }

  function updateVariant(
    index: number,
    key: keyof Variant,
    value: number,
  ) {
    setVariants((prev) =>
      prev.map((variant, i) =>
        i === index
          ? {
              ...variant,
              [key]: value,
            }
          : variant,
      ),
    );
  }

  function removeVariant(index: number) {
    setVariants((prev) => prev.filter((_, i) => i !== index));
  }

  function addImage() {
    setImages((prev) => [
      ...prev,
      {
        file: null,
        preview: '',
        is_main: prev.length === 0,
        color_id: null,
      },
    ]);
  }

  function updateImage(
    index: number,
    data: Partial<ImageRow>,
  ) {
    setImages((prev) =>
      prev.map((image, i) => {
        if (i !== index) {
          if (data.is_main) {
            return {
              ...image,
              is_main: false,
            };
          }

          return image;
        }

        return {
          ...image,
          ...data,
        };
      }),
    );
  }

  function removeImage(index: number) {
    setImages((prev) => prev.filter((_, i) => i !== index));
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();

    try {
      const formData = new FormData();

      const product = {
        name,
        description,
        price: Number(price),
        segment_id: Number(segmentId),
        variants,
        images: images.map((image) => ({
          path:
            image.file?.name ??
            image.path ??
            '',
          is_main: image.is_main,
          color_id: image.color_id,
        })),
      };

      formData.append(
        'product',
        new Blob([JSON.stringify(product)], {
          type: 'application/json',
        }),
      );

      images.forEach((image) => {
        if (image.file) {
          formData.append('images', image.file);
        }
      });

      const response = await fetch(
        editedId !== null
          ? `/api/products?id=${editedId}`
          : '/api/products',
        {
          method: editedId !== null ? 'PUT' : 'POST',
          body: formData,
        },
      );

      if (!response.ok) {
        throw new Error(await response.text());
      }

      cleanForm();
      await loadProducts();
    } catch (error) {
      alert(
        error instanceof Error
          ? error.message
          : 'Ошибка',
      );
    }
  }

  async function handleDelete(id: number) {
    const confirmed = confirm(
      'Удалить продукт?',
    );

    if (!confirmed) {
      return;
    }

    const response = await fetch(
      `/api/products?id=${id}`,
      {
        method: 'DELETE',
      },
    );

    if (!response.ok) {
      alert('Ошибка удаления');
      return;
    }

    await loadProducts();
  }

  function handleEdit(product: Product) {
    setEditedId(product.id);

    setName(product.name);
    setDescription(product.description);
    setPrice(String(product.price));
    setSegmentId(String(product.segment_id));

    setVariants(product.variants);

    setImages(
      product.images.map((image) => ({
        file: null,
        preview: image.path,
        is_main: image.is_main,
        color_id: image.color_id,
        existing: true,
        path: image.path,
      })),
    );

    window.scrollTo({
      top: 0,
      behavior: 'smooth',
    });
  }

  function cleanForm() {
    setEditedId(null);

    setName('');
    setDescription('');
    setPrice('');
    setSegmentId('');

    setVariants([]);
    setImages([]);
  }

  function toggleProduct(id: number) {
    setOpenedProducts((prev) =>
      prev.includes(id)
        ? prev.filter((v) => v !== id)
        : [...prev, id],
    );
  }

  const groupedImages = useMemo(() => {
    const groups: Record<string, ImageRow[]> = {};

    images.forEach((image) => {
      const key = String(image.color_id ?? 'none');

      if (!groups[key]) {
        groups[key] = [];
      }

      groups[key].push(image);
    });

    return groups;
  }, [images]);

  return (
    <div className={styles.productsTab}>
      <div className={styles.productFormContainer}>
        <form onSubmit={handleSubmit}>
          <div className={styles.formGroup}>
            <label>Название</label>

            <input
              value={name}
              onChange={(e) =>
                setName(e.target.value)
              }
              required
            />
          </div>

          <div className={styles.formGroup}>
            <label>Цена</label>

            <input
              type="number"
              min="0"
              step="0.01"
              value={price}
              onChange={(e) =>
                setPrice(e.target.value)
              }
              required
            />
          </div>

          <div className={styles.formGroup}>
            <label>Категория</label>

            <select
              value={segmentId}
              onChange={(e) =>
                setSegmentId(e.target.value)
              }
              required
            >
              <option value="">
                Выберите категорию
              </option>

              {segments.map((segment) => (
                <option
                  key={segment.id}
                  value={segment.id}
                >
                  {segment.name}
                </option>
              ))}
            </select>
          </div>

          <div className={styles.formGroup}>
            <label>Описание</label>

            <textarea
              rows={6}
              value={description}
              onChange={(e) =>
                setDescription(e.target.value)
              }
              required
            />
          </div>

          <div className={styles.formGroup}>
            <label>Вариации</label>

            <div className={styles.variantsContainer}>
              {variants.map((variant, index) => (
                <div
                  key={index}
                  className={styles.variantRow}
                >
                  <select
                    value={
                      variant.size_id || ''
                    }
                    onChange={(e) =>
                      updateVariant(
                        index,
                        'size_id',
                        Number(e.target.value),
                      )
                    }
                  >
                    <option value="">
                      Размер
                    </option>

                    {sizes.map((size) => (
                      <option
                        key={size.id}
                        value={size.id}
                      >
                        {size.name}
                      </option>
                    ))}
                  </select>

                  <select
                    value={
                      variant.color_id || ''
                    }
                    onChange={(e) =>
                      updateVariant(
                        index,
                        'color_id',
                        Number(e.target.value),
                      )
                    }
                  >
                    <option value="">
                      Цвет
                    </option>

                    {colors.map((color) => (
                      <option
                        key={color.id}
                        value={color.id}
                      >
                        {color.name}
                      </option>
                    ))}
                  </select>

                  <button
                    type="button"
                    className={styles.deleteButton}
                    onClick={() =>
                      removeVariant(index)
                    }
                  >
                    ✖
                  </button>
                </div>
              ))}
            </div>

            <button
              type="button"
              className={styles.secondaryButton}
              onClick={addVariant}
            >
              + Добавить вариацию
            </button>
          </div>

          <div className={styles.formGroup}>
            <label>Изображения</label>

            {Object.entries(groupedImages).map(
              ([group, groupImages]) => (
                <div
                  key={group}
                  className={styles.imageGroup}
                >
                  <div className={styles.imageGroupTitle}>
                    {group === 'none'
                      ? 'Без цвета'
                      : getColor(
                          Number(group),
                        )?.name}
                  </div>

                  <div
                    className={
                      styles.imagesContainer
                    }
                  >
                    {groupImages.map(
                      (image, index) => {
                        const realIndex =
                          images.indexOf(image);

                        return (
                          <div
                            key={realIndex}
                            className={
                              styles.imageRow
                            }
                          >
                            {image.preview && (
                              <img
                                src={
                                  image.preview
                                }
                                className={
                                  styles.preview
                                }
                              />
                            )}

                            <input
                              type="file"
                              accept="image/*"
                              onChange={(
                                e: ChangeEvent<HTMLInputElement>,
                              ) => {
                                const file =
                                  e.target
                                    .files?.[0];

                                if (!file) {
                                  return;
                                }

                                updateImage(
                                  realIndex,
                                  {
                                    file,
                                    preview:
                                      URL.createObjectURL(
                                        file,
                                      ),
                                  },
                                );
                              }}
                            />

                            <label
                              className={
                                styles.mainCheckbox
                              }
                            >
                              <input
                                type="checkbox"
                                checked={
                                  image.is_main
                                }
                                onChange={(
                                  e,
                                ) =>
                                  updateImage(
                                    realIndex,
                                    {
                                      is_main:
                                        e
                                          .target
                                          .checked,
                                    },
                                  )
                                }
                              />
                              Главная
                            </label>

                            <select
                              value={
                                image.color_id ??
                                ''
                              }
                              onChange={(e) =>
                                updateImage(
                                  realIndex,
                                  {
                                    color_id:
                                      e.target
                                        .value
                                        ? Number(
                                            e
                                              .target
                                              .value,
                                          )
                                        : null,
                                  },
                                )
                              }
                            >
                              <option value="">
                                Цвет
                              </option>

                              {colors.map(
                                (color) => (
                                  <option
                                    key={
                                      color.id
                                    }
                                    value={
                                      color.id
                                    }
                                  >
                                    {color.name}
                                  </option>
                                ),
                              )}
                            </select>

                            <button
                              type="button"
                              className={
                                styles.deleteButton
                              }
                              onClick={() =>
                                removeImage(
                                  realIndex,
                                )
                              }
                            >
                              ✖
                            </button>
                          </div>
                        );
                      },
                    )}
                  </div>
                </div>
              ),
            )}

            <button
              type="button"
              className={styles.secondaryButton}
              onClick={addImage}
            >
              + Добавить изображение
            </button>
          </div>

          <div className={styles.actions}>
            <button
              type="submit"
              className={styles.saveButton}
            >
              {editedId !== null
                ? 'Обновить'
                : 'Сохранить'}
            </button>

            <button
              type="button"
              className={styles.cancelButton}
              onClick={cleanForm}
            >
              Отменить
            </button>
          </div>
        </form>
      </div>

      <div className={styles.productsContainer}>
        {products.map((product) => {
          const opened =
            openedProducts.includes(
              product.id,
            );

          return (
            <div
              key={product.id}
              className={styles.productBlock}
            >
              <div
                className={
                  styles.productHeader
                }
              >
                <button
                  onClick={() =>
                    toggleProduct(
                      product.id,
                    )
                  }
                >
                  {opened
                    ? 'Свернуть'
                    : 'Раскрыть'}
                </button>

                <button
                  className={
                    styles.editButton
                  }
                  onClick={() =>
                    handleEdit(product)
                  }
                >
                  Редактировать
                </button>

                <button
                  className={
                    styles.deleteButton
                  }
                  onClick={() =>
                    handleDelete(
                      product.id,
                    )
                  }
                >
                  Удалить
                </button>
              </div>

              <div
                className={
                  styles.productName
                }
              >
                {product.name}
              </div>

              {opened && (
                <div
                  className={
                    styles.productDetails
                  }
                >
                  <div>
                    id: {product.id}
                  </div>

                  <div>
                    Цена:{' '}
                    {product.price}
                  </div>

                  <div>
                    Категория:{' '}
                    {getSegmentName(
                      product.segment_id,
                    )}
                  </div>

                  <div
                    dangerouslySetInnerHTML={{
                      __html:
                        product.description,
                    }}
                  />

                  <ul>
                    {product.variants.map(
                      (
                        variant,
                        index,
                      ) => {
                        const color =
                          getColor(
                            variant.color_id,
                          );

                        return (
                          <li
                            key={index}
                          >
                            Размер:{' '}
                            {getSizeName(
                              variant.size_id,
                            )}{' '}
                            | Цвет:{' '}
                            <span
                              className={
                                styles.colorSquare
                              }
                              style={{
                                backgroundColor:
                                  `#${color?.hex}`,
                              }}
                            />
                            {color?.name}
                          </li>
                        );
                      },
                    )}
                  </ul>

                  <div
                    className={
                      styles.productImages
                    }
                  >
                    {product.images.map(
                      (
                        image,
                        index,
                      ) => (
                        <img
                          key={index}
                          src={image.path}
                          className={
                            styles.productImage
                          }
                        />
                      ),
                    )}
                  </div>
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}