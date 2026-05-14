export interface Size {
  id: number;
  name: string;
}

/* -------------------- CACHE -------------------- */

const SIZES_TTL_MS = 5 * 60 * 1000;

let sizesCache: Size[] | null = null;
let sizesCacheTime = 0;
let sizesPromise: Promise<Size[]> | null = null;

const sizeByIdCache = new Map<number, { data: Size; time: number }>();

function isFresh(time: number) {
  return Date.now() - time < SIZES_TTL_MS;
}

/* -------------------- LOGGING -------------------- */

function log(action: string, detail: unknown) {
  console.log(`[sizes] ${action}`, detail);
}

/* -------------------- API -------------------- */

export async function fetchSizes(): Promise<Size[]> {
  if (sizesCache && isFresh(sizesCacheTime)) {
    return sizesCache;
  }

  if (sizesPromise) {
    return sizesPromise;
  }

  sizesPromise = (async () => {
    try {
      const res = await fetch("/api/sizes");

      if (!res.ok) {
        log("FETCH FAIL fetchSizes", res.status);
        throw new Error("Failed to fetch sizes");
      }

      const data: Size[] = await res.json();

      sizesCache = data;
      sizesCacheTime = Date.now();

      log("FETCH OK fetchSizes", { size: data.length });

      return data;
    } catch (e) {
      log("FETCH ERROR fetchSizes", e);
      throw e;
    } finally {
      sizesPromise = null;
    }
  })();

  return sizesPromise;
}

/* -------------------- BY ID -------------------- */

export async function fetchSizeById(id: number): Promise<Size | null> {
  const cached = sizeByIdCache.get(id);
  if (cached && isFresh(cached.time)) {
    return cached.data;
  }

  const sizes = await fetchSizes();

  const size = sizes.find(s => s.id === id) ?? null;

  if (size) {
    sizeByIdCache.set(id, { data: size, time: Date.now() });
  }

  return size;
}

/* -------------------- NAME ONLY -------------------- */

export async function fetchSizeNameById(id: number): Promise<string | null> {
  const size = await fetchSizeById(id);
  return size ? size.name : null;
}