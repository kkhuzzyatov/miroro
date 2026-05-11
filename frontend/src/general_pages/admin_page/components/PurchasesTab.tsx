import { useEffect, useState } from 'react';
import styles from './PurchasesTab.module.css';

type PurchaseStatus = {
  id: number;
  name: string;
};

type PurchaseItem = {
  id: number;
  productName: string;
  sizeName: string;
  colorName: string;
  price: number;
};

type Purchase = {
  purchaseId: number;
  targetAddress: string;
  userEmail: string;
  status: string;
  purchaseItems: PurchaseItem[];
};

export default function PurchasesTab() {
  const [purchases, setPurchases] = useState<Purchase[]>([]);
  const [statuses, setStatuses] = useState<PurchaseStatus[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    try {
      setLoading(true);
      setError('');

      const [statusesResponse, purchasesResponse] =
        await Promise.all([
          fetch('/api/purchase-statuses'),
          fetch('/api/purchases/all'),
        ]);

      if (!statusesResponse.ok) {
        throw new Error(
          'Ошибка загрузки статусов',
        );
      }

      if (!purchasesResponse.ok) {
        throw new Error(
          'Ошибка загрузки покупок',
        );
      }

      const statusesData =
        await statusesResponse.json();

      const purchasesData =
        await purchasesResponse.json();

      setStatuses(statusesData);
      setPurchases(purchasesData);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : 'Ошибка',
      );
    } finally {
      setLoading(false);
    }
  }

  async function updateStatus(
    purchaseId: number,
    newStatus: string,
  ) {
    try {
      const response = await fetch(
        `/api/purchases?id=${purchaseId}&new_status=${encodeURIComponent(newStatus)}`,
        {
          method: 'PATCH',
        },
      );

      if (!response.ok) {
        throw new Error(
          await response.text(),
        );
      }

      setPurchases((prev) =>
        prev.map((purchase) =>
          purchase.purchaseId ===
          purchaseId
            ? {
                ...purchase,
                status: newStatus,
              }
            : purchase,
        ),
      );
    } catch (err) {
      alert(
        err instanceof Error
          ? err.message
          : 'Не удалось обновить статус',
      );
    }
  }

  if (loading) {
    return (
      <div className={styles.purchasesTab}>
        <div className={styles.message}>
          Загрузка...
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className={styles.purchasesTab}>
        <div className={styles.error}>
          {error}
        </div>
      </div>
    );
  }

  return (
    <div className={styles.purchasesTab}>
      {purchases.length === 0 ? (
        <div className={styles.message}>
          Покупок нет
        </div>
      ) : (
        <div className={styles.purchasesContainer}>
          {purchases.map((purchase) => {
            const total =
              purchase.purchaseItems.reduce(
                (sum, item) =>
                  sum + item.price,
                0,
              );

            return (
              <div
                key={purchase.purchaseId}
                className={styles.purchaseCard}
              >
                <div
                  className={
                    styles.purchaseHeader
                  }
                >
                  <div>
                    <strong>
                      id:{' '}
                      {
                        purchase.purchaseId
                      }
                    </strong>
                  </div>

                  <div>
                    Адрес доставки:{' '}
                    {
                      purchase.targetAddress
                    }
                  </div>

                  <div>
                    Почта покупателя:{' '}
                    {purchase.userEmail}
                  </div>

                  <div
                    className={
                      styles.statusRow
                    }
                  >
                    <span>Статус:</span>

                    <select
                      value={
                        purchase.status
                      }
                      onChange={(e) =>
                        updateStatus(
                          purchase.purchaseId,
                          e.target.value,
                        )
                      }
                    >
                      {statuses.map(
                        (status) => (
                          <option
                            key={
                              status.id
                            }
                            value={
                              status.name
                            }
                          >
                            {
                              status.name
                            }
                          </option>
                        ),
                      )}
                    </select>
                  </div>
                </div>

                <ul
                  className={
                    styles.itemsList
                  }
                >
                  {purchase.purchaseItems.map(
                    (item) => (
                      <li
                        key={item.id}
                        className={
                          styles.item
                        }
                      >
                        <div>
                          <strong>
                            товар:
                          </strong>{' '}
                          {
                            item.productName
                          }
                        </div>

                        <div>
                          <strong>
                            размер:
                          </strong>{' '}
                          {
                            item.sizeName
                          }
                        </div>

                        <div>
                          <strong>
                            цвет:
                          </strong>{' '}
                          {
                            item.colorName
                          }
                        </div>

                        <div>
                          <strong>
                            цена:
                          </strong>{' '}
                          {item.price.toFixed(
                            2,
                          )}{' '}
                          ₽
                        </div>

                        <div>
                          <strong>
                            ItemId:
                          </strong>{' '}
                          {item.id}
                        </div>
                      </li>
                    ),
                  )}
                </ul>

                <div className={styles.total}>
                  Итого:{' '}
                  {total.toFixed(2)} ₽
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}