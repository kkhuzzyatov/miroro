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

/* -------------------- NORMALIZATION -------------------- */

function normalizeHex(hex: string): string {
  if (!hex) return "#000000";
  return hex.startsWith("#") ? hex : `#${hex}`;
}

/* -------------------- CACHE -------------------- */

const COLORS_TTL_MS = 5 * 60 * 1000;

let colorsCache: Color[] | null = null;
let colorsCacheTime = 0;
let colorsPromise: Promise<Color[]> | null = null;

const colorByIdCache = new Map<number, { data: Color; time: number }>();

function isFresh(time: number) {
  return Date.now() - time < COLORS_TTL_MS;
}

/* -------------------- LOGGING -------------------- */

function log(action: string, detail: unknown) {
  console.log(`[colors] ${action}`, detail);
}

/* -------------------- API -------------------- */

export async function fetchColors(): Promise<Color[]> {
  if (colorsCache && isFresh(colorsCacheTime)) {
    return colorsCache;
  }

  if (colorsPromise) {
    return colorsPromise;
  }

  colorsPromise = (async () => {
    try {
      const res = await fetch("/api/colors");

      if (!res.ok) {
        log("FETCH FAIL fetchColors", res.status);
        throw new Error("Failed to fetch colors");
      }

      const data: ApiColor[] = await res.json();

      const normalized = data.map(c => ({
        id: c.id,
        name: c.name,
        hex: normalizeHex(c.hex),
      }));

      colorsCache = normalized;
      colorsCacheTime = Date.now();

      log("FETCH OK fetchColors", { size: normalized.length });

      return normalized;
    } catch (e) {
      log("FETCH ERROR fetchColors", e);
      throw e;
    } finally {
      colorsPromise = null;
    }
  })();

  return colorsPromise;
}

/* -------------------- BY ID -------------------- */

export async function fetchColorById(id: number): Promise<Color | null> {
  const cached = colorByIdCache.get(id);
  if (cached && isFresh(cached.time)) {
    return cached.data;
  }

  const colors = await fetchColors();

  const color = colors.find(c => c.id === id) ?? null;

  if (color) {
    colorByIdCache.set(id, { data: color, time: Date.now() });
  }

  return color;
}

/* -------------------- NAME ONLY -------------------- */

export async function fetchColorNameById(id: number): Promise<string | null> {
  const color = await fetchColorById(id);
  return color ? color.name : null;
}