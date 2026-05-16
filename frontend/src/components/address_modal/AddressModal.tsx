import { useEffect, useState } from "react";
import { createPortal } from "react-dom";
import styles from "./AddressModal.module.css";

import { useCitySearch } from "./useCitySearch";
import type { City } from "./useCitySearch";

import { useAddressSearch } from "./useAddressSearch";

/* ================= TYPES ================= */

type Address = {
  id: number;
  address: string;
  city: {
    cityUuid: string;
    name: string;
  };
};

type Props = {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (address: Address) => void;
};

/* ================= COOKIE ================= */

function setCookie(name: string, value: string, days = 7) {
  const expires = new Date(Date.now() + days * 864e5).toUTCString();
  document.cookie = `${name}=${encodeURIComponent(value)}; expires=${expires}; path=/`;
}

function getCookie(name: string) {
  return document.cookie
    .split("; ")
    .find((row) => row.startsWith(name + "="))
    ?.split("=")[1];
}

/* ================= COMPONENT ================= */

export default function AddressModal({ isOpen, onClose }: Props) {
  const [cityQuery, setCityQuery] = useState("");
  const [addressQuery, setAddressQuery] = useState("");

  const [selectedCity, setSelectedCity] = useState<City | null>(null);
  const [selectedAddress, setSelectedAddress] = useState<Address | null>(null);

  const [cityOpen, setCityOpen] = useState(false);
  const [addressOpen, setAddressOpen] = useState(false);

  const { cities, loading: cityLoading } = useCitySearch(cityQuery);

  const { addresses, loading: addressLoading } = useAddressSearch(
    selectedCity?.city_uuid ?? null,
    addressQuery
  );

  /* ================= RESTORE ================= */

  useEffect(() => {
    if (!isOpen) return;

    const savedCityName = getCookie("city_name");
    const savedCityId = getCookie("city_id");

    const savedAddressName = getCookie("address_name");
    const savedAddressId = getCookie("address_id");

    if (savedCityName && savedCityId) {
      setSelectedCity({
        city_uuid: decodeURIComponent(savedCityId),
        full_name: decodeURIComponent(savedCityName),
      });

      setCityQuery(decodeURIComponent(savedCityName));
    }

    if (savedAddressName && savedAddressId) {
      setSelectedAddress({
        id: Number(savedAddressId),
        address: decodeURIComponent(savedAddressName),
        city: {
          cityUuid: "",
          name: "",
        },
      });

      setAddressQuery(decodeURIComponent(savedAddressName));
    }
  }, [isOpen]);

  if (!isOpen) return null;

  /* ================= CITY ================= */

  const selectCity = (city: City) => {
    setSelectedCity(city);
    setCityQuery(city.full_name);

    setCityOpen(false);

    setSelectedAddress(null);
    setAddressQuery("");
  };

  const resetCity = () => {
    setSelectedCity(null);
    setCityQuery("");
    setSelectedAddress(null);
    setAddressQuery("");
  };

  /* ================= ADDRESS ================= */

  const selectAddress = (addr: Address) => {
    setSelectedAddress(addr);
    setAddressQuery(addr.address);
    setAddressOpen(false);
  };

  const resetAddress = () => {
    setSelectedAddress(null);
    setAddressQuery("");
  };

  /* ================= CONFIRM ================= */

  const handleConfirm = () => {
    if (!selectedCity || !selectedAddress) return;

    setCookie("city_id", selectedCity.city_uuid);
    setCookie("city_name", selectedCity.full_name);

    setCookie("address_id", String(selectedAddress.id));
    setCookie("address_name", selectedAddress.address);

    onClose();
    window.location.reload();
  };

  /* ================= UI ================= */

  return createPortal(
    <div className={styles.backdrop}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        {/* CITY */}
        <div className={styles.block}>
          <div className={styles.inputGroup}>
            <input
              value={cityQuery}
              onChange={(e) => setCityQuery(e.target.value)}
              onFocus={() => !selectedCity && setCityOpen(true)}
              readOnly={!!selectedCity}
              placeholder="Город"
            />

            {selectedCity && (
              <button onClick={resetCity}>✕</button>
            )}
          </div>

          {cityOpen && cityLoading && <div>Загрузка...</div>}

          {cityOpen && cities.length > 0 && (
            <div className={styles.dropdown}>
              {cities.map((c) => (
                <div
                  className={styles.item}
                  key={c.city_uuid}
                  onClick={() => selectCity(c)}
                >
                  {c.full_name}
                </div>
              ))}
            </div>
          )}
        </div>

        {/* ADDRESS */}
        {selectedCity && (
          <div className={styles.block}>
            <div className={styles.inputGroup}>
              <input
                value={addressQuery}
                onChange={(e) => setAddressQuery(e.target.value)}
                onFocus={() => !selectedAddress && setAddressOpen(true)}
                readOnly={!!selectedAddress}
                placeholder="ПВЗ"
              />

              {selectedAddress && (
                <button onClick={resetAddress}>✕</button>
              )}
            </div>

            {addressOpen && addressLoading && <div>Загрузка...</div>}

            {addressOpen && addresses.length > 0 && (
              <div className={styles.dropdown}>
                {addresses.map((a: Address) => (
                  <div
                    className={styles.item}
                    key={a.id}
                    onClick={() => selectAddress(a)}
                  >
                    {a.address}
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* CONFIRM */}
        {selectedAddress && (
          <button className={styles.confirmButton} onClick={handleConfirm}>
            Подтвердить
          </button>
        )}

        <button className={styles.closeButton} onClick={onClose}>
          Закрыть
        </button>
      </div>
    </div>,
    document.body
  );
}