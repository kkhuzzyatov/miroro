import { useEffect, useState } from 'react';
import styles from './SizesTab.module.css';

import { fetchSizes } from "../../../api_client/sizes";
import type { Size } from "../../../api_client/sizes";

export default function SizesTab() {
  const [sizes, setSizes] = useState<Size[]>([]);
  const [newName, setNewName] = useState('');

  useEffect(() => {
    loadSizes();
  }, []);

  async function loadSizes() {
    const data = await fetchSizes();
    setSizes(data);
  }

  async function addSize() {
    const res = await fetch('/api/sizes', {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        id: 0,
        name: newName.trim(),
      }),
    });

    if (res.status !== 201) {
      alert('Ошибка создания размера');
      return;
    }

    setNewName('');
    await loadSizes();
  }

  async function updateSize(id: number, name: string) {
    const res = await fetch(`/api/sizes/${id}`, {
      method: 'PUT',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ id, name }),
    });

    if (res.status === 200) {
      await loadSizes();
    } else {
      alert(`Ошибка обновления размера: ${res.status}`);
    }
  }

  async function deleteSize(id: number) {
    const res = await fetch(`/api/sizes/${id}`, {
      method: 'DELETE',
      credentials: 'include',
    });

    if (res.status === 204) {
      await loadSizes();
    } else {
      alert(`Ошибка удаления размера: ${res.status}`);
    }
  }

  return (
    <div className={styles.sizesTab}>
      {/* ADD */}
      <div className={styles.addRow}>
        <input
          className={styles.sizeName}
          placeholder="Название"
          value={newName}
          onChange={(e) => setNewName(e.target.value)}
        />

        <button
          className={styles.btnAdd}
          disabled={!newName.trim()}
          onClick={addSize}
        >
          Добавить
        </button>
      </div>

      {/* LIST */}
      <div className={styles.container}>
        {sizes.map((s) => (
          <SizeRow
            key={s.id}
            size={s}
            onUpdate={updateSize}
            onDelete={deleteSize}
          />
        ))}
      </div>
    </div>
  );
}

/* ================= ROW ================= */

function SizeRow({
  size,
  onUpdate,
  onDelete,
}: {
  size: { id: number; name: string };
  onUpdate: (id: number, name: string) => void;
  onDelete: (id: number) => void;
}) {
  const [value, setValue] = useState(size.name);

  const changed = value !== size.name;

  return (
    <div className={styles.item}>
      <div className={styles.sizeId}>id: {size.id}</div>

      <input
        className={styles.sizeName}
        value={value}
        onChange={(e) => setValue(e.target.value)}
      />

      {changed && (
        <button
          className={styles.btnUpdate}
          onClick={() => onUpdate(size.id, value)}
        >
          Обновить
        </button>
      )}

      <div className={styles.spacer} />

      <button
        className={styles.btnDelete}
        onClick={() => onDelete(size.id)}
      >
        X
      </button>
    </div>
  );
}