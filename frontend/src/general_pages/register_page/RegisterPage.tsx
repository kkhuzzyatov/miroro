import styles from "./RegisterPage.module.css";
import { useState } from "react";

export default function RegisterPage() {
  const [name] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const setCookie = (name: string, value: string, days = 7) => {
    const expires = new Date(Date.now() + days * 864e5).toUTCString();

    document.cookie =
      name +
      "=" +
      encodeURIComponent(value) +
      "; expires=" +
      expires +
      "; path=/";
  };

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();

    setError("");

    if (password !== confirm) {
      setError("Пароли не совпадают");
      return;
    }

    setLoading(true);

    try {
      const response = await fetch("/api/users", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password, name }),
      });

      if (!response.ok) {
        const text = await response.text();

        setError(text || "Что-то пошло не так. Попробуйте ещё раз.");

        return;
      }

      const loginResponse = await fetch("/api/sessions/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
      });

      if (!loginResponse.ok) {
        const text = await loginResponse.text();

        setError(text || "Что-то пошло не так. Попробуйте ещё раз.");

        return;
      }

      const data = await loginResponse.json();

      const token = data?.token;

      setCookie("session_token", token);

      window.location.href = "/";
    } catch (e) {
      console.error(e);

      setError("Что-то пошло не так. Попробуйте ещё раз.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.page}>
      <form className={styles.registrationForm} onSubmit={submit}>
        <h2 className={styles.title}>Регистрация</h2>

        {error && (
          <div
            style={{
              width: "100%",
              background: "#ffebeb",
              border: "1px solid #ff4d4f",
              color: "#b00020",
              padding: "12px",
              borderRadius: "8px",
              marginBottom: "16px",
              fontSize: "14px",
              boxSizing: "border-box",
            }}
          >
            {error}
          </div>
        )}

        <label className={styles.label}>Username</label>

        <input
          className={styles.input}
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />

        <label className={styles.label}>Пароль</label>

        <div className={styles.passwordContainer}>
          <input
            className={styles.passwordInput}
            type={showPassword ? "text" : "password"}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          <button
            type="button"
            className={styles.toggleButton}
            onClick={() => setShowPassword((v) => !v)}
          >
            {showPassword ? "🙈" : "👁"}
          </button>
        </div>

        <label className={styles.label}>Подтверждение</label>

        <div className={styles.passwordContainer}>
          <input
            className={styles.passwordInput}
            type={showConfirm ? "text" : "password"}
            value={confirm}
            onChange={(e) => setConfirm(e.target.value)}
            required
          />

          <button
            type="button"
            className={styles.toggleButton}
            onClick={() => setShowConfirm((v) => !v)}
          >
            {showConfirm ? "🙈" : "👁"}
          </button>
        </div>

        <button className={styles.submitButton} disabled={loading}>
          {loading ? "..." : "Зарегистрироваться"}
        </button>
      </form>
    </div>
  );
}