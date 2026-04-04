import React, { useEffect, useState } from 'react';
import FeaturedCard from './FeaturedCard';
import api from '../api'; // Путь к твоему файлу с axios

export default function FeaturedSection() {
    // 1. Создаем состояние для категорий
    const [categories, setCategories] = useState([]);

    // 2. Загружаем данные при открытии страницы
    useEffect(() => {
        api.get('/categories') // Вызываем твой @GetMapping getAll()
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
                        id={cat.slug}      // ТЕПЕРЬ ПЕРЕДАЕМ SLUG (zefir, macaron)
                        name={cat.name}    // Имя из БД
                        image={cat.imageUrl} // Картинка из БД (/images/strawberry.jpg)
                    />
                ))}
            </div>
        </section>
    );
}