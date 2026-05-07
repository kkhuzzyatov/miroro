import styles from "./RegisterPage.module.css";
import { useState } from "react";

export default function RegisterPage() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [loading, setLoading] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (password !== confirm) {
      alert("Пароли не совпадают");
      return;
    }

    setLoading(true);

    try {
      const response = await fetch("/api/users", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password, name }),
      });

      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "Ошибка регистрации");
      }

      window.location.href = "/login";
    } catch (e: any) {
      alert("Пользователь с таким email уже существует");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.page}>
      <form className={styles.registrationForm} onSubmit={submit}>
        <h2 className={styles.title}>Регистрация</h2>

        <label className={styles.label}>Имя</label>
        <input
          className={styles.input}
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
        />

        <label className={styles.label}>Email</label>
        <input
          className={styles.input}
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
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