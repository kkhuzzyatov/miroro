import styles from "./LoginPage.module.css";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function LoginPage() {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [show, setShow] = useState(false);

  const setCookie = (name: string, value: string, days = 7) => {
    const expires = new Date(Date.now() + days * 864e5).toUTCString();
    document.cookie =
      name + "=" + encodeURIComponent(value) + "; expires=" + expires + "; path=/";
  };

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const res = await fetch("/api/sessions/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
      });

      if (!res.ok) {
        const text = await res.text();
        alert(text || "Ошибка входа");
        return;
      }

      const data = await res.json();

      const token = data?.token;

      if (!token) {
        alert("Токен не найден в ответе сервера");
        return;
      }

      setCookie("session_token", token);

      navigate("/");
      window.location.reload();
    } catch (err) {
      console.error(err);
      alert("Ошибка сети");
    }
  };

  return (
    <div className={styles.page}>
      <form className={styles.loginForm} onSubmit={handleLogin}>
        <h2 className={styles.title}>Вход</h2>

        <label className={styles.label}>Username</label>
        <input
          className={styles.input}
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />

        <label className={styles.label}>Пароль</label>

        <div className={styles.passwordContainer}>
          <input
            className={styles.passwordInput}
            type={show ? "text" : "password"}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          <button
            className={styles.toggleButton}
            type="button"
            onClick={() => setShow((s) => !s)}
          >
            {show ? "🙈" : "👁"}
          </button>
        </div>

        <button className={styles.submitButton} type="submit">
          Войти
        </button>

        <button
          type="button"
          className={styles.submitButton}
          style={{ marginTop: "12px", background: "#e0e0e0", color: "#333" }}
          onClick={() => navigate("/register")}
        >
          Зарегистрироваться
        </button>
      </form>
    </div>
  );
}