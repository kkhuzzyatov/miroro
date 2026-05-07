import { useEffect, useState } from "react";
import { fetchProductById } from "../api";
import type { Product } from "../types";

export function useProduct(id: number | null) {
  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) return;

    (async () => {
      setLoading(true);
      const data = await fetchProductById(id);
      setProduct(data);
      setLoading(false);
    })();
  }, [id]);

  return { product, loading };
}