import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import TabsMenu from './components/TabsMenu';
import styles from './AdminPage.module.css';

type User = {
  id: number;
  role: string;
};

export default function AdminPage() {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();

  useEffect(() => {
    loadUser();
  }, []);

  async function loadUser() {
    try {
      const res = await fetch('/api/users/me', {
        credentials: 'include',
      });

      if (!res.ok) {
        setUser(null);
        setLoading(false);
        return;
      }

      const data: User = await res.json();
      setUser(data);
    } catch (e) {
      setUser(null);
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return <div className={styles.loading}>Загрузка...</div>;
  }

  const isAdmin = user?.role === 'admin';

  if (!isAdmin) {
    return (
      <div className={styles.accessDenied}>
        <div className={styles.card}>
          <h2>Доступ ограничен</h2>
          <p>Доступ только для администраторов</p>

          <button
            className={styles.loginButton}
            onClick={() => navigate('/login')}
          >
            Войти в качестве администратора
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.adminPage}>
      <TabsMenu />
    </div>
  );
}