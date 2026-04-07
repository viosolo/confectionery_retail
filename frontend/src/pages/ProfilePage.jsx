import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

const ProfilePage = () => {
    const [user, setUser] = useState(null);
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const savedUser = JSON.parse(localStorage.getItem('user'));
        if (!savedUser) {
            navigate('/login');
        } else {
            setUser(savedUser);
            if (savedUser.role !== 'ADMIN') {
                fetchOrders(savedUser.id);
            } else {
                setLoading(false);
            }
        }
    }, [navigate]);

    const fetchOrders = async (userId) => {
        try {
            const response = await api.get(`/orders/user/${userId}`);
            setOrders(response.data);
        } catch (error) {
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('user');
        localStorage.removeItem('cart');
        window.dispatchEvent(new Event('storage'));
        navigate('/');
    };

    if (!user) return null;

    const isAdmin = user.role === 'ADMIN' || user.role === 'ROLE_ADMIN';

    return (
        <div style={profileContainer}>
            <div style={sidebarStyle}>
                <h2 style={nameStyle}>{user.firstName} {user.lastName}</h2>
                <p style={emailStyle}>{user.email}</p>

                {isAdmin && (
                    <button onClick={() => navigate('/admin')} style={adminBtnStyle}>
                        ПАНЕЛЬ УПРАВЛЕНИЯ
                    </button>
                )}

                <button onClick={handleLogout} style={logoutBtn}>ВЫЙТИ ИЗ СИСТЕМЫ</button>
            </div>

            <div style={contentStyle}>
                <h3 style={sectionTitle}>
                    {isAdmin ? 'Статус аккаунта: Администратор' : 'История моих заказов'}
                </h3>

                {loading ? (
                    <p>Загрузка...</p>
                ) : isAdmin ? (
                    <div style={placeholderBox}>
                        <p>Вы вошли как администратор Viosolocake.</p>
                        <button onClick={() => navigate('/admin')} style={shopBtn}>ПЕРЕЙТИ В АДМИНКУ</button>
                    </div>
                ) : orders.length > 0 ? (
                    <div style={ordersListStyle}>
                        {orders.map((order) => (
                            <div key={order.id} style={orderCardStyle}>
                                <div style={orderHeader}>
                                    <strong>Заказ № {order.orderNumber}</strong>
                                    <span>{new Date(order.createdAt).toLocaleDateString()}</span>
                                </div>
                                <div style={orderInfo}>
                                    <p>Статус: <span style={statusBadge}>{order.status}</span></p>
                                    <p>Сумма: <strong>{order.totalAmount} BYN</strong></p>
                                    <p>Товары: {order.productNames?.join(', ')}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                ) : (
                    <div style={placeholderBox}>
                        <p>У вас пока нет завершенных заказов.</p>
                        <button onClick={() => navigate('/')} style={shopBtn}>ПЕРЕЙТИ В КАТАЛОГ</button>
                    </div>
                )}
            </div>
        </div>
    );
};

const profileContainer = { display: 'flex', maxWidth: '1100px', margin: '60px auto', padding: '0 20px', gap: '50px' };
const sidebarStyle = { flex: '1', borderRight: '1px solid #eee', paddingRight: '40px' };
const contentStyle = { flex: '2' };
const nameStyle = { fontSize: '1.8rem', fontWeight: '300', margin: '0 0 10px 0' };
const emailStyle = { color: '#888', marginBottom: '30px' };
const sectionTitle = { textTransform: 'uppercase', fontSize: '1.3rem', letterSpacing: '2px', marginBottom: '30px' };
const logoutBtn = { background: 'none', border: '1px solid #ddd', padding: '10px 20px', cursor: 'pointer', fontSize: '0.8rem', letterSpacing: '1px', width: '100%' };
const adminBtnStyle = { background: '#2D2D2D', color: '#fff', border: 'none', padding: '12px 20px', cursor: 'pointer', fontSize: '0.8rem', letterSpacing: '1px', marginBottom: '15px', width: '100%', fontWeight: '600' };
const placeholderBox = { padding: '40px', border: '1px dotted #ccc', textAlign: 'center', color: '#999' };
const shopBtn = { background: '#000', color: '#fff', border: 'none', padding: '12px 25px', marginTop: '15px', cursor: 'pointer' };
const ordersListStyle = { display: 'flex', flexDirection: 'column', gap: '20px' };
const orderCardStyle = { border: '1px solid #eee', padding: '20px', borderRadius: '4px' };
const orderHeader = { display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid #f9f9f9', marginBottom: '10px', paddingBottom: '5px' };
const orderInfo = { fontSize: '0.9rem', color: '#555', lineHeight: '1.6' };
const statusBadge = { background: '#f0f0f0', padding: '2px 8px', borderRadius: '10px', fontSize: '0.8rem' };

export default ProfilePage;