import styles from './AddToCartButton.module.css';

type Props = {
  disabled?: boolean;
  onClick?: () => void;
  text?: string;
};

export default function AddToCartButton({
  disabled = false,
  onClick,
  text = "В корзину",
}: Props) {
  return (
    <button
      className={styles.addToCartBtn}
      disabled={disabled}
      onClick={onClick}
    >
      {text}
    </button>
  );
}