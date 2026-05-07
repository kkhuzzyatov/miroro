import { useEffect, useState } from "react";
import styles from "./NavBar.module.css";

type Props = {
  onOpen: () => void;
};

function getCookie(name: string): string | null {
  const match = document.cookie.match(
    new RegExp("(^| )" + name + "=([^;]+)")
  );
  return match ? decodeURIComponent(match[2]) : null;
}

function formatAddress(value: string, maxLength: number = 12): string {
  if (value.length <= maxLength) return value;
  return value.slice(0, maxLength) + "...";
}

export default function AddressBar({ onOpen }: Props) {
  const [address, setAddress] = useState<string | null>(null);

  useEffect(() => {
    const storedAddress = getCookie("address_name");
    setAddress(storedAddress);
  }, []);

  return (
    <div className={styles.addressBarWrapper}>
      <div className={styles.buttonBase} onClick={onOpen}>
        {address ? formatAddress(address, 12) : "Выбрать город"}
      </div>
    </div>
  );
}