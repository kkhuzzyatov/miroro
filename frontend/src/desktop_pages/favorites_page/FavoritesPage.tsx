import styles from './FavoritesPage.module.css';
import ProductsContainer from "./components/ProductsContainer";

export default function FavoritesPage() {
  return (
    <>

      <main className={styles.pageContent}>
        <ProductsContainer />
      </main>
    </>
  );
}