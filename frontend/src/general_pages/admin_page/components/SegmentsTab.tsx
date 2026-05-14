import { useEffect, useState } from 'react';
import styles from './SegmentsTab.module.css';

type Segment = {
  id: number;
  name: string;
};

export default function SegmentTab() {
  const [segments, setSegments] = useState<Segment[]>([]);
  const [newName, setNewName] = useState('');

  // LOAD
  useEffect(() => {
    loadSegments();
  }, []);

  async function loadSegments() {
    const res = await fetch('/api/segments', {
      credentials: 'include',
    });

    const data: Segment[] = await res.json();
    setSegments(data);
  }

  // CREATE
  async function addSegment() {
    await fetch('/api/segments', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ name: newName }),
    });

    setNewName('');
    await loadSegments();
  }

  // UPDATE
  async function updateSegment(id: number, name: string) {
    const res = await fetch(`/api/segments/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({ id, name }),
    });

    if (res.status === 200) {
      await loadSegments();
    } else {
      alert(`Ошибка обновления: ${res.status}`);
    }
  }

  // DELETE
  async function deleteSegment(id: number) {
    const res = await fetch(`/api/segments/${id}`, {
      method: 'DELETE',
      credentials: 'include',
    });

    if (res.status === 204) {
      await loadSegments();
    } else {
      alert(`Ошибка удаления: ${res.status}`);
    }
  }

  return (
    <div className={styles.segmentTab}>
      {/* ADD ROW */}
      <div className={styles.addRow}>
        <input
          className={styles.segmentName}
          placeholder="Название"
          value={newName}
          onChange={(e) => setNewName(e.target.value)}
        />

        <button
          className={styles.btnAdd}
          disabled={!newName.trim()}
          onClick={addSegment}
        >
          Добавить
        </button>
      </div>

      {/* LIST */}
      <div className={styles.container}>
        {segments.map((s) => (
          <SegmentRow
            key={s.id}
            segment={s}
            onUpdate={updateSegment}
            onDelete={deleteSegment}
          />
        ))}
      </div>
    </div>
  );
}

function SegmentRow({
  segment,
  onUpdate,
  onDelete,
}: {
  segment: Segment;
  onUpdate: (id: number, name: string) => void;
  onDelete: (id: number) => void;
}) {
  const [value, setValue] = useState(segment.name);

  const hasChanges = value !== segment.name;

  return (
    <div className={styles.item}>
      <div className={styles.segmentId}>id: {segment.id}</div>

      <input
        className={styles.segmentName}
        value={value}
        onChange={(e) => setValue(e.target.value)}
      />

      {hasChanges && (
        <button
          className={styles.btnUpdate}
          onClick={() => onUpdate(segment.id, value)}
        >
          Обновить
        </button>
      )}

      <div className={styles.spacer} />

      <button
        className={styles.btnDelete}
        onClick={() => onDelete(segment.id)}
      >
        X
      </button>
    </div>
  );
}