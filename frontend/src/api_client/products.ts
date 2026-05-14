export interface ApiVariant {
  variant_id: number;
  size_id: number;
  color_id: number;
  quantity: number;
}

export interface ApiImage {
  path: string;
  color_id: number;
  is_main: boolean;
}

export interface ApiProduct {
  id: number;
  name: string;
  description: string;
  price: number;
  segment_id: number;
  variants: ApiVariant[];
  images: ApiImage[];
}

/* -------------------- UI MODELS -------------------- */

export interface ProductVariant {
  id: number;
  size_id: number;
  color_id: number;
  quantity: number;
}

export interface Image {
  path: string;
  color_id: number;
  isMain: boolean;
}

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  images: Image[];
  variants: ProductVariant[];
}

/* -------------------- CACHE -------------------- */

const PRODUCTS_TTL_MS = 5 * 60 * 1000;

let productsCache: Product[] | null = null;
let productsCacheTime = 0;
let productsPromise: Promise<Product[]> | null = null;

function isFresh(time: number) {
  return Date.now() - time < PRODUCTS_TTL_MS;
}

/* -------------------- LOGGING -------------------- */

function log(action: string, detail: unknown) {
  console.log(`[products] ${action}`, detail);
}

/* -------------------- MAPPER -------------------- */

function mapProduct(api: ApiProduct): Product {
  return {
    id: api.id,
    name: api.name,
    description: api.description,
    price: api.price,

    images: api.images.map(img => ({
      path: img.path,
      color_id: img.color_id,
      isMain: img.is_main,
    })),

    variants: api.variants.map(v => ({
      id: v.variant_id,
      size_id: v.size_id,
      color_id: v.color_id,
      quantity: v.quantity,
    })),
  };
}

/* -------------------- API -------------------- */

export async function fetchProducts(): Promise<Product[]> {
  if (productsCache && isFresh(productsCacheTime)) {
    return productsCache;
  }

  if (productsPromise) {
    return productsPromise;
  }

  productsPromise = (async () => {
    try {
      const res = await fetch("/api/products");

      if (!res.ok) {
        log("FETCH FAIL fetchProducts", res.status);
        throw new Error("Failed to fetch products");
      }

      const data: ApiProduct[] = await res.json();

      const mapped = data.map(mapProduct);

      productsCache = mapped;
      productsCacheTime = Date.now();

      log("FETCH OK fetchProducts", { size: mapped.length });

      return mapped;
    } catch (e) {
      log("FETCH ERROR fetchProducts", e);
      throw e;
    } finally {
      productsPromise = null;
    }
  })();

  return productsPromise;
}

/* -------------------- BY ID (NO HTTP) -------------------- */

export async function fetchProductById(id: number): Promise<Product | null> {
  const products = await fetchProducts();

  const product = products.find(p => p.id === id) ?? null;

  return product;
}