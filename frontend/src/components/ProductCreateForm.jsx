import React, { useState, useEffect } from 'react';
import api from '../api';

const ProductCreateForm = ({ onSuccess }) => {
    const [categories, setCategories] = useState([]);
    const [ingredients, setIngredients] = useState([]);
    const [loading, setLoading] = useState(false);

    const [quickIngredient, setQuickIngredient] = useState('');

    const [newProduct, setNewProduct] = useState({
        name: '', flavor: '', description: '', price: '',
        categoryId: '', imageUrl: '', stockQuantity: 0,
        ingredientIds: [],
        nutrition: { weight: '', calories: '' }
    });

    useEffect(() => {
        loadMetadata();
    }, []);

    const loadMetadata = async () => {
        try {
            const [catRes, ingRes] = await Promise.all([
                api.get('/categories'),
                api.get('/ingredients')
            ]);
            setCategories(catRes.data);
            setIngredients(ingRes.data);
        } catch (err) {
            console.error("Ошибка загрузки данных", err);
        }
    };

    const handleAddQuickIngredient = async () => {
        if (!quickIngredient.trim()) return;
        try {

            const response = await api.post('/ingredients', {
                name: quickIngredient,
                description: 'Добавлено через форму товара'
            });
            setIngredients([...ingredients, response.data]);
            setNewProduct(prev => ({
                ...prev,
                ingredientIds: [...prev.ingredientIds, response.data.id]
            }));
            setQuickIngredient('');
        } catch (err) {
            alert('Не удалось создать ингредиент');
        }
    };

    const handleIngredientChange = (id) => {
        setNewProduct(prev => {
            const ids = prev.ingredientIds.includes(id)
                ? prev.ingredientIds.filter(i => i !== id)
                : [...prev.ingredientIds, id];
            return { ...prev, ingredientIds: ids };
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            await api.post('/products', newProduct);
            onSuccess('Изделие успешно добавлено в каталог');
            setNewProduct({
                name: '', flavor: '', description: '', price: '',
                categoryId: '', imageUrl: '', stockQuantity: 0,
                ingredientIds: [], nutrition: { weight: '', calories: '' }
            });
        } catch (err) {
            alert('Ошибка: ' + err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} style={fContainer}>
            <header style={fHeader}>
                <h2 style={fTitle}>Регистрация нового изделия</h2>
                <div style={accentLine}></div>
            </header>

            <div style={fGrid}>

                <div style={fColumn}>
                    <div style={card}>
                        <label style={lStyle}>Название и визуализация</label>
                        <input style={iStyle} placeholder="Название (напр. Зефир Маракуйя)" value={newProduct.name}
                               onChange={e => setNewProduct({...newProduct, name: e.target.value})} required />
                        <div style={innerRow}>
                            <input style={iStyle} placeholder="Вкус" value={newProduct.flavor}
                                   onChange={e => setNewProduct({...newProduct, flavor: e.target.value})} required />
                            <input style={iStyle} placeholder="Имя файла фото" value={newProduct.imageUrl}
                                   onChange={e => setNewProduct({...newProduct, imageUrl: e.target.value})} />
                        </div>
                        <textarea style={{...iStyle, minHeight: '100px', resize: 'none'}} placeholder="Описание состава и декора..."
                                  value={newProduct.description} onChange={e => setNewProduct({...newProduct, description: e.target.value})} required />
                    </div>

                    <div style={card}>
                        <label style={lStyle}>Параметры продажи</label>
                        <div style={innerRow}>
                            <div style={inputGroup}>
                                <small style={sLabel}>Цена (BYN)</small>
                                <input style={iStyle} type="number" step="0.01" value={newProduct.price}
                                       onChange={e => setNewProduct({...newProduct, price: e.target.value})} required />
                            </div>
                            <div style={inputGroup}>
                                <small style={sLabel}>На складе (шт)</small>
                                <input style={iStyle} type="number" value={newProduct.stockQuantity}
                                       onChange={e => setNewProduct({...newProduct, stockQuantity: e.target.value})} required />
                            </div>
                        </div>
                    </div>
                </div>

                <div style={fColumn}>
                    <div style={card}>
                        <label style={lStyle}>Категория и КБЖУ</label>
                        <select style={{...iStyle, marginBottom: '15px'}} value={newProduct.categoryId}
                                onChange={e => setNewProduct({...newProduct, categoryId: e.target.value})} required>
                            <option value="">Выберите категорию</option>
                            {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                        </select>
                        <div style={innerRow}>
                            <div style={inputGroup}>
                                <small style={sLabel}>Вес (г)</small>
                                <input style={iStyle} type="number" value={newProduct.nutrition.weight}
                                       onChange={e => setNewProduct({...newProduct, nutrition: {...newProduct.nutrition, weight: e.target.value}})} required />
                            </div>
                            <div style={inputGroup}>
                                <small style={sLabel}>Ккал</small>
                                <input style={iStyle} type="number" value={newProduct.nutrition.calories}
                                       onChange={e => setNewProduct({...newProduct, nutrition: {...newProduct.nutrition, calories: e.target.value}})} required />
                            </div>
                        </div>
                    </div>

                    <div style={card}>
                        <label style={lStyle}>Состав ингредиентов</label>

                        {/* Поле быстрого создания ингредиента */}
                        <div style={quickAddRow}>
                            <input style={{...iStyle, flex: 1, padding: '8px 12px'}}
                                   placeholder="Новый ингредиент..."
                                   value={quickIngredient}
                                   onChange={e => setQuickIngredient(e.target.value)} />
                            <button type="button" onClick={handleAddQuickIngredient} style={addBtn}>+</button>
                        </div>

                        <div style={scrollArea}>
                            {ingredients.map(ing => (
                                <label key={ing.id} style={checkItem}>
                                    <input type="checkbox" checked={newProduct.ingredientIds.includes(ing.id)}
                                           onChange={() => handleIngredientChange(ing.id)} />
                                    <span style={{marginLeft: '10px'}}>{ing.name}</span>
                                </label>
                            ))}
                        </div>
                    </div>
                </div>
            </div>

            <button type="submit" disabled={loading} style={mainBtn}>
                {loading ? 'СОХРАНЕНИЕ...' : 'ОПУБЛИКОВАТЬ В МАГАЗИНЕ'}
            </button>
        </form>
    );
};

const fContainer = { width: '100%', maxWidth: '1100px', margin: '0 auto', display: 'flex', flexDirection: 'column', gap: '25px' };
const fHeader = { marginBottom: '10px' };
const fTitle = { fontWeight: '300', textTransform: 'uppercase', letterSpacing: '3px', fontSize: '1.4rem', margin: 0 };
const accentLine = { width: '50px', height: '2px', background: '#000', marginTop: '10px' };
const fGrid = { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '30px' };
const fColumn = { display: 'flex', flexDirection: 'column', gap: '25px' };
const card = { background: '#fff', padding: '25px', borderRadius: '12px', boxShadow: '0 4px 20px rgba(0,0,0,0.05)', border: '1px solid #f0f0f0' };
const lStyle = { display: 'block', fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '1px', color: '#999', marginBottom: '15px', fontWeight: '600' };
const iStyle = { padding: '14px', border: '1px solid #e0e0e0', borderRadius: '8px', fontSize: '0.95rem', width: '100%', boxSizing: 'border-box', background: '#fafafa', outline: 'none' };
const innerRow = { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' };
const inputGroup = { display: 'flex', flexDirection: 'column', gap: '6px' };
const sLabel = { fontSize: '0.7rem', color: '#bbb', marginLeft: '4px' };
const quickAddRow = { display: 'flex', gap: '10px', marginBottom: '15px', paddingBottom: '15px', borderBottom: '1px solid #f5f5f5' };
const addBtn = { width: '45px', height: '42px', background: '#000', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '1.2rem' };
const scrollArea = { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px', maxHeight: '180px', overflowY: 'auto' };
const checkItem = { display: 'flex', alignItems: 'center', cursor: 'pointer', fontSize: '0.9rem', padding: '8px', borderRadius: '6px', transition: 'background 0.2s' };
const mainBtn = { padding: '20px', background: '#000', color: '#fff', border: 'none', borderRadius: '50px', cursor: 'pointer', fontWeight: 'bold', fontSize: '1rem', letterSpacing: '2px', boxShadow: '0 10px 20px rgba(0,0,0,0.1)' };

export default ProductCreateForm;