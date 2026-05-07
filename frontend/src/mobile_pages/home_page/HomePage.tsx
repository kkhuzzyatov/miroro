import styles from './HomePage.module.css';
import ProductsContainer from "./components/ProductsContainer";

export default function HomePage() {
  return (
    <>

      <main className={styles.pageContent}>
        <ProductsContainer />
      </main>
    </>
  );
}