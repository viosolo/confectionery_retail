import React, { useState, useEffect } from 'react';
import api from '../api';
import ArchiveTable from '../components/ArchiveTable';
import ProductCreateForm from '../components/ProductCreateForm';
import CategoryCreateForm from '../components/CategoryCreateForm';
import ProductEditForm from '../components/ProductEditForm';

const AdminDashboard = () => {
    const [activeTab, setActiveTab] = useState('orders');
    const [orders, setOrders] = useState([]);
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [ingredients, setIngredients] = useState([]);
    const [message, setMessage] = useState('');
    const [editingProduct, setEditingProduct] = useState(null);

    useEffect(() => {
        if (activeTab === 'orders') fetchOrders();
        if (activeTab === 'products_list' || activeTab === 'archive') fetchProducts();
        if (activeTab === 'categories_list') fetchCategories();
        if (activeTab === 'ingredients_list') fetchIngredients();
    }, [activeTab]);

    const fetchOrders = async () => {
        try { const res = await api.get('/orders'); setOrders(res.data); }
        catch (err) { setMessage('Ошибка загрузки заказов'); }
    };

    const fetchProducts = async () => {
        try {
            const res = await api.get('/products/all');
            setProducts(res.data);
        }
        catch (err) { setMessage('Ошибка загрузки товаров'); }
    };

    const fetchCategories = async () => {
        try { const res = await api.get('/categories'); setCategories(res.data); }
        catch (err) { setMessage('Ошибка загрузки категорий'); }
    };

    const fetchIngredients = async () => {
        try { const res = await api.get('/ingredients'); setIngredients(res.data); }
        catch (err) { setMessage('Ошибка загрузки ингредиентов'); }
    };

    const handleDeactivate = async (id) => {
        if (window.confirm('Перенести товар в архив?')) {
            try {
                await api.delete(`/products/${id}`);
                setMessage('Товар деактивирован');
                fetchProducts();
            } catch (err) { setMessage('Ошибка архивации'); }
        }
    };

    const handleRestore = async (id) => {
        try {
            await api.patch(`/products/${id}/restore`);
            setMessage('Товар успешно восстановлен');
            fetchProducts();
        } catch (err) { setMessage('Ошибка восстановления'); }
    };

    const handleDeletePermanent = async (endpoint, id, refreshFunc) => {
        if (window.confirm('Удалить безвозвратно?')) {
            try {
                await api.delete(`${endpoint}/${id}`);
                setMessage('Удалено навсегда');
                refreshFunc();
            } catch (err) { setMessage('Ошибка удаления'); }
        }
    };

    const activeProducts = products.filter(p => p.active !== false);
    const archivedProducts = products.filter(p => p.active === false);

    const archiveStyles = {
        table: tableStyle,
        th: thStyle,
        td: tdStyle,
        tr: trStyle,
        img: imgLarge,
        btn: actionBtn
    };

    return (
        <div style={adminContainer}>
            <h1 style={headerStyle}>Viosolocake Admin</h1>

            {message && <div style={msgStyle}>{message}</div>}

            <div style={tabBar}>
                <div style={tabGroup}>
                    <button onClick={() => setActiveTab('orders')} style={activeTab === 'orders' ? activeTabBtn : tabBtn}>Заказы</button>
                    <button onClick={() => setActiveTab('products_list')} style={activeTab === 'products_list' ? activeTabBtn : tabBtn}>Товары</button>
                    <button onClick={() => setActiveTab('archive')} style={activeTab === 'archive' ? activeTabBtn : tabBtn}>Архив</button>
                    <button onClick={() => setActiveTab('categories_list')} style={activeTab === 'categories_list' ? activeTabBtn : tabBtn}>Категории</button>
                    <button onClick={() => setActiveTab('ingredients_list')} style={activeTab === 'ingredients_list' ? activeTabBtn : tabBtn}>Ингредиенты</button>
                </div>
                <div style={tabGroup}>
                    <button onClick={() => setActiveTab('add_product')} style={activeTab === 'add_product' ? activeTabBtn : tabBtn}>+ Товар</button>
                    <button onClick={() => setActiveTab('add_category')} style={activeTab === 'add_category' ? activeTabBtn : tabBtn}>+ Категория</button>
                </div>
            </div>

            <div style={contentArea}>
                {activeTab === 'orders' && (
                    <table style={tableStyle}>
                        <thead>
                        <tr><th style={thStyle}>ID</th><th style={thStyle}>Дата</th><th style={thStyle}>Клиент</th><th style={thStyle}>Сумма</th><th style={thStyle}>Статус</th><th style={thStyle}></th></tr>
                        </thead>
                        <tbody>
                        {orders.map(o => (
                            <tr key={o.id} style={trStyle}>
                                <td style={tdStyle}>#{o.id}</td>
                                <td style={tdStyle}>{new Date(o.createdAt).toLocaleDateString()}</td>
                                <td style={tdStyle}>{o.userName || 'Гость'}</td>
                                <td style={{...tdStyle, fontWeight: '600'}}>{o.totalAmount} BYN</td>
                                <td style={tdStyle}><span style={badge}>{o.statusName}</span></td>
                                <td style={tdStyle}><button style={actionBtn}>УПРАВЛЯТЬ</button></td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}

                {activeTab === 'products_list' && (
                    <table style={tableStyle}>
                        <thead>
                        <tr><th style={thStyle}>Фото</th><th style={thStyle}>Название</th><th style={thStyle}>Цена</th><th style={thStyle}>Действия</th></tr>
                        </thead>
                        <tbody>
                        {activeProducts.map(p => (
                            <tr key={p.id} style={trStyle}>
                                <td style={tdStyle}><img src={p.imageUrl} alt="" style={imgLarge}/></td>
                                <td style={{...tdStyle, fontSize: '1.1rem'}}>{p.name}</td>
                                <td style={tdStyle}>{p.price} BYN</td>
                                <td style={tdStyle}>
                                    <button style={editBtn} onClick={() => setEditingProduct(p)}>ИЗМЕНИТЬ</button>
                                    <button style={delLink} onClick={() => handleDeactivate(p.id)}>В архив</button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}

                {activeTab === 'archive' && (
                    <ArchiveTable
                        products={archivedProducts}
                        onRestore={handleRestore}
                        styles={archiveStyles}
                    />
                )}

                {activeTab === 'categories_list' && (
                    <table style={tableStyle}>
                        <thead>
                        <tr><th style={thStyle}>Фото</th><th style={thStyle}>Категория</th><th style={thStyle}>Slug</th><th style={thStyle}></th></tr>
                        </thead>
                        <tbody>
                        {categories.map(c => (
                            <tr key={c.id} style={trStyle}>
                                <td style={tdStyle}><img src={c.imageUrl} alt="" style={imgLarge}/></td>
                                <td style={{...tdStyle, fontSize: '1.1rem'}}>{c.name}</td>
                                <td style={tdStyle}>{c.slug}</td>
                                <td style={tdStyle}><button style={delLink} onClick={() => handleDeletePermanent('/categories', c.id, fetchCategories)}>Удалить</button></td>
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

                {activeTab === 'add_product' && <ProductCreateForm onSuccess={setMessage} />}
                {activeTab === 'add_category' && <CategoryCreateForm onSuccess={setMessage} />}
            </div>

            {editingProduct && (
                <div style={overlay} onClick={() => setEditingProduct(null)}>
                    <div style={modal} onClick={e => e.stopPropagation()}>
                        <ProductEditForm
                            product={editingProduct}
                            categories={categories}
                            onCancel={() => setEditingProduct(null)}
                            onSave={(msg) => {
                                setMessage(msg);
                                setEditingProduct(null);
                                fetchProducts();
                            }}
                        />
                    </div>
                </div>
            )}

            <style>{`
                .ing-card { position: relative; transition: 0.3s; }
                .ing-card:hover { background: #fff !important; box-shadow: 0 10px 20px rgba(0,0,0,0.05); transform: translateY(-3px); }
                .ing-tooltip { 
                    visibility: hidden; opacity: 0; position: absolute; bottom: 100%; left: 0; 
                    background: #333; color: #fff; padding: 12px; borderRadius: 10px; 
                    width: 220px; font-size: 0.8rem; z-index: 10; transition: 0.3s;
                    margin-bottom: 10px; box-shadow: 0 5px 15px rgba(0,0,0,0.2);
                }
                .ing-card:hover .ing-tooltip { visibility: visible; opacity: 1; }
            `}</style>
        </div>
    );
};

const adminContainer = { padding: '60px 5%', maxWidth: '1400px', margin: '0 auto', fontFamily: 'Inter, sans-serif' };
const headerStyle = { fontWeight: '300', textTransform: 'uppercase', letterSpacing: '5px', textAlign: 'center', marginBottom: '60px' };
const tabBar = { display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid #eee', marginBottom: '40px' };
const tabGroup = { display: 'flex', gap: '5px' };
const tabBtn = { padding: '20px 25px', border: 'none', borderBottom: '3px solid transparent', background: 'none', cursor: 'pointer', fontSize: '0.9rem', color: '#aaa', transition: '0.3s' };
const activeTabBtn = { ...tabBtn, borderBottom: '3px solid #000', color: '#000', fontWeight: 'bold' };
const contentArea = { minHeight: '600px' };
const tableStyle = { width: '100%', borderCollapse: 'collapse' };
const thStyle = { textAlign: 'left', padding: '15px 20px', color: '#bbb', fontSize: '0.7rem', textTransform: 'uppercase', letterSpacing: '1px', borderBottom: '2px solid #f0f0f0' };
const tdStyle = { padding: '20px', borderBottom: '1px solid #f9f9f9' };
const trStyle = { transition: 'background 0.2s' };
const imgLarge = { width: '80px', height: '80px', objectFit: 'cover', borderRadius: '12px' };
const badge = { background: '#f5f5f5', padding: '6px 15px', borderRadius: '50px', fontSize: '0.8rem' };
const actionBtn = { padding: '10px 20px', background: '#000', color: '#fff', border: 'none', borderRadius: '5px', cursor: 'pointer' };
const editBtn = { padding: '8px 16px', background: '#fff', border: '1px solid #000', borderRadius: '5px', cursor: 'pointer', fontSize: '0.75rem', marginRight: '10px' };
const delLink = { background: 'none', border: 'none', color: '#ff4d4f', cursor: 'pointer', fontSize: '0.75rem', textDecoration: 'underline' };
const gridContainer = { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '20px' };
const ingCard = { padding: '20px', background: '#fafafa', borderRadius: '15px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', border: '1px solid #eee' };
const ingInfo = { display: 'flex', flexDirection: 'column' };
const ingName = { fontWeight: 'bold', fontSize: '1rem' };
const tooltipStyle = { lineHeight: '1.4' };
const delSmall = { background: 'none', border: 'none', color: '#ff4d4f', fontSize: '1.5rem', cursor: 'pointer' };
const overlay = { position: 'fixed', top: 0, left: 0, width: '100%', height: '100%', background: 'rgba(0,0,0,0.3)', backdropFilter: 'blur(5px)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 3000 };
const modal = { background: '#fff', borderRadius: '25px', width: '1000px', maxWidth: '95vw', boxShadow: '0 25px 50px rgba(0,0,0,0.15)', overflow: 'hidden' };
const msgStyle = { padding: '15px', background: '#000', color: '#fff', borderRadius: '10px', marginBottom: '30px', textAlign: 'center', fontSize: '0.9rem' };

export default AdminDashboard;