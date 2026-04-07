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
            setOrderInfo(prev => ({
                ...prev,
                guestName: `${savedUser.firstName || ''} ${savedUser.lastName || ''}`.trim(),
                guestPhone: savedUser.phone || ''
            }));
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
            guestName: user ? `${user.firstName} ${user.lastName}` : orderInfo.guestName,
            guestPhone: user ? user.phone : orderInfo.guestPhone,
            productIds: cartItems.flatMap(item => Array(item.count).fill(item.id)),
            deliveryAddress: orderInfo.deliveryAddress,
            paymentMethod: orderInfo.paymentMethod,
            notes: orderInfo.notes
        };

        try {
            const response = await api.post('/orders', orderRequest);
            if (response.status === 200 || response.status === 201) {
                const { orderNumber, totalAmount } = response.data;

                localStorage.setItem('lastOrder', JSON.stringify({
                    orderNumber: orderNumber,
                    totalAmount: totalAmount.toFixed(2)
                }));

                localStorage.removeItem('cart');
                window.dispatchEvent(new Event('cartUpdated'));
                navigate('/order-success');
            }
        } catch (error) {
            alert(error.response?.data?.message || "Ошибка при создании заказа");
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
                                <div style={userHeaderStyle}>
                                    Заказ для: {user.firstName} {user.lastName}
                                </div>
                                <div style={userInfoStyle}>{user.email}</div>
                                <div style={userInfoStyle}>{user.phone || 'Телефон не указан'}</div>
                            </div>
                        ) : (
                            <div style={{ marginBottom: '20px' }}>
                                <h3 style={sectionTitleStyle}>Контактные данные</h3>
                                <input
                                    required
                                    name="guestName"
                                    placeholder="Ваше полное имя"
                                    style={inputStyle}
                                    value={orderInfo.guestName}
                                    onChange={handleChange}
                                />
                                <input
                                    required
                                    name="guestPhone"
                                    placeholder="Номер телефона (+375...)"
                                    style={inputStyle}
                                    value={orderInfo.guestPhone}
                                    onChange={handleChange}
                                />
                            </div>
                        )}

                        <h3 style={sectionTitleStyle}>Адрес доставки</h3>
                        <textarea
                            required
                            name="deliveryAddress"
                            placeholder="Улица, дом, квартира..."
                            style={textareaStyle}
                            value={orderInfo.deliveryAddress}
                            onChange={handleChange}
                        />

                        <h3 style={sectionTitleStyle}>Дополнительные пожелания</h3>
                        <textarea
                            name="notes"
                            placeholder="Аллергии, пожелания по упаковке, текст для открытки..."
                            style={smallTextareaStyle}
                            value={orderInfo.notes}
                            onChange={handleChange}
                        />

                        <h3 style={sectionTitleStyle}>Способ оплаты</h3>
                        <select
                            name="paymentMethod"
                            style={inputStyle}
                            value={orderInfo.paymentMethod}
                            onChange={handleChange}
                        >
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
                        <h3 style={summaryTitleStyle}>Ваш выбор</h3>
                        {cartItems.map(item => (
                            <div key={item.id} style={itemRowStyle}>
                                <span>{item.name} x {item.count}</span>
                                <span style={{ fontWeight: '500' }}>
                                    {(item.price * item.count).toFixed(2)} BYN
                                </span>
                            </div>
                        ))}
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

const containerStyle = {
    maxWidth: '1200px',
    margin: '0 auto',
    padding: '80px 40px',
    fontFamily: "'Helvetica Neue', Arial, sans-serif"
};

const titleStyle = {
    fontSize: '3rem',
    marginBottom: '60px',
    fontWeight: '300',
    letterSpacing: '1px'
};

const layoutStyle = {
    display: 'flex',
    gap: '80px',
    flexWrap: 'wrap',
    alignItems: 'flex-start'
};

const leftColStyle = { flex: '1.5', minWidth: '350px' };
const rightColStyle = { flex: '1', minWidth: '320px' };
const formStyle = { display: 'flex', flexDirection: 'column' };

const userBadgeStyle = {
    background: '#f5f5f7',
    padding: '30px',
    borderRadius: '12px',
    marginBottom: '30px',
    borderLeft: '5px solid #000'
};

const userHeaderStyle = {
    fontSize: '1.25rem',
    fontWeight: '600',
    color: '#1d1d1f',
    marginBottom: '8px'
};

const userInfoStyle = {
    fontSize: '0.95rem',
    color: '#6e6e73',
    marginBottom: '4px'
};

const sectionTitleStyle = {
    fontSize: '0.75rem',
    textTransform: 'uppercase',
    letterSpacing: '2px',
    margin: '40px 0 15px',
    color: '#999',
    fontWeight: 'bold'
};

const inputStyle = {
    width: '100%',
    padding: '18px',
    marginBottom: '15px',
    border: '1px solid #e5e5e5',
    borderRadius: '8px',
    fontSize: '1rem',
    outline: 'none',
    background: '#fff',
    boxSizing: 'border-box',
    transition: 'border-color 0.2s'
};

const textareaStyle = { ...inputStyle, height: '120px', resize: 'none' };
const smallTextareaStyle = { ...inputStyle, height: '80px', resize: 'none' };

const summaryCardStyle = {
    background: '#fff',
    padding: '40px',
    borderRadius: '15px',
    border: '1px solid #f0f0f0',
    position: 'sticky',
    top: '40px',
    boxShadow: '0 4px 20px rgba(0,0,0,0.03)'
};

const summaryTitleStyle = {
    marginTop: 0,
    fontSize: '1.6rem',
    marginBottom: '25px',
    fontWeight: '500'
};

const itemRowStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    marginBottom: '15px',
    fontSize: '1rem',
    color: '#444'
};

const totalRowStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    fontWeight: 'bold',
    fontSize: '1.4rem',
    borderTop: '1px solid #eee',
    paddingTop: '25px',
    marginTop: '10px'
};

const submitBtnStyle = {
    background: '#000',
    color: '#fff',
    padding: '22px',
    border: 'none',
    borderRadius: '50px',
    width: '100%',
    cursor: 'pointer',
    fontSize: '1rem',
    letterSpacing: '2px',
    fontWeight: '600',
    marginTop: '40px',
    transition: 'background 0.3s ease'
};

export default CheckoutPage;