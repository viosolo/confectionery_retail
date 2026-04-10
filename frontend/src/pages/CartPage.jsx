import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';

export default function CartPage() {
    const [cartItems, setCartItems] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        const savedCart = JSON.parse(localStorage.getItem('cart')) || [];
        setCartItems(savedCart);
    }, []);

    const updateQuantity = (id, delta) => {
        const updatedCart = cartItems.map(item => {
            if (item.id === id) {
                const newCount = Math.max(0, item.count + delta);
                return { ...item, count: newCount };
            }
            return item;
        }).filter(item => item.count > 0);

        setCartItems(updatedCart);
        localStorage.setItem('cart', JSON.stringify(updatedCart));
        window.dispatchEvent(new Event('cartUpdated'));
    };

    const totalPrice = cartItems.reduce((sum, item) => sum + (item.price * item.count), 0);

    if (cartItems.length === 0) {
        return (
            <div style={{ textAlign: 'center', padding: '100px', fontFamily: 'var(--body-font)' }}>
                <h2>Ваша корзина пуста 🌸</h2>
                <Link to="/" style={{ color: '#333' }}>Вернуться к выбору</Link>
            </div>
        );
    }

    return (
        <div style={{ padding: '40px 10%', fontFamily: 'var(--body-font)' }}>
            <h1 style={{ fontFamily: 'var(--title-font)', marginBottom: '40px' }}>Корзина</h1>

            <div style={{ display: 'grid', gap: '20px' }}>
                {cartItems.map(item => (
                    <div key={item.id} style={{
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                        padding: '20px',
                        borderBottom: '1px solid #eee'
                    }}>
                        <img src={item.image} alt={item.name} style={{ width: '80px', borderRadius: '10px' }} />
                        <div style={{ flex: 1, marginLeft: '20px' }}>
                            <h3 style={{ margin: 0 }}>{item.name}</h3>
                            <p style={{ color: '#888' }}>{item.price} BYN</p>
                        </div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                            <button onClick={() => updateQuantity(item.id, -1)} style={btnStyle}>-</button>
                            <span>{item.count}</span>
                            <button onClick={() => updateQuantity(item.id, 1)} style={btnStyle}>+</button>
                        </div>
                        <div style={{ fontWeight: 'bold', marginLeft: '40px', width: '100px' }}>
                            {(item.price * item.count).toFixed(2)} BYN
                        </div>
                    </div>
                ))}
            </div>

            <div style={{ marginTop: '40px', textAlign: 'right' }}>
                <h2 style={{ fontFamily: 'var(--title-font)' }}>
                    Итого: {totalPrice.toFixed(2)} BYN
                </h2>
                <button
                    onClick={() => navigate('/checkout')}
                    style={{
                        background: '#222',
                        color: 'white',
                        padding: '15px 40px',
                        border: 'none',
                        cursor: 'pointer',
                        marginTop: '20px',
                        textTransform: 'uppercase',
                        letterSpacing: '2px'
                    }}>
                    Оформить заказ
                </button>
            </div>
        </div>
    );
}

const btnStyle = {
    width: '30px', height: '30px', cursor: 'pointer', border: '1px solid #ddd', background: 'white'
};