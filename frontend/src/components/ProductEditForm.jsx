import React, { useState, useEffect } from 'react';
import api from '../api';
import Swal from 'sweetalert2';

const ProductEditForm = ({ product, onSave, onCancel }) => {
    const [formData, setFormData] = useState({
        ...product,
        nutrition: {
            weight: product?.nutrition?.weight || product?.weight || '',
            calories: product?.nutrition?.calories || product?.calories || ''
        }
    });

    const [allIngredients, setAllIngredients] = useState([]);
    const [allCategories, setAllCategories] = useState([]);
    const [loading, setLoading] = useState(false);
    const [isDataLoaded, setIsDataLoaded] = useState(false);

    const getImageUrl = (url) => {
        if (!url) return 'https://via.placeholder.com/350?text=No+Image';
        if (url.startsWith('http')) return url;
        return url;
    };

    useEffect(() => {
        const loadInitialData = async () => {
            try {
                const [ingRes, catRes] = await Promise.all([
                    api.get('/ingredients'),
                    api.get('/categories')
                ]);

                setAllIngredients(ingRes.data);
                setAllCategories(catRes.data);

                if (product) {
                    const matchedIngredientIds = product.ingredients
                        ? ingRes.data
                            .filter(ing => product.ingredients.includes(ing.name))
                            .map(ing => ing.id)
                        : [];

                    const foundCategory = catRes.data.find(c =>
                        c.name?.toLowerCase() === product.categoryName?.toLowerCase()
                    );

                    setFormData({
                        ...product,
                        categoryId: foundCategory ? foundCategory.id : (product.categoryId || ''),
                        ingredientIds: matchedIngredientIds,
                        nutrition: {
                            weight: product.nutrition?.weight || product.weight || '',
                            calories: product.nutrition?.calories || product.calories || ''
                        }
                    });
                    setIsDataLoaded(true);
                }
            } catch (err) {
                console.error(err);
                setIsDataLoaded(true);
            }
        };
        loadInitialData();
    }, [product]);

    const handleIngredientChange = (id) => {
        const updatedIds = formData.ingredientIds.includes(id)
            ? formData.ingredientIds.filter(itemId => itemId !== id)
            : [...formData.ingredientIds, id];
        setFormData({ ...formData, ingredientIds: updatedIds });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const payload = {
                ...formData,
                price: parseFloat(formData.price),
                categoryId: parseInt(formData.categoryId),
                stockQuantity: parseInt(formData.stockQuantity) || 0,
                ingredientIds: formData.ingredientIds.map(id => parseInt(id)),
                nutrition: {
                    weight: parseInt(formData.nutrition.weight) || 0,
                    calories: parseInt(formData.nutrition.calories) || 0
                }
            };
            await api.put(`/products/${product.id}`, payload);
            Swal.fire({ title: 'Успешно', icon: 'success', timer: 1500, showConfirmButton: false });
            onSave();
        } catch (err) {
            Swal.fire('Ошибка', 'Не удалось сохранить', 'error');
        } finally {
            setLoading(false);
        }
    };

    if (!isDataLoaded) return <div style={{ padding: '50px', textAlign: 'center' }}>Загрузка...</div>;

    return (
        <div style={formCard}>
            <header style={formHeader}>
                <h2 style={formTitle}>Редактирование: {product.name?.toUpperCase()}</h2>
                <button onClick={onCancel} style={closeBtn}>×</button>
            </header>

            <div style={mainLayout}>
                <div style={leftCol}>
                    <label style={label}>Превью изделия</label>
                    <div style={imageContainer}>
                        <img
                            src={getImageUrl(formData.imageUrl)}
                            alt="Превью"
                            style={previewImg}
                            key={formData.imageUrl}
                            onError={(e) => {
                                e.target.onerror = null;
                                e.target.src = 'https://via.placeholder.com/350?text=Not+Found';
                            }}
                        />
                    </div>
                    <div style={{ marginTop: '25px' }}>
                        <label style={label}>Путь к изображению</label>
                        <input
                            style={input}
                            value={formData.imageUrl || ''}
                            onChange={e => setFormData({ ...formData, imageUrl: e.target.value })}
                        />
                    </div>
                </div>

                <form style={rightCol} onSubmit={handleSubmit}>
                    <div style={row}>
                        <div style={{...col, flex: 2}}>
                            <label style={label}>Название</label>
                            <input style={input} required value={formData.name || ''} onChange={e => setFormData({ ...formData, name: e.target.value })} />
                        </div>
                        <div style={col}>
                            <label style={label}>Вкус</label>
                            <input style={input} required value={formData.flavor || ''} onChange={e => setFormData({ ...formData, flavor: e.target.value })} />
                        </div>
                    </div>

                    <div style={row}>
                        <div style={col}>
                            <label style={label}>Цена (BYN)</label>
                            <input style={input} type="number" step="0.01" required value={formData.price || ''} onChange={e => setFormData({ ...formData, price: e.target.value })} />
                        </div>
                        <div style={col}>
                            <label style={label}>Склад (шт)</label>
                            <input style={input} type="number" required value={formData.stockQuantity || ''} onChange={e => setFormData({ ...formData, stockQuantity: e.target.value })} />
                        </div>
                    </div>

                    <div style={nutritionBox}>
                        <div style={col}>
                            <label style={label}>Вес (г)</label>
                            <input style={input} type="number" value={formData.nutrition.weight} onChange={e => setFormData({ ...formData, nutrition: { ...formData.nutrition, weight: e.target.value } })} />
                        </div>
                        <div style={col}>
                            <label style={label}>Калории (ккал)</label>
                            <input style={input} type="number" value={formData.nutrition.calories} onChange={e => setFormData({ ...formData, nutrition: { ...formData.nutrition, calories: e.target.value } })} />
                        </div>
                    </div>

                    <div style={inputGroup}>
                        <label style={label}>Категория</label>
                        <select
                            style={input}
                            required
                            value={formData.categoryId || ''}
                            onChange={e => setFormData({ ...formData, categoryId: e.target.value })}
                        >
                            <option value="" disabled>Выберите категорию</option>
                            {allCategories.map(c => (
                                <option key={c.id} value={c.id}>{c.name}</option>
                            ))}
                        </select>
                    </div>

                    <div style={inputGroup}>
                        <label style={label}>Ингредиенты</label>
                        <div style={ingredientsGrid}>
                            {allIngredients.map(ing => (
                                <label key={ing.id} style={ingCheckLabel}>
                                    <input
                                        type="checkbox"
                                        checked={formData.ingredientIds.includes(ing.id)}
                                        onChange={() => handleIngredientChange(ing.id)}
                                        style={checkboxStyle}
                                    />
                                    {ing.name}
                                </label>
                            ))}
                        </div>
                    </div>

                    <div style={inputGroup}>
                        <label style={label}>Описание</label>
                        <textarea
                            style={textarea}
                            value={formData.description || ''}
                            onChange={e => setFormData({ ...formData, description: e.target.value })}
                        />
                    </div>

                    <div style={btnRow}>
                        <button type="submit" disabled={loading} style={saveBtn}>
                            {loading ? 'СОХРАНЕНИЕ...' : 'СОХРАНИТЬ ИЗМЕНЕНИЯ'}
                        </button>
                        <button type="button" onClick={onCancel} style={cancelBtn}>ОТМЕНА</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

const formCard = { padding: '50px', width: '100%', boxSizing: 'border-box', background: '#fff' };
const formHeader = { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '40px' };
const formTitle = { fontWeight: '300', textTransform: 'uppercase', letterSpacing: '4px', margin: 0, fontSize: '1.8rem' };
const closeBtn = { background: 'none', border: 'none', fontSize: '2.5rem', cursor: 'pointer', color: '#ddd' };
const mainLayout = { display: 'flex', gap: '60px' };
const leftCol = { width: '350px', flexShrink: 0 };
const rightCol = { flex: 1, display: 'flex', flexDirection: 'column', gap: '20px' };
const imageContainer = { width: '350px', height: '350px', borderRadius: '30px', overflow: 'hidden', border: '1px solid #f0f0f0', background: '#fafafa' };
const previewImg = { width: '100%', height: '100%', objectFit: 'cover' };
const row = { display: 'flex', gap: '20px' };
const col = { flex: 1 };
const inputGroup = { display: 'flex', flexDirection: 'column', gap: '8px' };
const nutritionBox = { ...row, padding: '20px', background: '#fffcf5', borderRadius: '20px', border: '1px solid #fdf0d5' };
const label = { fontSize: '0.75rem', color: '#999', textTransform: 'uppercase', fontWeight: '700', letterSpacing: '1px' };
const input = { width: '100%', padding: '15px 20px', borderRadius: '12px', border: '1px solid #eee', fontSize: '1rem', outline: 'none', background: '#fff', boxSizing: 'border-box' };
const textarea = { ...input, minHeight: '100px', resize: 'none' };
const btnRow = { display: 'flex', gap: '20px', marginTop: '20px' };
const saveBtn = { flex: 2, padding: '20px', background: '#000', color: '#fff', border: 'none', borderRadius: '15px', cursor: 'pointer', fontWeight: '700', fontSize: '1rem' };
const cancelBtn = { flex: 1, padding: '20px', background: '#fff', color: '#000', border: '2px solid #000', borderRadius: '15px', cursor: 'pointer', fontWeight: '700', fontSize: '1rem' };
const ingredientsGrid = { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))', gap: '12px', padding: '15px', background: '#f9f9f9', borderRadius: '12px', maxHeight: '160px', overflowY: 'auto', border: '1px solid #eee' };
const ingCheckLabel = { display: 'flex', alignItems: 'center', gap: '10px', fontSize: '0.9rem', cursor: 'pointer', color: '#444' };
const checkboxStyle = { width: '18px', height: '18px', accentColor: '#000', cursor: 'pointer' };

export default ProductEditForm;