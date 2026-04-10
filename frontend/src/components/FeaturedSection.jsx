import React, { useEffect, useState } from 'react';
import FeaturedCard from './FeaturedCard';
import api from '../api';
export default function FeaturedSection() {

    const [categories, setCategories] = useState([]);

    useEffect(() => {
        api.get('/categories')
            .then(res => {
                setCategories(res.data);
            })
            .catch(err => console.error("Ошибка загрузки категорий:", err));
    }, []);

    return (
        <section style={{ padding: '0 80px 80px' }}>
            <h2 style={{ fontSize: '42px', textAlign: 'center', marginBottom: '40px', fontFamily: 'var(--title-font)' }}>
                Наша продукция
            </h2>
            <div style={{ display: 'flex', gap: '40px', justifyContent: 'center', flexWrap: 'wrap' }}>
                {categories.map(cat => (
                    <FeaturedCard
                        key={cat.id}
                        id={cat.slug}
                        name={cat.name}
                        image={cat.imageUrl}
                    />
                ))}
            </div>
        </section>
    );
}