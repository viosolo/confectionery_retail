import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

const ProfilePage = () => {
    const [user, setUser] = useState(null);
    const [orders, setOrders] = useState([]);
    const [productsCount, setProductsCount] = useState(0);
    const [allOrdersCount, setAllOrdersCount] = useState(0);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const savedUser = JSON.parse(localStorage.getItem('user'));
        if (!savedUser) {
            navigate('/login');
        } else {
            setUser(savedUser);
            const isAdminUser = savedUser.role === 'ADMIN' || savedUser.role === 'ROLE_ADMIN';

            if (isAdminUser) {
                fetchAdminStats();
            } else {
                fetchUserOrders(savedUser.id);
            }
        }
    }, [navigate]);

    const fetchAdminStats = async () => {
        try {
            const [prodRes, orderRes] = await Promise.all([
                api.get('/products'),
                api.get('/orders')
            ]);
            setProductsCount(prodRes.data.length);
            setAllOrdersCount(orderRes.data.length);
        } catch (error) {
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    const fetchUserOrders = async (userId) => {
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
    const displayName = user.fullName || `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.username || "Пользователь";

    return (
        <div style={profileContainer}>
            <div style={sidebarStyle}>
                <h2 style={nameStyle}>{displayName}</h2>
                <p style={emailStyle}>{user.email}</p>

                <div style={actionsContainer}>
                    {isAdmin && (
                        <button onClick={() => navigate('/admin-dashboard')} style={adminBtnStyle}>
                            ПАНЕЛЬ УПРАВЛЕНИЯ
                        </button>
                    )}
                    <button onClick={handleLogout} style={logoutBtn}>
                        ВЫЙТИ ИЗ СИСТЕМЫ
                    </button>
                </div>
            </div>

            <div style={contentStyle}>
                <h3 style={sectionTitle}>
                    {isAdmin ? 'Обзор системы' : 'История моих заказов'}
                </h3>

                {loading ? (
                    <p style={infoText}>Загрузка данных...</p>
                ) : isAdmin ? (
                    <div style={adminDashboardContainer}>
                        <div style={adminHeader}>
                            <h2 style={adminWelcomeTitle}>Здравствуйте, {user.firstName || 'Администратор'}!</h2>
                            <span style={adminAccessBadge}>Привилегированный доступ</span>
                        </div>

                        <p style={adminDashboardDesc}>
                            Вам доступен полный контроль над мастерской Viosolocake. Управляйте заказами,
                            анализируйте продажи и обновляйте каталог товаров в режиме реального времени.
                        </p>

                        <div style={statsGrid}>
                            <div style={statCard}>
                                <div style={statValue}>{allOrdersCount}</div>
                                <div style={statLabel}>Заказов в системе</div>
                            </div>
                            <div style={statCard}>
                                <div style={statValue}>{productsCount}</div>
                                <div style={statLabel}>Товаров в каталоге</div>
                            </div>
                            <div style={statCard}>
                                <div style={statValue}>Active</div>
                                <div style={statLabel}>Статус сервера</div>
                            </div>
                        </div>
                    </div>
                ) : orders.length > 0 ? (
                    <div style={ordersListStyle}>
                        {orders.map((order) => (
                            <div key={order.id} style={orderCardStyle}>
                                <div style={orderHeader}>
                                    <div style={orderNumberText}>Заказ № {order.orderNumber}</div>
                                    <div style={orderDateText}>{new Date(order.createdAt).toLocaleDateString()}</div>
                                </div>
                                <div style={orderMainInfo}>
                                    <div style={infoRow}>
                                        <span style={labelStyle}>Статус:</span>
                                        <span style={statusBadge}>{order.statusName || order.status}</span>
                                    </div>
                                    <div style={infoRow}>
                                        <span style={labelStyle}>Сумма:</span>
                                        <span style={priceText}>{order.totalAmount} BYN</span>
                                    </div>
                                    <div style={infoRow}>
                                        <span style={labelStyle}>Товары:</span>
                                        <span style={productsText}>
                            {order.products && order.products.length > 0 ? (
                                Object.entries(
                                    order.products.reduce((acc, p) => {
                                        acc[p.name] = (acc[p.name] || 0) + 1;
                                        return acc;
                                    }, {})
                                ).map(([name, count], index, array) => (
                                    <span key={name}>
                                        {name} <strong style={{color: '#d2691e'}}>(x{count})</strong>
                                        {index < array.length - 1 ? ', ' : ''}
                                    </span>
                                ))
                            ) : (
                                'Заказ пуст'
                            )}
                        </span>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                ) : (
                    <div style={emptyStateBox}>
                        <p style={bigText}>У вас пока нет оформленных заказов.</p>
                        <button onClick={() => navigate('/')} style={shopBtn}>ПЕРЕЙТИ В КАТАЛОГ</button>
                    </div>
                )}
            </div>
        </div>
    );
};

const profileContainer = { display: 'flex', maxWidth: '1300px', margin: '60px auto', padding: '0 40px', gap: '80px', fontFamily: "'Helvetica Neue', Arial, sans-serif" };
const sidebarStyle = { flex: '0 0 280px', borderRight: '1px solid #f0f0f0', paddingRight: '40px' };
const contentStyle = { flex: '1' };
const nameStyle = { fontSize: '2.2rem', fontWeight: '300', margin: '0 0 10px 0', color: '#1d1d1f' };
const emailStyle = { color: '#86868b', fontSize: '1rem', marginBottom: '40px' };
const actionsContainer = { display: 'flex', flexDirection: 'column', gap: '12px' };
const sectionTitle = { textTransform: 'uppercase', fontSize: '1.5rem', letterSpacing: '2px', marginBottom: '35px', color: '#1d1d1f', fontWeight: '700' };

const logoutBtn = { background: '#fff', border: '1px solid #d2d2d7', padding: '14px 20px', cursor: 'pointer', fontSize: '0.9rem', width: '100%', borderRadius: '12px', color: '#1d1d1f' };
const adminBtnStyle = { background: '#000', color: '#fff', border: 'none', padding: '14px 20px', cursor: 'pointer', fontSize: '0.9rem', width: '100%', fontWeight: '600', borderRadius: '12px' };

const adminDashboardContainer = { display: 'flex', flexDirection: 'column', gap: '40px' };
const adminHeader = { display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '20px' };
const adminWelcomeTitle = { fontSize: '2.8rem', fontWeight: '300', margin: '0', color: '#1d1d1f' };
const adminAccessBadge = { background: '#f5f5f7', color: '#1d1d1f', padding: '8px 20px', borderRadius: '20px', fontSize: '0.95rem', fontWeight: '600', textTransform: 'uppercase', letterSpacing: '1px', border: '1px solid #e5e5e5' };
const adminDashboardDesc = { fontSize: '1.2rem', color: '#424245', lineHeight: '1.6', margin: '0', maxWidth: '700px' };

const statsGrid = { display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '20px' };
const statCard = { background: '#fafafa', border: '1px solid #f0f0f2', padding: '30px', borderRadius: '24px', textAlign: 'center' };
const statValue = { fontSize: '2.5rem', fontWeight: '600', color: '#000', marginBottom: '5px' };
const statLabel = { fontSize: '0.85rem', color: '#86868b', textTransform: 'uppercase', letterSpacing: '1px' };

const ordersListStyle = { display: 'flex', flexDirection: 'column', gap: '20px' };
const orderCardStyle = { border: '1px solid #e5e5e5', padding: '30px', borderRadius: '20px' };
const orderHeader = { display: 'flex', justifyContent: 'space-between', marginBottom: '20px', borderBottom: '1px solid #f5f5f7', paddingBottom: '15px' };
const orderNumberText = { fontSize: '1.1rem', fontWeight: '600' };
const orderDateText = { color: '#86868b', fontSize: '0.9rem' };
const orderMainInfo = { display: 'flex', flexDirection: 'column', gap: '10px' };
const infoRow = { display: 'flex', fontSize: '1rem' };
const labelStyle = { color: '#86868b', width: '120px' };
const statusBadge = { background: '#f5f5f7', padding: '4px 12px', borderRadius: '20px', fontSize: '0.8rem', fontWeight: '600' };
const priceText = { fontWeight: '700' };
const productsText = { color: '#424245', flex: 1 };

const emptyStateBox = { padding: '80px 40px', textAlign: 'center', background: '#fafafa', borderRadius: '30px', border: '1px solid #f0f0f2' };
const shopBtn = { background: '#000', color: '#fff', border: 'none', padding: '16px 40px', borderRadius: '30px', cursor: 'pointer', fontWeight: '600', marginTop: '20px' };
const bigText = { fontSize: '1.2rem', color: '#86868b' };
const infoText = { color: '#86868b', textAlign: 'center', marginTop: '40px' };

export default ProfilePage;