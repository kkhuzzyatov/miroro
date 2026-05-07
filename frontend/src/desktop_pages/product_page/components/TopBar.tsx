import styles from './TopBar.module.css';
import { useNavigate } from "react-router-dom";
import FavoriteButton from "./FavoriteButton";

type Props = {
  productId: number;
  currentVariantId: number;
};

export default function TopBar({ productId, currentVariantId }: Props) {
  const navigate = useNavigate();

  return (
    <div className={styles.topBar}>
      <button className={styles.backBtn} onClick={() => navigate(-1)}>
        ←
      </button>
      <div className={styles.favoriteContainer}>
        <FavoriteButton
          productId={productId}
          variantId={currentVariantId}
        />
      </div>
    </div>
  );
}