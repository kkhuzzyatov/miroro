import styles from './NavBar.module.css';
import { useNavigate } from 'react-router-dom';

export default function UserAccountBar() {
  const navigate = useNavigate();

  return (
    <div className={styles.userBarWrapper}>
      <div
        className={styles.buttonBase}
        onClick={() => navigate('/login')}
      >
        Войти
      </div>
    </div>
  );
}