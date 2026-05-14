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

/* UI модели */

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

/* mapper API → UI */

function mapProduct(api: ApiProduct): Product {
  return {
    id: api.id,
    name: api.name,
    description: api.description,
    price: api.price,

    images: api.images.map(img => ({
      path: img.path,
      color_id: img.color_id,
      isMain: img.is_main
    })),

    variants: api.variants.map(v => ({
      id: v.variant_id,
      size_id: v.size_id,
      color_id: v.color_id,
      quantity: v.quantity
    }))
  };
}

export async function fetchProducts(): Promise<Product[]> {
  const res = await fetch("/api/products");

  if (!res.ok) {
    throw new Error("Failed to fetch products");
  }

  const data: ApiProduct[] = await res.json();

  return data.map(mapProduct);
}

export async function fetchProductById(id: number): Promise<Product | null> {
  const res = await fetch(`/api/products?id=${id}`);

  if (res.status === 404) return null;

  if (!res.ok) {
    throw new Error("Failed to fetch product");
  }

  const data: ApiProduct = await res.json();

  return mapProduct(data);
}