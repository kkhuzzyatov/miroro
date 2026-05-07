import { useEffect, useRef, useState } from "react";

export type Address = {
  addressId: number;
  address: string;
};

export function useAddressSearch(cityUuid: string | null, query: string) {
  const [addresses, setAddresses] = useState<Address[]>([]);
  const [loading, setLoading] = useState(false);

  const cache = useRef<Map<string, Address[]>>(new Map());

  useEffect(() => {
    if (!cityUuid) return;

    const t = setTimeout(() => search(), 200);
    return () => clearTimeout(t);
  }, [cityUuid, query]);

  const search = async () => {
    if (!cityUuid) return;

    const key = `${cityUuid}:${query.trim().toLowerCase()}`;

    setLoading(true);

    if (cache.current.has(key)) {
      setAddresses(cache.current.get(key)!);
      setLoading(false);
      return;
    }

    try {
      const url = query.trim()
        ? `/api/locations/delivery_points?city_uuid=${cityUuid}&name=${encodeURIComponent(query)}`
        : `/api/locations/delivery_points?city_uuid=${cityUuid}`;

      const res = await fetch(url);
      const data = await res.json();

      cache.current.set(key, data);
      setAddresses(data);
    } finally {
      setLoading(false);
    }
  };

  return { addresses, loading };
}