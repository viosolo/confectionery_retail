import React, { useState } from 'react';
import api from '../api';

const CategoryCreateForm = ({ onSuccess }) => {
    const [loading, setLoading] = useState(false);
    const [newCategory, setNewCategory] = useState({
        name: '',
        slug: '',
        description: '',
        imageUrl: ''
    });

    const transliterate = (text) => {
        const rus = {
            'а': 'a', 'б': 'b', 'в': 'v', 'г': 'g', 'д': 'd', 'е': 'e', 'ё': 'yo', 'ж': 'zh', 'з': 'z',
            'и': 'i', 'й': 'y', 'к': 'k', 'л': 'l', 'м': 'm', 'н': 'n', 'о': 'o', 'п': 'p', 'р': 'r',
            'с': 's', 'т': 't', 'у': 'u', 'ф': 'f', 'х': 'h', 'ц': 'c', 'ч': 'ch', 'ш': 'sh', 'щ': 'shch',
            'ъ': '', 'ы': 'y', 'ь': '', 'э': 'e', 'ю': 'yu', 'я': 'ya'
        };
        return text.split('').map(char => rus[char.toLowerCase()] || char).join('');
    };

    const generateSlug = (text) => {
        return transliterate(text)
            .toLowerCase()
            .trim()
            .replace(/\s+/g, '-')
            .replace(/[^\w-]+/g, '')
            .replace(/--+/g, '-');
    };

    const handleNameChange = (e) => {
        const name = e.target.value;
        setNewCategory({
            ...newCategory,
            name: name,
            slug: generateSlug(name)
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            await api.post('/categories', newCategory);
            onSuccess('Категория успешно создана!');
            setNewCategory({ name: '', slug: '', description: '', imageUrl: '' });
        } catch (err) {
            const errorMsg = err.response?.data?.devMessage || err.message;
            alert('Ошибка: ' + errorMsg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={container}>
            <header style={header}>
                <h2 style={title}>Новая категория изделий</h2>
                <div style={accent}></div>
            </header>

            <form onSubmit={handleSubmit} style={card}>
                <div style={inputGroup}>
                    <label style={label}>Название категории</label>
                    <input
                        style={input}
                        value={newCategory.name}
                        onChange={handleNameChange}
                        required
                    />
                </div>

                <div style={inputGroup}>
                    <label style={label}>URL Slug</label>
                    <input
                        style={{...input, background: '#f9f9f9'}}
                        value={newCategory.slug}
                        onChange={e => setNewCategory({...newCategory, slug: e.target.value})}
                        required
                    />
                </div>

                <div style={inputGroup}>
                    <label style={label}>Визуализация (имя файла)</label>
                    <input
                        style={input}
                        value={newCategory.imageUrl}
                        onChange={e => setNewCategory({...newCategory, imageUrl: e.target.value})}
                    />
                </div>

                <div style={inputGroup}>
                    <label style={label}>Описание</label>
                    <textarea
                        style={{...input, minHeight: '100px', resize: 'none'}}
                        value={newCategory.description}
                        onChange={e => setNewCategory({...newCategory, description: e.target.value})}
                        required
                    />
                </div>

                <button type="submit" disabled={loading} style={btn}>
                    {loading ? 'СОХРАНЕНИЕ...' : 'ДОБАВИТЬ В КАТАЛОГ'}
                </button>
            </form>
        </div>
    );
};

const container = { width: '100%', maxWidth: '600px' };
const header = { marginBottom: '25px' };
const title = { fontWeight: '300', textTransform: 'uppercase', letterSpacing: '2px', fontSize: '1.2rem' };
const accent = { width: '40px', height: '2px', background: '#000', marginTop: '8px' };
const card = { background: '#fff', padding: '30px', borderRadius: '12px', boxShadow: '0 4px 20px rgba(0,0,0,0.05)', border: '1px solid #f0f0f0', display: 'flex', flexDirection: 'column', gap: '20px' };
const inputGroup = { display: 'flex', flexDirection: 'column', gap: '8px' };
const label = { fontSize: '0.7rem', textTransform: 'uppercase', color: '#999', letterSpacing: '1px', fontWeight: '600' };
const input = { padding: '12px', border: '1px solid #e0e0e0', borderRadius: '8px', fontSize: '0.95rem', background: '#fafafa', outline: 'none' };
const btn = { padding: '18px', background: '#000', color: '#fff', border: 'none', borderRadius: '50px', cursor: 'pointer', fontWeight: 'bold', fontSize: '0.9rem', letterSpacing: '1px', marginTop: '10px' };

export default CategoryCreateForm;