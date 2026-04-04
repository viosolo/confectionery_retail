import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import FeaturedCard from '../components/FeaturedCard';
import api from '../api';

export default function CategoryPage() {
    const { slug } = useParams();
    const [categoryData, setCategoryData] = useState(null);

    // Состояния для фильтрации на клиенте или отправки в API
    const [maxPrice, setMaxPrice] = useState('');
    const [selectedFlavors, setSelectedFlavors] = useState([]);
    const [sortOrder, setSortOrder] = useState('asc');

    useEffect(() => {
        // Оставляем твой рабочий запрос к контроллеру
        api.get('/categories/products/search', {
            params: {
                slug: slug,
                maxPrice: maxPrice || null,
                flavors: selectedFlavors.length > 0 ? selectedFlavors.join(',') : null,
                sort: `price,${sortOrder}`
            }
        })
            .then(res => setCategoryData(res.data))
            .catch(err => console.error(err));
    }, [slug, maxPrice, selectedFlavors, sortOrder]);

    // --- МАГИЯ ФРОНТЕНДА: Получаем уникальные вкусы из пришедших данных ---
    const availableFlavors = categoryData?.products?.content
        ? [...new Set(categoryData.products.content.map(p => p.flavor).filter(Boolean))]
        : [];

    const handleFlavorChange = (flavor) => {
        setSelectedFlavors(prev =>
            prev.includes(flavor) ? prev.filter(f => f !== flavor) : [...prev, flavor]
        );
    };

    if (!categoryData) return <div style={{padding: '100px', textAlign: 'center'}}>Загрузка...</div>;

    return (
        <div style={{ display: 'flex', padding: '40px 80px', gap: '40px' }}>

            {/* БОКОВАЯ ПАНЕЛЬ (SIDEBAR) */}
            <aside style={{ width: '250px', flexShrink: 0, borderRight: '1px solid #eee', paddingRight: '20px' }}>
                <h3 style={{ marginBottom: '20px' }}>Фильтры</h3>

                {/* Сортировка */}
                <div style={{ marginBottom: '30px' }}>
                    <p><strong>Цена:</strong></p>
                    <select value={sortOrder} onChange={(e) => setSortOrder(e.target.value)} style={{ width: '100%', padding: '5px' }}>
                        <option value="asc">Сначала дешевле</option>
                        <option value="desc">Сначала дороже</option>
                    </select>
                </div>

                {/* Максимальная цена */}
                <div style={{ marginBottom: '30px' }}>
                    <p><strong>Макс. цена (BYN):</strong></p>
                    <input
                        type="number"
                        value={maxPrice}
                        onChange={(e) => setMaxPrice(e.target.value)}
                        style={{ width: '100%', padding: '5px' }}
                        placeholder="Напр. 5"
                    />
                </div>

                {/* Галочки вкусов (Динамически из данных) */}
                <div style={{ marginBottom: '30px' }}>
                    <p><strong>Вкус:</strong></p>
                    {availableFlavors.length > 0 ? availableFlavors.map(flavor => (
                        <div key={flavor} style={{ marginBottom: '8px' }}>
                            <label style={{ display: 'flex', alignItems: 'center', gap: '10px', cursor: 'pointer' }}>
                                <input
                                    type="checkbox"
                                    checked={selectedFlavors.includes(flavor)}
                                    onChange={() => handleFlavorChange(flavor)}
                                />
                                {flavor}
                            </label>
                        </div>
                    )) : <small>Вкусы не найдены</small>}
                </div>
            </aside>

            {/* ОСНОВНАЯ ЧАСТЬ С ТОВАРАМИ */}
            <main style={{ flexGrow: 1 }}>
                <h1 style={{ marginBottom: '30px', fontFamily: 'var(--title-font)' }}>{categoryData.name}</h1>

                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '25px' }}>
                    {categoryData.products?.content.map(product => (
                        <FeaturedCard
                            key={product.id}
                            id={product.id}
                            name={product.name}
                            image={product.imageUrl}
                            price={product.price}
                        />
                    ))}
                </div>
            </main>
        </div>
    );
}