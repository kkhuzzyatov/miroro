export interface ProductVariant {
  id: number;
  size_id: number;
  color_id: number;
  quantity: number;
}

export interface ProductImage {
  path: string;
  is_main: boolean;
}

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  variants: ProductVariant[];
  images: ProductImage[];
}