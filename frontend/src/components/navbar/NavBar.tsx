import styles from './NavBar.module.css';
import AddressBar from "./AddressBar";
import UserAccountBar from "./UserAccountBar";
import { useAuth } from "../../hooks/useAuth";

type Props = {
  onOpenAddress: () => void;
};

export default function NavBar({ onOpenAddress }: Props) {
  const isAuth = useAuth();

  if (isAuth === null) {
    return (
      <div className={styles.navBar}>
        <AddressBar onOpen={onOpenAddress} />
      </div>
    );
  }

  return (
    <div className={styles.navBar}>
      <AddressBar onOpen={onOpenAddress} />

      {!isAuth && <UserAccountBar />}
    </div>
  );
}