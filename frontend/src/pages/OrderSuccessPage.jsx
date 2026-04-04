import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const OrderSuccessPage = () => {
    const navigate = useNavigate();
    const [orderData, setOrderData] = useState({
        orderNumber: '...',
        totalAmount: '0.00'
    });

    useEffect(() => {
        const savedOrder = localStorage.getItem('lastOrder');
        if (savedOrder) {
            setOrderData(JSON.parse(savedOrder));
            // Очищаем, чтобы при случайном обновлении страницы данные не висели вечно
            // Но лучше оставить на время сессии
        }
    }, []);

    return (
        <div style={containerStyle}>
            <div style={cardStyle}>
                <div style={iconStyle}>✓</div>
                <h1 style={titleStyle}>Заказ успешно оформлен!</h1>
                <p style={subtitleStyle}>
                    Спасибо за ваш выбор! Мы уже получили ваш заказ и приступаем к его обработке.
                </p>

                <div style={detailsBoxStyle}>
                    <div style={rowStyle}>
                        <span>Номер заказа:</span>
                        <span style={boldStyle}>{orderData.orderNumber}</span>
                    </div>
                    <div style={rowStyle}>
                        <span>Сумма к оплате:</span>
                        <span style={boldStyle}>{orderData.totalAmount} BYN</span>
                    </div>
                    <div style={rowStyle}>
                        <span>Статус:</span>
                        <span style={{...boldStyle, color: '#4CAF50'}}>Принят</span>
                    </div>
                </div>

                <p style={infoStyle}>
                    Наш менеджер свяжется с вами в ближайшее время для подтверждения заказа и уточнения деталей доставки.
                </p>

                <button
                    onClick={() => navigate('/')}
                    style={buttonStyle}
                    onMouseOver={(e) => e.target.style.background = '#333'}
                    onMouseOut={(e) => e.target.style.background = '#000'}
                >
                    ВЕРНУТЬСЯ НА ГЛАВНУЮ
                </button>
            </div>
        </div>
    );
};

const containerStyle = {
    display: 'flex', justifyContent: 'center', alignItems: 'center',
    minHeight: '80vh', padding: '20px', background: '#fff', fontFamily: "'Helvetica Neue', sans-serif"
};

const cardStyle = {
    maxWidth: '500px', width: '100%', background: '#fff', padding: '40px',
    textAlign: 'center', border: '1px solid #eee'
};

const iconStyle = { fontSize: '40px', color: '#000', marginBottom: '20px', fontWeight: '100' };
const titleStyle = { fontSize: '1.6rem', fontWeight: '300', marginBottom: '15px', letterSpacing: '1px', textTransform: 'uppercase' };
const subtitleStyle = { color: '#666', lineHeight: '1.6', marginBottom: '30px', fontSize: '0.95rem' };
const detailsBoxStyle = { background: '#f9f9f9', padding: '20px', textAlign: 'left', marginBottom: '30px' };
const rowStyle = { display: 'flex', justifyContent: 'space-between', marginBottom: '10px', fontSize: '0.9rem' };
const boldStyle = { fontWeight: '600' };
const infoStyle = { fontSize: '0.8rem', color: '#999', marginBottom: '30px' };
const buttonStyle = {
    background: '#000', color: '#fff', border: 'none', padding: '18px',
    width: '100%', cursor: 'pointer', letterSpacing: '2px', fontSize: '0.8rem', transition: '0.3s'
};

export default OrderSuccessPage;