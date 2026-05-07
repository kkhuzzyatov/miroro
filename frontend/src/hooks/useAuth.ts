import { useEffect, useState } from "react";

export function useAuth() {
  const [isAuth, setIsAuth] = useState<boolean | null>(null);

  useEffect(() => {
    fetch("/api/sessions/me", {
      method: "GET",
      credentials: "include",
    })
      .then(res => res.json())
      .then(data => {
        setIsAuth(data.authenticated);
      })
      .catch(err => {
        setIsAuth(false);
      });
  }, []);

  return isAuth;
}