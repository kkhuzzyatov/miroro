import { useNavigate } from "react-router-dom";
import FooterButton from "./FooterButton";
import { useAuth } from "../../hooks/useAuth";

import homeIcon from "../../assets/footer/Home.png";
import favoritesIcon from "../../assets/footer/Favorites.png";
import cartIcon from "../../assets/footer/Cart.png";
import accountIcon from "../../assets/footer/Account.png";

export default function FooterContent() {
  const navigate = useNavigate();
  const isAuth = useAuth();

  if (isAuth === null) return null;

  return (
    <>
      <FooterButton
        text="Главная"
        icon={homeIcon}
        onClick={() => navigate("/")}
      />

      <FooterButton
        text="Избранное"
        icon={favoritesIcon}
        onClick={() => navigate("/favorites")}
      />

      <FooterButton
        text="Корзина"
        icon={cartIcon}
        onClick={() => navigate("/cart")}
      />

      <FooterButton
        text={isAuth ? "Аккаунт" : "Войти"}
        icon={accountIcon}
        onClick={() => navigate(isAuth ? "/account" : "/login")}
      />
    </>
  );
}