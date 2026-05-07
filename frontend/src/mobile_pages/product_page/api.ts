import type { Product } from "./types";

export async function fetchProductById(id: number): Promise<Product | null> {
  const res = await fetch(`/api/products?id=${id}`);
  if (!res.ok) return null;

  const data = await res.json();
  return Array.isArray(data) ? data[0] : data;
}