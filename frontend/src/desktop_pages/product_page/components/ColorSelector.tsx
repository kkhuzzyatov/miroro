import styles from './ColorSelector.module.css';

type Props = {
  colors: any[];
  variants: any[];
  selectedSize: number;
  selectedColor: number | null;
  onSelect: (id: number) => void;
};

export default function ColorSelector({
  colors,
  variants,
  selectedSize,
  selectedColor,
  onSelect,
}: Props) {
  const availableVariants = variants.filter(
    v => v.size_id === selectedSize && v.quantity > 0
  );

  const color_ids = [...new Set(availableVariants.map(v => v.color_id))];

  return (
    <div className={styles.colors}>
      {color_ids.map(id => (
        <button
          key={id}
          className={`${styles.colorBtn} ${
            selectedColor === id ? styles.selected : ""
          }`}
          style={{
            background: `#${colors.find(c => c.id === id)?.hex ?? "000000"}`,
          }}
          onClick={() => onSelect(id)}
        />
      ))}
    </div>
  );
}