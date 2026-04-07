import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api';

export default function ProductPage() {
    const { productId } = useParams();
    const navigate = useNavigate();

    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);

    const [cartCount, setCartCount] = useState(0);

    useEffect(() => {
        api.get(`/products/${productId}`)
            .then(res => {
                setProduct(res.data);
                setLoading(false);

                const cart = JSON.parse(localStorage.getItem('cart')) || [];
                const item = cart.find(i => i.id === parseInt(productId));
                if (item) {
                    setCartCount(item.count);
                }
            })
            .catch(err => {
                console.error("Ошибка при загрузке товара:", err);
                setLoading(false);
            });
    }, [productId]);

    const updateCart = (newCount) => {
        let cart = JSON.parse(localStorage.getItem('cart')) || [];
        const itemIndex = cart.findIndex(item => item.id === product.id);

        if (newCount > 0) {
            if (itemIndex > -1) {
                cart[itemIndex].count = newCount;
            } else {
                cart.push({
                    id: product.id,
                    name: product.name,
                    price: product.price,
                    image: product.imageUrl,
                    count: newCount
                });
            }
        } else {
            cart = cart.filter(item => item.id !== product.id);
        }

        localStorage.setItem('cart', JSON.stringify(cart));
        setCartCount(newCount); // Обновляем UI
        window.dispatchEvent(new Event('cartUpdated'));
    };

    if (loading) return <div style={{ padding: '100px', textAlign: 'center' }}>Загрузка десерта...</div>;
    if (!product) return <div style={{ padding: '100px', textAlign: 'center' }}>Товар не найден :(</div>;

    return (
        <div style={{
            padding: '80px 10%',
            display: 'flex',
            flexDirection: 'row',
            justifyContent: 'center',
            gap: '80px',
            alignItems: 'flex-start',
            backgroundColor: '#fafafa',
            minHeight: '100vh',
            fontFamily: 'var(--body-font)'
        }}>

            <div style={{ flex: '0 0 auto', width: '500px', textAlign: 'left' }}>
                <button
                    onClick={() => navigate(-1)}
                    style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'gray', marginBottom: '30px', padding: 0 }}
                >
                    ← Назад
                </button>
                <img
                    src={product.imageUrl}
                    alt={product.name}
                    style={{ width: '100%', height: 'auto', objectFit: 'cover', borderRadius: '20px', boxShadow: '0 15px 40px rgba(0,0,0,0.1)' }}
                />
            </div>

            <div style={{ flex: '1', maxWidth: '600px', textAlign: 'left' }}>
                <h1 style={{ fontFamily: 'var(--title-font)', fontSize: '56px', marginTop: 0, marginBottom: '15px', fontWeight: 'normal' }}>
                    {product.name}
                </h1>

                <p style={{ fontSize: '28px', fontWeight: 'bold', marginBottom: '40px' }}>
                    {product.price} BYN
                </p>

                <div style={{ marginBottom: '30px' }}>
                    <h3 style={{ fontSize: '18px', fontWeight: 'bold' }}>Описание</h3>
                    <p style={{ lineHeight: '1.8', color: '#666' }}>
                        {product.description}
                    </p>
                </div>

                <div style={{ marginBottom: '50px', padding: '25px', background: '#fff', borderRadius: '15px', border: '1px solid #eee' }}>
                    <h3 style={{ marginTop: 0, fontSize: '18px' }}>Состав</h3>
                    <p style={{ fontStyle: 'italic', color: '#888', margin: 0 }}>
                        {Array.isArray(product.ingredients) ? product.ingredients.join(', ') : product.ingredients}
                    </p>
                </div>

                <div style={{ marginTop: '20px' }}>
                    {cartCount === 0 ? (
                        <button
                            onClick={() => updateCart(1)}
                            style={{
                                background: '#333', color: 'white', border: 'none',
                                padding: '18px 50px', borderRadius: '30px',
                                cursor: 'pointer', width: '100%', fontWeight: 'bold',
                                transition: '0.3s'
                            }}
                        >
                            ДОБАВИТЬ В КОРЗИНУ
                        </button>
                    ) : (
                        <div style={{
                            display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                            background: '#fff', padding: '10px 20px',
                            borderRadius: '30px', border: '1px solid #ddd',
                            width: '100%', boxSizing: 'border-box'
                        }}>
                            <button
                                onClick={() => updateCart(cartCount - 1)}
                                style={{ background: 'none', border: 'none', fontSize: '24px', cursor: 'pointer', padding: '10px' }}
                            >
                                −
                            </button>

                            <span style={{ fontSize: '20px', fontWeight: 'bold' }}>
                                {cartCount} шт
                            </span>

                            <button
                                onClick={() => updateCart(cartCount + 1)}
                                style={{ background: 'none', border: 'none', fontSize: '24px', cursor: 'pointer', padding: '10px' }}
                            >
                                +
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}