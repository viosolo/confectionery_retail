import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

export default function Hero() {
    const navigate = useNavigate();
    const location = useLocation();

    const scrollToCatalog = () => {

        if (location.pathname === '/') {
            const catalogSection = document.getElementById('catalog');
            if (catalogSection) {
                catalogSection.scrollIntoView({ behavior: 'smooth' });
            }
        } else {

            navigate('/');
            setTimeout(() => {
                const catalogSection = document.getElementById('catalog');
                catalogSection?.scrollIntoView({ behavior: 'smooth' });
            }, 100);
        }
    };

    return (
        <section style={{
            background: 'var(--main-pink)',
            padding: '140px 20px',
            textAlign: 'center',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            borderRadius: '0 0 80px 80px',
            marginBottom: '80px',
            position: 'relative',
            overflow: 'hidden'
        }}>
            {/* ЛЕВОЕ ФОТО (Зефир) */}
            <div style={{
                position: 'absolute',
                top: '10%',
                left: '-50px',
                width: '250px',
                height: '350px',
                background: '#eee',
                borderRadius: '20px',
                transform: 'rotate(-10deg)',
                boxShadow: '0 20px 40px rgba(0,0,0,0.05)',
                overflow: 'hidden',
                zIndex: 1
            }}>
                <img
                    src="/images/zephyr.jpg"
                    alt="Зефир"
                    style={{ width: '100%', height: '100%', objectFit: 'cover', opacity: 0.9 }}
                />
            </div>

            <div style={{
                textTransform: 'uppercase',
                letterSpacing: '8px',
                fontSize: '19px',
                color: '#999',
                marginBottom: '20px',
                fontFamily: 'var(--body-font)',
                fontWeight: '600',
                zIndex: 2
            }}>
                Vio — эстетика. Solo — качество.
            </div>

            <h1 style={{
                fontSize: 'clamp(48px, 8vw, 90px)',
                lineHeight: '1',
                maxWidth: '1000px',
                marginBottom: '35px',
                fontFamily: 'var(--title-font)',
                fontWeight: 'normal',
                color: '#222',
                zIndex: 2
            }}>
                Мастерская Виолетты
            </h1>

            <div style={{ maxWidth: '600px', marginBottom: '50px', zIndex: 2 }}>
                <p style={{
                    color: '#555',
                    fontSize: '27px',
                    lineHeight: '1.6',
                    fontFamily: 'var(--body-font)',
                    margin: '0 0 15px 0'
                }}>
                    Кондитерское искусство в каждом движении.
                </p>
                <p style={{
                    color: '#888',
                    fontSize: '20px',
                    lineHeight: '1.8',
                    fontFamily: 'var(--body-font)',
                    margin: 0
                }}>
                    Авторские рецепты, натуральные ингредиенты и безупречный стиль.
                    Создаем десерты, которые вдохновляют.
                </p>
            </div>

            <button
                onClick={scrollToCatalog}
                style={{
                    background: '#222',
                    color: 'white',
                    border: 'none',
                    padding: '22px 60px',
                    borderRadius: '0px',
                    fontFamily: 'var(--body-font)',
                    fontWeight: '500',
                    fontSize: '14px',
                    letterSpacing: '3px',
                    textTransform: 'uppercase',
                    cursor: 'pointer',
                    boxShadow: '0 10px 30px rgba(0,0,0,0.1)',
                    transition: '0.4s',
                    zIndex: 2
                }}
                onMouseOver={(e) => {
                    e.target.style.background = '#444';
                    e.target.style.transform = 'scale(1.05)';
                }}
                onMouseOut={(e) => {
                    e.target.style.background = '#222';
                    e.target.style.transform = 'scale(1)';
                }}
            >
                Перейти к ассортименту
            </button>

            {/* ПРАВОЕ ФОТО (Торт) */}
            <div style={{
                position: 'absolute',
                bottom: '-30px',
                right: '-40px',
                width: '300px',
                height: '400px',
                background: '#eee',
                borderRadius: '20px',
                transform: 'rotate(8deg)',
                boxShadow: '0 20px 40px rgba(0,0,0,0.05)',
                overflow: 'hidden',
                zIndex: 1
            }}>
                <img
                    src="/images/cake.jpg"
                    alt="Торт"
                    style={{ width: '100%', height: '100%', objectFit: 'cover', opacity: 0.9 }}
                />
            </div>
        </section>
    );
}