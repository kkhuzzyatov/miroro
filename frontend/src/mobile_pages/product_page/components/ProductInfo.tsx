import styles from './ProductInfo.module.css';

export function ProductInfo({ name, price, description }) {
  return (
    <div className={styles.productInfo}>
      <div className={styles.productTitle}>{name}</div>

      <div className={styles.productPrice}>{price} ₽</div>

      <div
        className={styles.productDescription}
        dangerouslySetInnerHTML={{ __html: description || "" }}
      />
    </div>
  );
}