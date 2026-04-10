import React from 'react';
import { Routes, Route, useLocation } from 'react-router-dom';
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
import AdminDashboard from './pages/AdminDashboard';
import AboutPage from './pages/AboutPage';
import DeliveryPage from './pages/DeliveryPage';

function App() {

    const location = useLocation();

    const isAdminPage = location.pathname === '/admin';

    return (
        <div className="app-container">
            {!isAdminPage && <Header />}

            <Routes>
                <Route path="/cart" element={<CartPage />} />
                <Route path="/checkout" element={<CheckoutPage />} />

                <Route path="/" element={
                    <>
                        <Hero />
                        <div id="catalog" style={{ scrollMarginTop: '100px' }}>
                            <FeaturedSection />
                        </div>
                    </>
                } />

                <Route path="/about" element={<AboutPage />} />
                <Route path="/delivery" element={<DeliveryPage />} />
                <Route path="/admin-dashboard" element={<AdminDashboard />} />
                <Route path="/category/:slug" element={<CategoryPage />} />
                <Route path="/order-success" element={<OrderSuccessPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/profile" element={<ProfilePage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/product/:productId" element={<ProductPage />} />
            </Routes>
        </div>
    );
}

export default App;