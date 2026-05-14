import type { Product } from "../api_client/products";
import type { Size } from "../api_client/sizes";
import type { Color } from "../api_client/colors";

export function getSizes(product: Product, sizes: Size[]): Size[] {
  const ids = new Set(product.variants.map(v => v.size_id));
  return sizes.filter(s => ids.has(s.id));
}

export function getColors(product: Product, colors: Color[]): Color[] {
  const ids = new Set(product.variants.map(v => v.color_id));
  return colors.filter(c => ids.has(c.id));
}