import { useEffect, useRef, useState } from "react";

export type City = {
  city_uuid: string;
  full_name: string;
};

export function useCitySearch(query: string) {
  const [cities, setCities] = useState<City[]>([]);
  const [loading, setLoading] = useState(false);

  const cache = useRef<Map<string, City[]>>(new Map());

  useEffect(() => {
    const t = setTimeout(() => search(), 200);
    return () => clearTimeout(t);
  }, [query]);

  const search = async () => {
    const key = query.trim().toLowerCase();

    setLoading(true);

    if (cache.current.has(key)) {
      setCities(cache.current.get(key)!);
      setLoading(false);
      return;
    }

    try {
      const url = key
        ? `/api/locations/cities?name=${encodeURIComponent(key)}`
        : `/api/locations/cities`;

      const res = await fetch(url);
      const data = await res.json();

      cache.current.set(key, data);
      setCities(data);
    } finally {
      setLoading(false);
    }
  };

  return { cities, loading };
}