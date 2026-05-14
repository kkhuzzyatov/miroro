import type { Product } from "../api_client/products";

export function filterAvailable(products: Product[]): Product[] {
  return products.filter(p =>
    p.variants.some(v => v.quantity > 0)
  );
}