import React, { useEffect, useState, useRef } from 'react';
import { useParams } from 'react-router-dom';
import FeaturedCard from '../components/FeaturedCard';
import api from '../api';

export default function CategoryPage() {
    const { slug } = useParams();
    const [categoryData, setCategoryData] = useState(null);
    const [maxPrice, setMaxPrice] = useState('');
    const [selectedFlavors, setSelectedFlavors] = useState([]);
    const [sortOrder, setSortOrder] = useState('asc');

    const allFlavorsRef = useRef([]);

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const res = await api.get('/categories/products/search', {
                    params: {
                        slug: slug,
                        maxPrice: maxPrice || null,
                        flavors: selectedFlavors.length > 0 ? selectedFlavors.join(',') : null,
                        sort: `price,${sortOrder}`
                    }
                });

                if (allFlavorsRef.current.length === 0 || !categoryData || categoryData.slug !== slug) {
                    const flavors = [...new Set(res.data.products.content.map(p => p.flavor).filter(Boolean))];
                    allFlavorsRef.current = flavors;
                }

                setCategoryData(res.data);
            } catch (err) {
                console.error(err);
            }
        };

        fetchProducts();
    }, [slug, maxPrice, selectedFlavors, sortOrder]);

    useEffect(() => {
        allFlavorsRef.current = [];
        setSelectedFlavors([]);
        setMaxPrice('');
    }, [slug]);

    const handleFlavorChange = (flavor) => {
        setSelectedFlavors(prev =>
            prev.includes(flavor) ? prev.filter(f => f !== flavor) : [...prev, flavor]
        );
    };

    if (!categoryData) return <div style={{ padding: '100px', textAlign: 'center' }}>Загрузка...</div>;

    return (
        <div style={pageContainerStyle}>
            <aside style={sidebarStyle}>
                <h3 style={filterTitleStyle}>Фильтры</h3>

                <div style={filterGroupStyle}>
                    <p style={labelStyle}>Сортировка</p>
                    <select value={sortOrder} onChange={(e) => setSortOrder(e.target.value)} style={inputStyle}>
                        <option value="asc">Сначала дешевле</option>
                        <option value="desc">Сначала дороже</option>
                    </select>
                </div>

                <div style={filterGroupStyle}>
                    <p style={labelStyle}>Макс. цена (BYN)</p>
                    <input
                        type="number"
                        value={maxPrice}
                        onChange={(e) => setMaxPrice(e.target.value)}
                        style={inputStyle}
                        placeholder="Напр. 50"
                    />
                </div>

                <div style={filterGroupStyle}>
                    <p style={labelStyle}>Вкус</p>
                    <div style={checkboxContainerStyle}>
                        {allFlavorsRef.current.length > 0 ? allFlavorsRef.current.map(flavor => (
                            <label key={flavor} style={checkboxLabelStyle}>
                                <input
                                    type="checkbox"
                                    checked={selectedFlavors.includes(flavor)}
                                    onChange={() => handleFlavorChange(flavor)}
                                    style={checkboxStyle}
                                />
                                {flavor}
                            </label>
                        )) : <small style={{ color: '#999' }}>Вкусы не найдены</small>}
                    </div>
                </div>
            </aside>

            <main style={{ flexGrow: 1 }}>
                <h1 style={titleStyle}>{categoryData.name}</h1>
                <div style={gridStyle}>
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

const pageContainerStyle = { display: 'flex', padding: '40px 80px', gap: '40px', background: '#fff', minHeight: '100vh' };
const sidebarStyle = { width: '280px', flexShrink: 0, paddingRight: '30px' };
const filterTitleStyle = { fontSize: '1.2rem', fontWeight: '500', marginBottom: '30px', letterSpacing: '1px', textTransform: 'uppercase' };
const filterGroupStyle = { marginBottom: '35px' };
const labelStyle = { fontSize: '0.85rem', color: '#888', textTransform: 'uppercase', marginBottom: '12px', letterSpacing: '1px' };
const inputStyle = { width: '100%', padding: '12px', borderRadius: '10px', border: '1px solid #eee', outline: 'none', fontFamily: 'inherit' };
const checkboxContainerStyle = { display: 'flex', flexDirection: 'column', gap: '10px' };
const checkboxLabelStyle = { display: 'flex', alignItems: 'center', gap: '12px', cursor: 'pointer', fontSize: '1rem', color: '#444' };
const checkboxStyle = { width: '18px', height: '18px', accentColor: '#333', cursor: 'pointer' };
const titleStyle = { marginBottom: '40px', fontSize: '2.5rem', fontWeight: '300', fontFamily: 'var(--title-font)', letterSpacing: '2px' };
const gridStyle = { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '30px' };