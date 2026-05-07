import { Routes, Route } from "react-router-dom";
import { useState, useMemo } from "react";

import HomePageMobile from "./mobile_pages/home_page/HomePage";
import HomePageDesktop from "./desktop_pages/home_page/HomePage";

import ProductPageMobile from "./mobile_pages/product_page/ProductPage";
import ProductPageDesktop from "./desktop_pages/product_page/ProductPage";

import FavoritesPageMobile from "./mobile_pages/favorites_page/FavoritesPage";
import FavoritesPageDesktop from "./desktop_pages/favorites_page/FavoritesPage";

import RegisterPage from "./general_pages/register_page/RegisterPage";
import LoginPage from "./general_pages/login_page/LoginPage";
import BasketsPage from "./general_pages/cart_page/BasketPage";
import AccountPage from "./general_pages/account_page/AccountPage";

import Footer from "./components/footer/Footer";
import NavBar from "./components/navbar/NavBar";
import AddressModal from "./components/address_modal/AddressModal";

export default function App() {
  const [addressOpen, setAddressOpen] = useState(false);

  const isMobile = useMemo(() => {
    const ua = navigator.userAgent || navigator.vendor;
    return /android|iphone|ipad|ipod|opera mini|iemobile|mobile/i.test(ua);
  }, []);

  const HomePage = isMobile ? HomePageMobile : HomePageDesktop;
  const ProductPage = isMobile ? ProductPageMobile : ProductPageDesktop;
  const FavoritesPage = isMobile ? FavoritesPageMobile : FavoritesPageDesktop;

  return (
    <div className="appLayout">
      <NavBar onOpenAddress={() => setAddressOpen(true)} />
        <div style={{ height: "40px" }} /> {/* отступ для фиксированного NavBar */}

      <div className="pageContent">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/product/:id" element={<ProductPage />} />
          <Route path="/favorites" element={<FavoritesPage />} />

          <Route path="/register" element={<RegisterPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/cart" element={<BasketsPage />} />
          <Route path="/account" element={<AccountPage />} />
        </Routes>
      </div>

      <Footer />

      <AddressModal
        isOpen={addressOpen}
        onClose={() => setAddressOpen(false)}
      />
    </div>
  );
}