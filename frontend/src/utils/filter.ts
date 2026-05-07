import type { Product } from "../api/products";

export function filterAvailable(products: Product[]): Product[] {
  return products.filter(p =>
    p.variants.some(v => v.quantity > 0)
  );
}