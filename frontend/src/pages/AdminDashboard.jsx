import React, { useState, useEffect } from 'react';
import api from '../api';
import Swal from 'sweetalert2';
import ArchiveTable from '../components/ArchiveTable';
import OrdersTable from '../components/OrdersTable';
import OrderCreateForm from '../components/OrderCreateForm';
import ProductCreateForm from '../components/ProductCreateForm';
import CategoryCreateForm from '../components/CategoryCreateForm';
import ProductEditForm from '../components/ProductEditForm';

const AdminDashboard = () => {
    const [activeTab, setActiveTab] = useState('orders');
    const [orders, setOrders] = useState([]);
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [ingredients, setIngredients] = useState([]);
    const [editingProduct, setEditingProduct] = useState(null);
    const [selectedOrder, setSelectedOrder] = useState(null);
    const [searchQuery, setSearchQuery] = useState('');
    const [filterCategory, setFilterCategory] = useState(null);

    const handleLogout = () => {
        localStorage.removeItem('token');
        window.location.href = '/';
    };

    useEffect(() => {
        if (activeTab === 'orders') fetchOrders();

        if (activeTab === 'products_list' || activeTab === 'archive') {
            if (activeTab === 'archive') setFilterCategory(null);
            fetchProducts();
        }

        if (activeTab === 'categories_list') fetchCategories();
        if (activeTab === 'ingredients_list') fetchIngredients();
    }, [activeTab]);

    const fetchOrders = async () => {
        try {
            const res = await api.get('/orders');
            setOrders(res.data);
        } catch (err) { console.error(err); }
    };

    const handleSearch = async (e) => {
        const q = e.target.value;
        setSearchQuery(q);
        try {
            const endpoint = q.trim() ? `/orders/search?q=${q}` : '/orders';
            const res = await api.get(endpoint);
            setOrders(res.data);
        } catch (err) { console.error(err); }
    };

    const handleUpdateStatus = async (id, newStatus) => {
        try {
            await api.patch(`/orders/${id}/status?status=${newStatus}`);
            Swal.fire({ title: 'Обновлено', icon: 'success', timer: 1000, showConfirmButton: false });
            fetchOrders();
        } catch (err) { Swal.fire('Ошибка', 'Не удалось обновить статус', 'error'); }
    };

    const handleDeleteOrder = async (id, orderNumber) => {
        Swal.fire({
            title: 'Удалить заказ?',
            text: `Заказ ${orderNumber} будет стерт навсегда`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#000',
            confirmButtonText: 'Удалить',
            cancelButtonText: 'Отмена'
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    await api.delete(`/orders/${id}`);
                    Swal.fire('Удалено', '', 'success');
                    fetchOrders();
                } catch (err) { Swal.fire('Ошибка', 'Удаление не удалось', 'error'); }
            }
        });
    };

    const fetchProducts = async () => {
        try {
            const res = await api.get('/products/all');
            setProducts(res.data);
        } catch (err) { console.error(err); }
    };

    const fetchCategories = async () => {
        try {
            const res = await api.get('/categories');
            setCategories(res.data);
        } catch (err) { console.error(err); }
    };

    const fetchIngredients = async () => {
        try {
            const res = await api.get('/ingredients');
            setIngredients(res.data);
        } catch (err) { console.error(err); }
    };

    const handleDeactivate = async (id) => {
        try {
            await api.delete(`/products/${id}`);
            Swal.fire('В архиве', '', 'success');
            fetchProducts();
        } catch (err) { console.error(err); }
    };

    const handleRestore = async (id) => {
        try {
            await api.patch(`/products/${id}/restore`);
            fetchProducts();
        } catch (err) { console.error(err); }
    };

    const handleDeletePermanent = async (endpoint, id, refreshFunc) => {
        Swal.fire({
            title: 'Удалить навсегда?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            confirmButtonText: 'Удалить'
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    await api.delete(`${endpoint}/${id}`);
                    refreshFunc();
                } catch (err) { console.error(err); }
            }
        });
    };

    const commonStyles = {
        table: tableStyle, th: thStyle, td: tdStyle, tr: trStyle,
        img: imgLarge, btn: actionBtn, select: selectStyle
    };

    return (
        <div style={adminContainer}>
            <div style={topActions}>
                <a href="/" style={secondaryBtn}>← На сайт</a>
                <button style={logoutBtn} onClick={handleLogout}>Выйти</button>
            </div>

            <h1 style={headerStyle}>Viosolocake Admin</h1>

            <div style={tabBar}>
                <div style={tabGroup}>
                    <button onClick={() => setActiveTab('orders')} style={activeTab === 'orders' ? activeTabBtn : tabBtn}>Заказы</button>
                    <button onClick={() => setActiveTab('products_list')} style={activeTab === 'products_list' ? activeTabBtn : tabBtn}>Товары</button>
                    <button onClick={() => setActiveTab('archive')} style={activeTab === 'archive' ? activeTabBtn : tabBtn}>Архив</button>
                    <button onClick={() => setActiveTab('categories_list')} style={activeTab === 'categories_list' ? activeTabBtn : tabBtn}>Категории</button>
                    <button onClick={() => setActiveTab('ingredients_list')} style={activeTab === 'ingredients_list' ? activeTabBtn : tabBtn}>Ингредиенты</button>
                </div>
                <div style={tabGroup}>
                    <button onClick={() => setActiveTab('phone_order')} style={activeTab === 'phone_order' ? activeTabBtn : tabBtn}>📞 По телефону</button>
                    <button onClick={() => setActiveTab('add_product')} style={activeTab === 'add_product' ? activeTabBtn : tabBtn}>+ Товар</button>
                    <button onClick={() => setActiveTab('add_category')} style={activeTab === 'add_category' ? activeTabBtn : tabBtn}>+ Категория</button>
                </div>
            </div>

            <div style={contentArea}>
                {activeTab === 'orders' && (
                    <>
                        <div style={searchWrapper}>
                            <span style={searchIcon}>🔍</span>
                            <input type="text" placeholder="Поиск..." value={searchQuery} onChange={handleSearch} style={searchInputStyle} />
                        </div>
                        <OrdersTable orders={orders} onUpdateStatus={handleUpdateStatus} onDeleteOrder={handleDeleteOrder} onViewDetails={setSelectedOrder} styles={commonStyles} />
                    </>
                )}

                {activeTab === 'products_list' && (
                    <>
                        {filterCategory && (
                            <div style={{marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '15px'}}>
                                <span>Показаны товары категории: <strong>{filterCategory}</strong></span>
                                <button
                                    style={{...secondaryBtn, padding: '8px 15px'}}
                                    onClick={() => {
                                        setFilterCategory(null);
                                        fetchProducts();
                                    }}
                                >
                                    Сбросить фильтр ✕
                                </button>
                            </div>
                        )}
                        <table style={tableStyle}>
                            <thead>
                            <tr>
                                <th style={thStyle}>Фото</th>
                                <th style={thStyle}>Название</th>
                                <th style={thStyle}>Цена</th>
                                <th style={thStyle}>Действия</th>
                            </tr>
                            </thead>
                            <tbody>
                            {products
                                .filter(p => p.active === true || p.active === 1)
                                .filter(p => filterCategory ? p.categoryName === filterCategory : true)
                                .map(p => (
                                    <tr key={p.id} style={trStyle}>
                                        <td style={tdStyle}><img src={p.imageUrl} alt="" style={imgLarge}/></td>
                                        <td style={tdStyle}>{p.name}</td>
                                        <td style={tdStyle}>{p.price} BYN</td>
                                        <td style={tdStyle}>
                                            <button style={editBtn} onClick={() => setEditingProduct(p)}>ИЗМЕНИТЬ</button>
                                            <button style={delLink} onClick={() => handleDeactivate(p.id)}>В архив</button>
                                        </td>
                                    </tr>
                                ))
                            }
                            </tbody>
                        </table>
                    </>
                )}

                {activeTab === 'archive' && (
                    <ArchiveTable
                        products={products.filter(p => p.active === false || p.active === 0)}
                        onRestore={handleRestore}
                        styles={commonStyles}
                    />
                )}

                {activeTab === 'categories_list' && (
                    <table style={tableStyle}>
                        <thead>
                        <tr>
                            <th style={thStyle}>Фото</th>
                            <th style={thStyle}>Название (нажми для просмотра товаров)</th>
                            <th style={thStyle}></th>
                        </tr>
                        </thead>
                        <tbody>
                        {categories.map(c => (
                            <tr
                                key={c.id}
                                style={{...trStyle, cursor: 'pointer'}}
                                onClick={async () => {
                                    try {
                                        const res = await api.get(`/products?categoryId=${c.id}`);
                                        setProducts(res.data);
                                        setFilterCategory(c.name);
                                        setActiveTab('products_list');
                                    } catch (err) {
                                        console.error("Ошибка при фильтрации категорий:", err);
                                    }
                                }}
                            >
                                <td style={tdStyle}><img src={c.imageUrl} alt="" style={imgLarge}/></td>
                                <td style={{...tdStyle, fontWeight: '600', color: '#000'}}>{c.name}</td>
                                <td style={tdStyle} onClick={(e) => e.stopPropagation()}>
                                    <button style={delLink} onClick={() => handleDeletePermanent('/categories', c.id, fetchCategories)}>
                                        Удалить
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}

                {activeTab === 'ingredients_list' && (
                    <div style={gridContainer}>
                        {ingredients.map(ing => (
                            <div key={ing.id} style={ingCard} className="ing-card">
                                <div style={ingInfo}>
                                    <span style={ingName}>{ing.name}</span>
                                    <div className="ing-tooltip" style={tooltipStyle}>
                                        {ing.description || 'Описание отсутствует'}
                                    </div>
                                </div>
                                <button style={delSmall} onClick={() => handleDeletePermanent('/ingredients', ing.id, fetchIngredients)}>×</button>
                            </div>
                        ))}
                    </div>
                )}

                {activeTab === 'phone_order' && <div style={phoneOrderWrapper}><OrderCreateForm isAdmin={true} onSuccess={() => { fetchOrders(); setActiveTab('orders'); }} /></div>}
                {activeTab === 'add_product' && <ProductCreateForm onSuccess={() => fetchProducts()} />}
                {activeTab === 'add_category' && <CategoryCreateForm onSuccess={() => fetchCategories()} />}
            </div>

            {selectedOrder && (
                <div style={overlay} onClick={() => setSelectedOrder(null)}>
                    <div style={{...modal, padding: '40px', maxWidth: '800px'}} onClick={e => e.stopPropagation()}>
                        <h2 style={{fontWeight:'300', marginBottom:'20px'}}>Заказ {selectedOrder.orderNumber}</h2>
                        <div style={detailsGrid}>
                            <div><strong>Клиент:</strong> {selectedOrder.userName || 'Гость'}</div>
                            <div><strong>Телефон:</strong> {selectedOrder.guestPhone || '—'}</div>
                            <div style={{gridColumn:'span 2'}}><strong>Адрес:</strong> {selectedOrder.deliveryAddress}</div>
                        </div>
                        <div style={{marginTop:'20px', maxHeight:'200px', overflowY:'auto'}}>
                            {selectedOrder.products.map((p, i) => <div key={i} style={{display:'flex', justifyContent:'space-between', padding:'5px 0'}}>{p.name} <span>{p.price} BYN</span></div>)}
                        </div>
                        <h3 style={{textAlign:'right', marginTop:'20px'}}>Итого: {selectedOrder.totalAmount} BYN</h3>
                        <button onClick={() => setSelectedOrder(null)} style={{...actionBtn, width:'100%', marginTop:'20px'}}>ЗАКРЫТЬ</button>
                    </div>
                </div>
            )}

            {editingProduct && (
                <div style={overlay} onClick={() => setEditingProduct(null)}>
                    <div style={modal} onClick={e => e.stopPropagation()}>
                        <ProductEditForm product={editingProduct} categories={categories} onCancel={() => setEditingProduct(null)} onSave={() => { setEditingProduct(null); fetchProducts(); }} />
                    </div>
                </div>
            )}

            <style>{`
                .ing-card { position: relative; transition: 0.3s; cursor: help; }
                .ing-card:hover { background: #fff !important; box-shadow: 0 10px 20px rgba(0,0,0,0.05); transform: translateY(-3px); }
                .ing-tooltip { 
                    visibility: hidden; opacity: 0; position: absolute; bottom: 100%; left: 0; 
                    background: #333; color: #fff; padding: 12px; border-radius: 10px; 
                    width: 220px; font-size: 0.8rem; z-index: 10; transition: 0.3s;
                    margin-bottom: 10px; box-shadow: 0 5px 15px rgba(0,0,0,0.2);
                }
                .ing-card:hover .ing-tooltip { visibility: visible; opacity: 1; }
            `}</style>
        </div>
    );
};

const adminContainer = { padding: '80px 5%', maxWidth: '1600px', margin: '0 auto', fontFamily: 'Inter, system-ui, sans-serif', fontSize: '1.1rem', position: 'relative' };
const headerStyle = { fontWeight: '400', textTransform: 'uppercase', letterSpacing: '6px', textAlign: 'center', marginBottom: '60px', fontSize: '2.5rem' };
const tabBar = { display: 'flex', justifyContent: 'space-between', borderBottom: '2px solid #eee', marginBottom: '50px' };
const tabGroup = { display: 'flex', gap: '10px' };
const tabBtn = { padding: '20px 35px', border: 'none', borderBottom: '4px solid transparent', background: 'none', cursor: 'pointer', color: '#aaa', fontSize: '1.1rem', transition: '0.3s' };
const activeTabBtn = { ...tabBtn, borderBottom: '4px solid #000', color: '#000', fontWeight: '700' };
const contentArea = { minHeight: '600px' };
const tableStyle = { width: '100%', borderCollapse: 'separate', borderSpacing: '0 10px' };
const thStyle = { textAlign: 'left', padding: '15px 25px', color: '#888', fontSize: '0.9rem', textTransform: 'uppercase', letterSpacing: '1.5px', borderBottom: '2px solid #f0f0f0' };
const tdStyle = { padding: '25px', background: '#fff', fontSize: '1.15rem', borderBottom: '1px solid #f0f0f0' };
const trStyle = { transition: '0.2s' };
const imgLarge = { width: '90px', height: '90px', objectFit: 'cover', borderRadius: '15px' };
const actionBtn = { padding: '16px 35px', background: '#000', color: '#fff', border: 'none', borderRadius: '12px', cursor: 'pointer', fontSize: '1.1rem', fontWeight: '600' };
const editBtn = { padding: '12px 24px', background: '#fff', border: '2px solid #000', borderRadius: '8px', cursor: 'pointer', fontSize: '0.95rem', marginRight: '12px', fontWeight: '700' };
const delLink = { background: 'none', border: 'none', color: '#ff4d4f', cursor: 'pointer', fontSize: '0.95rem', fontWeight: '600', textDecoration: 'underline' };
const selectStyle = { padding: '12px 18px', borderRadius: '12px', border: '2px solid #eee', fontSize: '1rem', outline: 'none' };
const searchWrapper = { position: 'relative', maxWidth: '700px', margin: '0 auto 60px', display: 'flex', alignItems: 'center' };
const searchIcon = { position: 'absolute', left: '25px', color: '#888', fontSize: '1.6rem' };
const searchInputStyle = { width: '100%', padding: '22px 25px 22px 75px', borderRadius: '60px', border: '2px solid #eee', outline: 'none', fontSize: '1.3rem', boxShadow: '0 4px 15px rgba(0,0,0,0.05)' };
const gridContainer = { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '30px' };
const ingCard = { padding: '30px', background: '#f9f9f9', borderRadius: '20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', border: '1px solid #eee' };
const ingInfo = { display: 'flex', flexDirection: 'column', gap: '10px' };
const ingName = { fontWeight: '600', fontSize: '1.4rem', color: '#000' };
const tooltipStyle = { lineHeight: '1.6', fontSize: '1.05rem', pointerEvents: 'none' };
const delSmall = { background: 'none', border: 'none', color: '#ff4d4f', cursor: 'pointer', fontSize: '2.2rem', padding: '0 10px' };
const overlay = { position: 'fixed', top: 0, left: 0, width: '100%', height: '100%', background: 'rgba(0,0,0,0.4)', backdropFilter: 'blur(8px)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 3000 };
const modal = { background: '#fff', borderRadius: '35px', width: '90%', maxWidth: '1150px', boxShadow: '0 30px 70px rgba(0,0,0,0.2)', padding: '50px' };
const detailsGrid = { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '25px', fontSize: '1.2rem' };
const phoneOrderWrapper = { maxWidth: '650px', margin: '0 auto' };
const topActions = { position: 'absolute', top: '25px', right: '5%', display: 'flex', gap: '15px', alignItems: 'center' };
const secondaryBtn = { padding: '10px 20px', background: '#fff', border: '1px solid #ddd', borderRadius: '10px', cursor: 'pointer', fontSize: '0.95rem', color: '#666', textDecoration: 'none', fontWeight: '500' };
const logoutBtn = { ...secondaryBtn, color: '#ff4d4f', borderColor: '#ff4d4f' };

export default AdminDashboard;