import React, { useState, useEffect } from 'react';
import api from '../api';

const ProductEditForm = ({ product, categories = [], onSave, onCancel }) => {
    const [formData, setFormData] = useState({ ...product });
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        setFormData({ ...product });
    }, [product]);

    const handleSubmit = async (e, method) => {
        e.preventDefault();
        setLoading(true);
        try {
            const url = `/products/${product.id}`;
            method === 'PATCH' ? await api.patch(url, formData) : await api.put(url, formData);
            onSave('Товар успешно обновлен!');
        } catch (err) {
            alert('Ошибка: ' + (err.response?.data?.userMessage || err.message));
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={formCard}>
            <header style={formHeader}>
                <h3 style={formTitle}>Редактирование: {product.name}</h3>
                <button onClick={onCancel} style={closeBtn}>×</button>
            </header>

            <div style={mainLayout}>
                <div style={leftCol}>
                    <label style={label}>Превью изделия</label>
                    <div style={imageContainer}>
                        <img src={formData.imageUrl || ''} alt="Превью" style={previewImg} />
                    </div>
                    <div style={{marginTop: '20px'}}>
                        <label style={label}>Ссылка на изображение</label>
                        <input
                            style={input}
                            value={formData.imageUrl || ''}
                            onChange={e => setFormData({ ...formData, imageUrl: e.target.value })}
                        />
                    </div>
                </div>

                <form style={rightCol}>
                    <div style={inputGroup}>
                        <label style={label}>Название</label>
                        <input style={input} value={formData.name || ''} onChange={e => setFormData({ ...formData, name: e.target.value })} />
                    </div>

                    <div style={row}>
                        <div style={col}>
                            <label style={label}>Цена (BYN)</label>
                            <input style={input} type="number" value={formData.price || ''} onChange={e => setFormData({ ...formData, price: e.target.value })} />
                        </div>
                        <div style={col}>
                            <label style={label}>Склад (шт)</label>
                            <input style={input} type="number" value={formData.stockQuantity || ''} onChange={e => setFormData({ ...formData, stockQuantity: e.target.value })} />
                        </div>
                    </div>

                    <div style={nutritionBox}>
                        <div style={col}>
                            <label style={label}>Вес (г)</label>
                            <input style={input} type="number" value={formData.weight || ''} onChange={e => setFormData({ ...formData, weight: e.target.value })} />
                        </div>
                        <div style={col}>
                            <label style={label}>Калории (ккал)</label>
                            <input style={input} type="number" value={formData.calories || ''} onChange={e => setFormData({ ...formData, calories: e.target.value })} />
                        </div>
                    </div>

                    <div style={inputGroup}>
                        <label style={label}>Категория</label>
                        <select style={input} value={formData.categoryId || ''} onChange={e => setFormData({...formData, categoryId: e.target.value})}>
                            <option value="">Выберите категорию</option>
                            {categories?.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                        </select>
                    </div>

                    <div style={inputGroup}>
                        <label style={label}>Описание</label>
                        <textarea style={textarea} value={formData.description || ''} onChange={e => setFormData({ ...formData, description: e.target.value })} />
                    </div>

                    <div style={btnRow}>
                        <button type="button" onClick={(e) => handleSubmit(e, 'PATCH')} style={patchBtn}>PATCH</button>
                        <button type="button" onClick={(e) => handleSubmit(e, 'PUT')} style={putBtn}>PUT</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

// СТИЛИ: Добавлены ВСЕ переменные, чтобы не было ReferenceError
const formCard = { padding: '40px', width: '100%', boxSizing: 'border-box' };
const formHeader = { display: 'flex', justifyContent: 'space-between', marginBottom: '30px' };
const formTitle = { fontWeight: '300', textTransform: 'uppercase', letterSpacing: '2px', margin: 0 };
const closeBtn = { background: 'none', border: 'none', fontSize: '2rem', cursor: 'pointer', color: '#ccc' };
const mainLayout = { display: 'flex', gap: '40px' };
const leftCol = { width: '300px', flexShrink: 0 };
const rightCol = { flex: 1, display: 'flex', flexDirection: 'column', gap: '15px' };
const imageContainer = { width: '300px', height: '300px', borderRadius: '20px', overflow: 'hidden', border: '1px solid #eee' };
const previewImg = { width: '100%', height: '100%', objectFit: 'cover' };
const row = { display: 'flex', gap: '15px' };
const col = { flex: 1 };
const inputGroup = { display: 'flex', flexDirection: 'column', gap: '5px' }; // ЭТОГО НЕ ХВАТАЛО
const nutritionBox = { ...row, padding: '15px', background: '#fffcf5', borderRadius: '15px', border: '1px solid #fdf0d5' };
const label = { fontSize: '0.65rem', color: '#bbb', textTransform: 'uppercase', fontWeight: 'bold', marginBottom: '5px', display: 'block' };
const input = { width: '100%', padding: '12px', borderRadius: '10px', border: '1px solid #eee', boxSizing: 'border-box', outline: 'none' };
const textarea = { ...input, minHeight: '80px', resize: 'none' };
const btnRow = { display: 'flex', gap: '15px', marginTop: '10px' };
const patchBtn = { flex: 1, padding: '15px', background: '#000', color: '#fff', border: 'none', borderRadius: '50px', cursor: 'pointer', fontWeight: 'bold' };
const putBtn = { flex: 1, padding: '15px', background: '#fff', color: '#000', border: '1px solid #000', borderRadius: '50px', cursor: 'pointer', fontWeight: 'bold' };

export default ProductEditForm;