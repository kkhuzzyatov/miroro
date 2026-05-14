export interface Color {
  id: number;
  name: string;
  hex: string;
}

interface ApiColor {
  id: number;
  name: string;
  hex: string;
}

function normalizeHex(hex: string): string {
  if (!hex) return "#000000";
  return hex.startsWith("#") ? hex : `#${hex}`;
}

// получить все цвета
export async function fetchColors(): Promise<Color[]> {
  const res = await fetch("/api/colors");

  if (!res.ok) {
    throw new Error("Failed to fetch colors");
  }

  const data: ApiColor[] = await res.json();

  return data.map(c => ({
    ...c,
    hex: normalizeHex(c.hex),
  }));
}

// получить цвет по id
export async function fetchColorById(id: number): Promise<Color | null> {
  const res = await fetch(`/api/colors?id=${id}`);

  if (res.status === 404) return null;

  if (!res.ok) {
    throw new Error("Failed to fetch color");
  }

  const data: ApiColor = await res.json();

  return {
    ...data,
    hex: normalizeHex(data.hex),
  };
}

// получить ТОЛЬКО name по id
export async function fetchColorNameById(id: number): Promise<string | null> {
  const color = await fetchColorById(id);
  return color ? color.name : null;
}