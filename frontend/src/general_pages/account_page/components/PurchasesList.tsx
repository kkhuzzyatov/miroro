import styles from "./PurchasesList.module.css";
import { useEffect, useState } from "react";

type PurchaseItem = {
  productName: string;
  sizeName: string;
  colorName: string;
  price: number;
};

type Purchase = {
  id: number;
  targetAddress: string;
  status: "NEW" | "PROCESSING" | "DELIVERED" | "CANCELLED";
  purchaseItems: PurchaseItem[];
};

const statusClassMap: Record<Purchase["status"], string> = {
  NEW: styles.statusNew,
  PROCESSING: styles.statusProcessing,
  DELIVERED: styles.statusDelivered,
  CANCELLED: styles.statusCancelled,
};

export default function PurchasesList() {
  const [purchases, setPurchases] = useState<Purchase[] | null>(null);
  const [error, setError] = useState(false);

  useEffect(() => {
    loadPurchases();
  }, []);

  const loadPurchases = async () => {
    try {
      const res = await fetch("/api/purchases");
      if (!res.ok) throw new Error();

      const data = await res.json();
      setPurchases(data);
    } catch (e) {
      console.error(e);
      setError(true);
    }
  };

  if (error) {
    return <div className={styles.purchasesError}>Ошибка загрузки покупок</div>;
  }

  if (!purchases) {
    return <div className={styles.purchasesLoading}>Loading...</div>;
  }

  if (purchases.length === 0) {
    return <div className={styles.purchasesEmpty}>У вас пока нет покупок</div>;
  }

  return (
    <div className={styles.purchasesList}>
      {purchases.map((p) => {
        const total = p.purchaseItems.reduce((sum, i) => sum + i.price, 0);

        return (
          <div key={p.id} className={styles.purchaseCard}>
            <div className={styles.purchaseHeader}>
              <div className={styles.purchaseAddress}>📦 {p.targetAddress}</div>

              <div className={`${styles.purchaseStatus} ${statusClassMap[p.status]}`}>
                {p.status}
              </div>
            </div>

            <div className={styles.purchaseItems}>
              {p.purchaseItems.map((i, idx) => (
                <div key={idx} className={styles.purchaseItem}>
                  <div className={styles.purchaseItemInfo}>
                    <div className={styles.purchaseProductName}>
                      {i.productName}
                    </div>

                    <div className={styles.purchaseMeta}>
                      Размер: {i.sizeName} · Цвет: {i.colorName}
                    </div>
                  </div>

                  <div className={styles.purchasePrice}>
                    {i.price.toFixed(2)} ₽
                  </div>
                </div>
              ))}
            </div>

            <div className={styles.purchaseTotal}>
              Итого: {total.toFixed(2)} ₽
            </div>
          </div>
        );
      })}
    </div>
  );
}