import styles from './SizeSelector.module.css';

type Props = {
  sizes: any[];
  variants: any[];
  selectedSize: number | null;
  onSelect: (id: number) => void;
};

export default function SizeSelector({
  sizes,
  variants,
  selectedSize,
  onSelect,
}: Props) {
  const size_ids = [...new Set(variants.map(v => v.size_id))];

  return (
    <div className={styles.sizes}>
      {size_ids.map(id => {
        const hasStock = variants.some(
          v => v.size_id === id && v.quantity > 0
        );

        return (
          <button
            key={id}
            className={`${styles.sizeBtn} ${
              selectedSize === id ? styles.selected : ""
            }`}
            disabled={!hasStock}
            onClick={() => onSelect(id)}
          >
            {sizes.find(s => s.id === id)?.name ?? id}
          </button>
        );
      })}
    </div>
  );
}