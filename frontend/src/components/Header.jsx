import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';

export default function Header() {
    const [cartCount, setCartCount] = useState(0);
    const navigate = useNavigate();
    // Функция для подсчета общего количества товаров в localStorage
    const updateCartCount = () => {
        const cart = JSON.parse(localStorage.getItem('cart')) || [];
        const total = cart.reduce((sum, item) => sum + item.count, 0);
        setCartCount(total);
    };

    useEffect(() => {
        updateCartCount();

        // Чтобы счетчик обновлялся мгновенно при добавлении на ProductPage
        const handleStorageChange = () => updateCartCount();
        window.addEventListener('storage', handleStorageChange);

        // Кастомное событие для обновления в пределах одной вкладки
        window.addEventListener('cartUpdated', handleStorageChange);

        return () => {
            window.removeEventListener('storage', handleStorageChange);
            window.removeEventListener('cartUpdated', handleStorageChange);
        };
    }, []);

    const iconStyle = {
        cursor: 'pointer',
        display: 'flex',
        alignItems: 'center',
        background: 'none',
        border: 'none',
        padding: '5px',
        position: 'relative',
        color: '#333',
        textDecoration: 'none'
    };

    return (
        <header style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            padding: '20px 80px',
            background: 'white',
            borderBottom: '1px solid #f0f0f0',
            position: 'sticky',
            top: 0,
            zIndex: 1000
        }}>
            <Link to="/" style={{ textDecoration: 'none' }}>
                <h2 style={{
                    margin: 0,
                    fontSize: '28px',
                    fontFamily: 'var(--title-font)',
                    color: '#333'
                }}>
                    VioSoloCake
                </h2>
            </Link>

            <nav style={{ display: 'flex', gap: '25px', alignItems: 'center', fontFamily: 'var(--body-font)', fontSize: '15px' }}>
                <Link to="/" style={{ color: '#333', textDecoration: 'none' }}>Каталог</Link>
                <span style={{ cursor: 'pointer' }}>О нас</span>
                <span style={{ cursor: 'pointer' }}>Доставка</span>

                {/* ИКОНКА ПРОФИЛЯ — ТЕПЕРЬ С СЫЛКОЙ */}
                <Link to="/profile" style={iconStyle} title="Профиль">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                        <circle cx="12" cy="7" r="4"></circle>
                    </svg>
                </Link>

                {/* ИКОНКА КОРЗИНЫ С ЦИФРОЙ */}
                <Link to="/cart" style={iconStyle} title="Корзина">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"></path>
                        <line x1="3" y1="6" x2="21" y2="6"></line>
                        <path d="M16 10a4 4 0 0 1-8 0"></path>
                    </svg>
                    {cartCount > 0 && (
                        <span style={{
                            position: 'absolute',
                            top: '-2px',
                            right: '-2px',
                            background: '#333', // черный или розовый #ff69b4
                            color: 'white',
                            borderRadius: '50%',
                            width: '18px',
                            height: '18px',
                            fontSize: '10px',
                            display: 'flex',
                            justifyContent: 'center',
                            alignItems: 'center',
                            fontWeight: 'bold',
                            border: '2px solid white'
                        }}>
                            {cartCount}
                        </span>
                    )}
                </Link>

                <button
                    onClick={() => navigate('/cart')} // 1. Сначала пишем действие
                    style={{                        // 2. Потом открываем стили
                        background: '#333',
                        color: 'white',
                        border: 'none',
                        padding: '10px 25px',
                        borderRadius: '25px',
                        cursor: 'pointer',
                        fontFamily: 'var(--body-font)',
                        fontSize: '14px',
                        marginLeft: '10px',
                        fontWeight: '500'
                    }}
                >
                    ЗАКАЗАТЬ
                </button>
            </nav>
        </header>
    );
}