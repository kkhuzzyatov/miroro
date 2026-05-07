import styles from './ProductImageGallery.module.css';
import { useEffect, useMemo, useState } from "react";

type Image = {
  path: string;
  is_main?: boolean;
};

interface Props {
  images: Image[];
}

export function ProductImageGallery({ images }: Props) {
  const list = useMemo(() => images ?? [], [images]);

  const initialIndex = useMemo(() => {
    if (!list.length) return 0;
    const idx = list.findIndex(i => i.is_main);
    return idx >= 0 ? idx : 0;
  }, [list]);

  const [activeIndex, setActiveIndex] = useState(initialIndex);

  useEffect(() => {
    setActiveIndex(initialIndex);
  }, [initialIndex]);

  if (!list.length) return null;

  return (
    <div className={styles.wrapper}>

      <div className={styles.sidebar}>
        {list.map((img, i) => (
          <button
            key={i}
            type="button"
            className={`${styles.item} ${i === activeIndex ? styles.active : ""}`}
            onClick={() => setActiveIndex(i)}
          >
            <img src={img.path} className={styles.thumb} alt="" />
          </button>
        ))}
      </div>

      <div className={styles.main}>
        <img
          src={list[activeIndex].path}
          className={styles.mainImage}
          alt="product"
        />
      </div>

    </div>
  );
}