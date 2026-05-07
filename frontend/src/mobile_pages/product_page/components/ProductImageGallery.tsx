import styles from './ProductImageGallery.module.css';
import { useEffect, useMemo, useRef, useState } from "react";

type Image = {
  path: string;
  is_main?: boolean;
};

interface Props {
  images: Image[];
}

export function ProductImageGallery({ images }: Props) {
  const list = useMemo(() => {
    return Array.isArray(images) ? images : [];
  }, [images]);

  const initialIndex = useMemo(() => {
    if (!list.length) return 0;
    const mainIndex = list.findIndex(i => i.is_main);
    return mainIndex >= 0 ? mainIndex : 0;
  }, [list]);

  const [activeIndex, setActiveIndex] = useState(0);

  const startX = useRef<number>(0);
  const isSwiping = useRef(false);

  // синхронизация activeIndex при изменении списка
  useEffect(() => {
    setActiveIndex(initialIndex);
  }, [initialIndex]);

  // защита от выхода за границы
  useEffect(() => {
    if (activeIndex >= list.length) {
      setActiveIndex(0);
    }
  }, [list, activeIndex]);

  const prev = () => {
    setActiveIndex(i => (i === 0 ? list.length - 1 : i - 1));
  };

  const next = () => {
    setActiveIndex(i => (i === list.length - 1 ? 0 : i + 1));
  };

  const onTouchStart = (e: React.TouchEvent) => {
    startX.current = e.touches[0].clientX;
    isSwiping.current = false;
  };

  const onTouchMove = () => {
    isSwiping.current = true;
  };

  const onTouchEnd = (e: React.TouchEvent) => {
    if (!isSwiping.current) return;

    const endX = e.changedTouches[0].clientX;
    const diff = endX - startX.current;
    const threshold = 40;

    if (Math.abs(diff) < threshold) {
      isSwiping.current = false;
      return;
    }

    if (diff < 0) next();
    else prev();

    isSwiping.current = false;
  };

  if (!list.length) return null;

  const currentImage = list[activeIndex];

  if (!currentImage) return null;

  return (
    <div className={styles.gallery}>
      <div
        className={styles.galleryImageWrapper}
        onTouchStart={onTouchStart}
        onTouchMove={onTouchMove}
        onTouchEnd={onTouchEnd}
      >
        <img
          key={activeIndex}
          src={currentImage.path}
          className={styles.galleryImage}
          alt="product"
        />
      </div>

      <div className={styles.galleryDots}>
        {list.map((_, i) => (
          <button
            key={i}
            className={`${styles.dot} ${
              i === activeIndex ? styles.active : ""
            }`}
            onClick={() => setActiveIndex(i)}
          />
        ))}
      </div>
    </div>
  );
}