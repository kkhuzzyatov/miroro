import styles from "./AccountPage.module.css";
import PurchasesList from "./components/PurchasesList";

export default function AccountPage() {
  return (
    <div className={styles.accountPage}>

      <section className={styles.accountSection}>
        <PurchasesList />
      </section>
    </div>
  );
}