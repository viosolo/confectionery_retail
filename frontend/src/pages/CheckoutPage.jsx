import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

const CheckoutPage = () => {
    const navigate = useNavigate();
    const [cartItems, setCartItems] = useState([]);
    const [totalPrice, setTotalPrice] = useState(0);
    const [user, setUser] = useState(null);

    const [orderInfo, setOrderInfo] = useState({
        guestName: '',
        guestPhone: '',
        deliveryAddress: '',
        paymentMethod: 'CASH',
        notes: ''
    });

    useEffect(() => {
        const savedUser = JSON.parse(localStorage.getItem('user'));
        if (savedUser) {
            setUser(savedUser);
        }

        const savedCart = JSON.parse(localStorage.getItem('cart')) || [];
        if (savedCart.length === 0) {
            navigate('/cart');
            return;
        }
        setCartItems(savedCart);

        const total = savedCart.reduce((sum, item) => sum + (item.price * item.count), 0);
        setTotalPrice(total);
    }, [navigate]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setOrderInfo(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const orderRequest = {
            userId: user ? user.id : null,
            guestName: user ? null : orderInfo.guestName,
            guestPhone: user ? null : orderInfo.guestPhone,
            productIds: cartItems.flatMap(item => Array(item.count).fill(item.id)),
            deliveryAddress: orderInfo.deliveryAddress,
            paymentMethod: orderInfo.paymentMethod,
            notes: orderInfo.notes
        };

        try {
            const response = await api.post('/orders', orderRequest);

            // ПРОВЕРЯЕМ УСПЕХ И СОХРАНЯЕМ ДАННЫЕ
            if (response.status === 200 || response.status === 201) {
                // Извлекаем номер и сумму из ответа бэкенда (твоего OrderResponseDto)
                const { orderNumber, totalAmount } = response.data;

                // Сохраняем в localStorage, чтобы SuccessPage могла их прочитать
                localStorage.setItem('lastOrder', JSON.stringify({
                    orderNumber: orderNumber,
                    totalAmount: totalAmount.toFixed(2)
                }));

                localStorage.removeItem('cart');
                window.dispatchEvent(new Event('cartUpdated'));

                // Теперь переходим
                navigate('/order-success');
            }
        } catch (error) {
            console.error("Ошибка заказа:", error);
            const errorMsg = error.response?.data?.message || "Ошибка при создании заказа";
            alert(errorMsg);
        }
    };

    return (
        <div style={containerStyle}>
            <h1 style={titleStyle}>Оформление заказа</h1>

            <div style={layoutStyle}>
                <div style={leftColStyle}>
                    <form onSubmit={handleSubmit} style={formStyle}>

                        {user ? (
                            <div style={userBadgeStyle}>
                                <h3 style={{margin: '0 0 5px 0'}}>Заказ для: {user.firstName} {user.lastName}</h3>
                                <p style={{margin: 0, color: '#666', fontSize: '0.9rem'}}>{user.email}</p>
                                <p style={{margin: '5px 0 0 0', color: '#666', fontSize: '0.9rem'}}>{user.phone}</p>
                            </div>
                        ) : (
                            <>
                                <h3 style={sectionTitleStyle}>Контактные данные</h3>
                                <input
                                    required
                                    name="guestName"
                                    placeholder="Ваше полное имя"
                                    style={inputStyle}
                                    onChange={handleChange}
                                />
                                <input
                                    required
                                    name="guestPhone"
                                    placeholder="Номер телефона (+375...)"
                                    style={inputStyle}
                                    onChange={handleChange}
                                />
                            </>
                        )}

                        <h3 style={sectionTitleStyle}>Адрес доставки</h3>
                        <textarea
                            required
                            name="deliveryAddress"
                            placeholder="Улица, дом, квартира..."
                            style={{...inputStyle, height: '80px', resize: 'none'}}
                            onChange={handleChange}
                        />

                        <h3 style={sectionTitleStyle}>Дополнительные пожелания</h3>
                        <textarea
                            name="notes"
                            placeholder="Аллергии, пожелания по упаковке, текст для открытки..."
                            style={{...inputStyle, height: '60px', resize: 'none'}}
                            onChange={handleChange}
                        />

                        <h3 style={sectionTitleStyle}>Способ оплаты</h3>
                        <select name="paymentMethod" style={inputStyle} onChange={handleChange}>
                            <option value="CASH">Наличными при получении</option>
                            <option value="CARD_ON_DELIVERY">Картой курьеру</option>
                            <option value="ONLINE_PAYMENT">Оплата на сайте</option>
                        </select>

                        <button type="submit" style={submitBtnStyle}>
                            ПОДТВЕРДИТЬ ЗАКАЗ — {totalPrice.toFixed(2)} BYN
                        </button>
                    </form>
                </div>

                <div style={rightColStyle}>
                    <div style={summaryCardStyle}>
                        <h3 style={{marginTop: 0, fontFamily: 'serif'}}>Ваш выбор</h3>
                        {cartItems.map(item => (
                            <div key={item.id} style={itemRowStyle}>
                                <span>{item.name} x {item.count}</span>
                                <span>{(item.price * item.count).toFixed(2)} BYN</span>
                            </div>
                        ))}
                        <hr style={hrStyle} />
                        <div style={totalRowStyle}>
                            <span>Итого к оплате</span>
                            <span>{totalPrice.toFixed(2)} BYN</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

const containerStyle = { maxWidth: '1100px', margin: '0 auto', padding: '60px 20px', fontFamily: "'Helvetica Neue', sans-serif" };
const titleStyle = { fontSize: '2.5rem', marginBottom: '40px', fontWeight: '200', letterSpacing: '1px' };
const layoutStyle = { display: 'flex', gap: '60px', flexWrap: 'wrap' };
const leftColStyle = { flex: '1.4', minWidth: '320px' };
const rightColStyle = { flex: '1', minWidth: '300px' };
const formStyle = { display: 'flex', flexDirection: 'column' };
const userBadgeStyle = { background: '#f0f0f0', padding: '20px', borderRadius: '8px', marginBottom: '20px', borderLeft: '4px solid #333' };
const sectionTitleStyle = { fontSize: '0.9rem', textTransform: 'uppercase', letterSpacing: '1.5px', margin: '30px 0 15px', color: '#888' };
const inputStyle = { width: '100%', padding: '15px', marginBottom: '10px', border: '1px solid #e0e0e0', borderRadius: '2px', fontSize: '1rem', outline: 'none' };
const summaryCardStyle = { background: '#fff', padding: '30px', border: '1px solid #eee', position: 'sticky', top: '40px' };
const itemRowStyle = { display: 'flex', justifyContent: 'space-between', marginBottom: '12px', fontSize: '0.95rem' };
const hrStyle = { border: 'none', borderTop: '1px solid #eee', margin: '20px 0' };
const totalRowStyle = { display: 'flex', justifyContent: 'space-between', fontWeight: '600', fontSize: '1.3rem' };
const submitBtnStyle = { background: '#000', color: '#fff', padding: '20px', border: 'none', width: '100%', cursor: 'pointer', fontSize: '1rem', letterSpacing: '2px', marginTop: '30px', transition: 'opacity 0.3s' };

export default CheckoutPage;