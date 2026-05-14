import { useEffect, useState } from 'react';
import styles from './ColorsTab.module.css';

import { fetchColors } from "../../../api_client/colors";
import type { Color } from "../../../api_client/colors";

export default function ColorsTab() {
  const [colors, setColors] = useState<Color[]>([]);

  const [name, setName] = useState('');
  const [hex, setHex] = useState('');
  const [picker, setPicker] = useState('#000000');

  useEffect(() => {
    loadColors();
  }, []);

  async function loadColors() {
    const data = await fetchColors();
    setColors(data);
  }

  function normalizeHex(v: string) {
    return v.replace('#', '').toUpperCase();
  }

  function updateAddState() {
    return !name.trim() || !hex.trim();
  }

  async function addColor() {
    const payload = {
      name: name.trim(),
      hex: normalizeHex(hex),
    };

    const res = await fetch('/api/colors', {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (res.status !== 201) {
      alert('Ошибка создания цвета');
      return;
    }

    setName('');
    setHex('');
    setPicker('#000000');
    await loadColors();
  }

  async function updateColor(id: number, nameVal: string, hexVal: string) {
    const payload = {
      id,
      name: nameVal.trim(),
      hex: normalizeHex(hexVal),
    };

    const res = await fetch(`/api/colors/${id}`, {
      method: 'PUT',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (res.status === 200) {
      await loadColors();
    } else {
      alert(`Ошибка обновления цвета: ${res.status}`);
    }
  }

  async function deleteColor(id: number) {
    const res = await fetch(`/api/colors/${id}`, {
      method: 'DELETE',
      credentials: 'include',
    });

    if (res.status === 204) {
      await loadColors();
    } else {
      alert(`Ошибка удаления цвета: ${res.status}`);
    }
  }

  return (
    <div className={styles.colorsTab}>
      {/* ADD */}
      <div className={styles.addRow}>
        <input
          className={styles.colorName}
          placeholder="Название"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />

        <input
          className={styles.colorHex}
          placeholder="HEX"
          value={hex}
          onChange={(e) => setHex(e.target.value)}
        />

        <input
          type="color"
          className={styles.colorPicker}
          value={picker}
          onChange={(e) => {
            setPicker(e.target.value);
            setHex(normalizeHex(e.target.value));
          }}
        />

        <button
          className={styles.btnAdd}
          disabled={updateAddState()}
          onClick={addColor}
        >
          Добавить
        </button>
      </div>

      {/* LIST */}
      <div className={styles.container}>
        {colors.map((c) => (
          <ColorRow
            key={c.id}
            color={c}
            onUpdate={updateColor}
            onDelete={deleteColor}
          />
        ))}
      </div>
    </div>
  );
}

/* ================= ROW ================= */

function ColorRow({
  color,
  onUpdate,
  onDelete,
}: {
  color: Color;
  onUpdate: (id: number, name: string, hex: string) => void;
  onDelete: (id: number) => void;
}) {
  const [name, setName] = useState(color.name);
  const [hex, setHex] = useState(color.hex.toUpperCase());
  const [picker, setPicker] = useState('#' + color.hex);

  function normalizeHex(v: string) {
    return v.replace('#', '').toUpperCase();
  }

  const changed =
    name !== color.name || normalizeHex(hex) !== color.hex;

  return (
    <div className={styles.item}>
      <div className={styles.colorId}>id: {color.id}</div>

      <input
        className={styles.colorName}
        value={name}
        onChange={(e) => setName(e.target.value)}
      />

      <input
        className={styles.colorHex}
        value={hex}
        onChange={(e) => {
          setHex(e.target.value);
          setPicker('#' + normalizeHex(e.target.value));
        }}
      />

      <input
        type="color"
        className={styles.colorPicker}
        value={picker}
        onChange={(e) => {
          setPicker(e.target.value);
          setHex(normalizeHex(e.target.value));
        }}
      />

      {changed && (
        <button
          className={styles.btnUpdate}
          onClick={() => onUpdate(color.id, name, hex)}
        >
          Обновить
        </button>
      )}

      <div className={styles.spacer} />

      <button
        className={styles.btnDelete}
        onClick={() => onDelete(color.id)}
      >
        X
      </button>
    </div>
  );
}