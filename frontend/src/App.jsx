import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Header from './components/Header';
import Hero from './components/Hero';
import FeaturedSection from './components/FeaturedSection';
import CategoryPage from './pages/CategoryPage';
import ProductPage from './pages/ProductPage';
import CartPage from './pages/CartPage';
import CheckoutPage from './pages/CheckoutPage';
import OrderSuccessPage from './pages/OrderSuccessPage';
import LoginPage from './pages/LoginPage';
import ProfilePage from './pages/ProfilePage';
import RegisterPage from './pages/RegisterPage';

function App() {
    return (
        <div className="app-container">
            {/* Шапка всегда сверху */}
            <Header />

            <Routes>
                {/* Страница корзины */}
                <Route path="/cart" element={<CartPage />} />
                <Route path="/checkout" element={<CheckoutPage />} />

                {/* Главная страница */}
                <Route path="/" element={
                    <>
                        <Hero />
                        {/* id="catalog" — это цель для нашей кнопки.
                            scrollMarginTop — делает отступ сверху при скролле (под шапку).
                        */}
                        <div id="catalog" style={{ scrollMarginTop: '100px' }}>
                            <FeaturedSection />
                        </div>
                    </>
                } />

                {/* Страница категории по слагу (например, /category/zefir) */}
                <Route path="/category/:slug" element={<CategoryPage />} />
                <Route path="/order-success" element={<OrderSuccessPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/profile" element={<ProfilePage />} />
                <Route path="/register" element={<RegisterPage />} />
                {/* Страница конкретного товара */}
                <Route path="/product/:productId" element={<ProductPage />} />
            </Routes>
        </div>
    );
}

export default App;