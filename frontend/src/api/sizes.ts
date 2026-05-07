export interface Size {
  id: number;
  name: string;
}

// получить все размеры
export async function fetchSizes(): Promise<Size[]> {
  const res = await fetch("/api/sizes");

  if (!res.ok) {
    throw new Error("Failed to fetch sizes");
  }

  return res.json();
}

// получить размер по id (один объект)
export async function fetchSizeById(id: number): Promise<Size | null> {
  const res = await fetch(`/api/sizes?id=${id}`);

  if (res.status === 404) return null;

  if (!res.ok) {
    throw new Error("Failed to fetch size");
  }

  return res.json();
}

// получить ТОЛЬКО name по id
export async function fetchSizeNameById(id: number): Promise<string | null> {
  const size = await fetchSizeById(id);
  return size ? size.name : null;
}