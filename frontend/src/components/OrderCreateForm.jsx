import React, { useState, useEffect } from 'react';
import api from '../api';

const OrderCreateForm = ({ isAdmin = false, onSuccess }) => {
    const [products, setProducts] = useState([]);
    const [selectedItems, setSelectedItems] = useState([]);
    const [user, setUser] = useState(null); // Храним объект пользователя
    const [orderInfo, setOrderInfo] = useState({
        guestName: '',
        guestPhone: '',
        deliveryAddress: '',
        paymentMethod: 'CASH',
        notes: ''
    });

    useEffect(() => {
        const userData = JSON.parse(localStorage.getItem('user'));
        if (userData) {
            setUser(userData);
            setOrderInfo(prev => ({
                ...prev,
                guestName: userData.fullName || userData.username || '',
                guestPhone: userData.phone || ''
            }));
        }

        if (isAdmin) {
            api.get('/products/all').then(res =>
                setProducts(res.data.filter(p => p.active !== false))
            );
        } else {
            const savedCart = JSON.parse(localStorage.getItem('cart')) || [];
            setSelectedItems(savedCart);
        }
    }, [isAdmin]);

    const handleAddItem = (productId) => {
        const product = products.find(p => p.id === parseInt(productId));
        if (product) {
            setSelectedItems(prev => {
                const existing = prev.find(item => item.id === product.id);
                if (existing) {
                    return prev.map(item => item.id === product.id ? {...item, count: item.count + 1} : item);
                }
                return [...prev, { ...product, count: 1 }];
            });
        }
    };

    const totalPrice = selectedItems.reduce((sum, item) => sum + (item.price * item.count), 0);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const productIds = selectedItems.flatMap(item => Array(item.count).fill(item.id));
        const orderRequest = { ...orderInfo, productIds: productIds };

        try {
            const response = await api.post('/orders', orderRequest);
            onSuccess(response.data);
            if (!isAdmin) localStorage.removeItem('cart');
        } catch (error) {
            alert(error.response?.data?.message || "Ошибка");
        }
    };

    return (
        <form onSubmit={handleSubmit} style={formStyle}>

            <div style={sectionStyle}>
                {user && !isAdmin ? (

                    <div style={userBadgeStyle}>
                        <div style={{fontWeight: '600', color: '#333', marginBottom: '4px'}}>
                            Заказ для: {user.fullName || user.username}
                        </div>
                        <div style={{fontSize: '0.85rem', color: '#777'}}>
                            {user.email}<br/>
                            {user.phone || 'Телефон не указан'}
                        </div>
                    </div>
                ) : (

                    <>
                        <h3 style={sectionTitleStyle}>Контактные данные</h3>
                        <input required placeholder="Имя заказчика" style={inputStyle}
                               value={orderInfo.guestName}
                               onChange={e => setOrderInfo({...orderInfo, guestName: e.target.value})} />
                        <input required placeholder="Телефон" style={inputStyle}
                               value={orderInfo.guestPhone}
                               onChange={e => setOrderInfo({...orderInfo, guestPhone: e.target.value})} />
                    </>
                )}
            </div>

            {isAdmin && (
                <div style={sectionStyle}>
                    <h3 style={sectionTitleStyle}>Добавить в заказ</h3>
                    <select style={inputStyle} onChange={(e) => handleAddItem(e.target.value)} value="">
                        <option value="" disabled>-- Выберите из меню --</option>
                        {products.map(p => (
                            <option key={p.id} value={p.id}>{p.name} — {p.price} BYN</option>
                        ))}
                    </select>
                </div>
            )}

            <div style={summaryBox}>
                <h4 style={{margin: '0 0 10px 0', fontSize: '0.9rem'}}>Ваш выбор:</h4>
                {selectedItems.map(item => (
                    <div key={item.id} style={itemRow}>
                        <span>{item.name} x {item.count}</span>
                        <span>{(item.price * item.count).toFixed(2)} BYN</span>
                    </div>
                ))}
                <div style={totalLine}>Итого: {totalPrice.toFixed(2)} BYN</div>
            </div>

            <div style={sectionStyle}>
                <h3 style={sectionTitleStyle}>Адрес доставки</h3>
                <textarea required placeholder="Улица, дом, квартира..." style={{...inputStyle, height: '80px'}}
                          value={orderInfo.deliveryAddress}
                          onChange={e => setOrderInfo({...orderInfo, deliveryAddress: e.target.value})} />

                <h3 style={sectionTitleStyle}>Способ оплаты</h3>
                <select style={inputStyle} value={orderInfo.paymentMethod}
                        onChange={e => setOrderInfo({...orderInfo, paymentMethod: e.target.value})}>
                    <option value="CASH">Наличными при получении</option>
                    <option value="CARD_ON_DELIVERY">Картой курьеру</option>
                    <option value="ONLINE_PAYMENT">Оплата на сайте</option>
                </select>

                <h3 style={sectionTitleStyle}>Дополнительные пожелания</h3>
                <textarea placeholder="Аллергии, пожелания по упаковке..." style={{...inputStyle, height: '60px'}}
                          value={orderInfo.notes}
                          onChange={e => setOrderInfo({...orderInfo, notes: e.target.value})} />
            </div>

            <button type="submit" disabled={selectedItems.length === 0} style={submitBtnStyle}>
                ПОДТВЕРДИТЬ ЗАКАЗ — {totalPrice.toFixed(2)} BYN
            </button>
        </form>
    );
};

const formStyle = { display: 'flex', flexDirection: 'column', gap: '5px' };
const sectionStyle = { marginBottom: '20px' };
const sectionTitleStyle = { fontSize: '0.7rem', textTransform: 'uppercase', letterSpacing: '1.5px', color: '#999', marginBottom: '10px', fontWeight: '600' };
const inputStyle = { width: '100%', padding: '15px', border: '1px solid #eee', borderRadius: '8px', fontSize: '0.95rem', marginBottom: '10px', outline: 'none', background: '#fff', boxSizing: 'border-box' };
const userBadgeStyle = { background: '#f2f3f5', padding: '20px', borderRadius: '12px', borderLeft: '4px solid #333', marginBottom: '10px' };
const summaryBox = { background: '#fff', border: '1px solid #eee', padding: '20px', borderRadius: '12px', marginBottom: '20px' };
const itemRow = { display: 'flex', justifyContent: 'space-between', fontSize: '0.9rem', marginBottom: '8px', color: '#555' };
const totalLine = { borderTop: '1px solid #eee', marginTop: '15px', paddingTop: '15px', fontWeight: 'bold', textAlign: 'right', fontSize: '1.1rem' };
const submitBtnStyle = { background: '#000', color: '#fff', padding: '20px', border: 'none', cursor: 'pointer', letterSpacing: '2px', fontWeight: '600', borderRadius: '40px', transition: '0.3s' };

export default OrderCreateForm;