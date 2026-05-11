import { useState } from 'react';
import styles from './TabsMenu.module.css';

import SegmentsTab from './SegmentsTab';
import SizesTab from './SizesTab';
import ColorsTab from './ColorsTab';
import ProductsTab from './ProductsTab';
import PurchasesTab from './PurchasesTab';
import ProductItems from './ProductItems';

type TabId =
  | 'segments-tab'
  | 'sizes-tab'
  | 'colors-tab'
  | 'products-tab'
  | 'purchases-tab'
  | 'product-items-tab';

const TABS: { id: TabId; label: string }[] = [
  { id: 'segments-tab', label: 'Категории' },
  { id: 'sizes-tab', label: 'Размеры' },
  { id: 'colors-tab', label: 'Цвета' },
  { id: 'products-tab', label: 'Продукты' },
  { id: 'purchases-tab', label: 'Покупки' },
  { id: 'product-items-tab', label: 'Товарные единицы' },
];

function renderTab(tab: TabId) {
  switch (tab) {
    case 'segments-tab':
      return <SegmentsTab />;
    case 'sizes-tab':
      return <SizesTab />;
    case 'colors-tab':
      return <ColorsTab />;
    case 'products-tab':
      return <ProductsTab />;
    case 'purchases-tab':
      return <PurchasesTab />;
    case 'product-items-tab':
      return <ProductItems />;
    default:
      return null;
  }
}

export default function TabsMenu() {
  const [activeTab, setActiveTab] = useState<TabId>('segments-tab');

  return (
    <div>
      <div className={styles.tabs}>
        {TABS.map((tab) => (
          <button
            key={tab.id}
            className={tab.id === activeTab ? styles.active : undefined}
            onClick={() => setActiveTab(tab.id)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      <div className={styles.tabContent}>{renderTab(activeTab)}</div>
    </div>
  );
}