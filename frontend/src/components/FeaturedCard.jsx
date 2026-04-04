import { useNavigate } from 'react-router-dom';

export default function FeaturedCard({ id, name, image, price }) {
    const navigate = useNavigate();

    // Функция для обработки клика
    const handleClick = () => {
        if (price) {
            // Если это ТОВАР (есть цена)
            navigate(`/product/${id}`);
        } else if (id) {
            // Если это КАТЕГОРИЯ
            navigate(`/category/${id}`);
        }
    };

    return (
        <div
            onClick={handleClick}
            style={{
                background: 'white',
                borderRadius: '20px',
                width: '280px',
                textAlign: 'center',
                cursor: price ? 'default' : 'pointer', // У товара курсор обычный, у категории - палец
                boxShadow: '0 4px 20px rgba(0,0,0,0.06)',
                overflow: 'hidden',
                transition: 'transform 0.2s ease-in-out'
            }}
        >
            <img
                src={image || 'https://via.placeholder.com/280'}
                style={{ width: '100%', height: '280px', objectFit: 'cover' }}
            />
            <div style={{ padding: '20px' }}>
                <h3 style={{ margin: '0 0 10px 0', fontFamily: 'var(--title-font)' }}>{name}</h3>

                {price ? (
                    /* ДЛЯ ТОВАРА: только цена и кнопка */
                    <>
                        <p style={{
                            fontSize: '18px',
                            fontWeight: '600',
                            color: '#333',
                            margin: '10px 0'
                        }}>
                            {price} BYN
                        </p>
                        <button style={{
                            background: '#333',
                            color: 'white',
                            border: 'none',
                            padding: '10px 20px',
                            borderRadius: '5px',
                            width: '100%',
                            cursor: 'pointer',
                            fontFamily: 'var(--body-font)'
                        }}>
                            В КОРЗИНУ
                        </button>
                    </>
                ) : (
                    /* ДЛЯ КАТЕГОРИИ: только ссылка-подсказка */
                    <p style={{
                        color: 'gray',
                        fontSize: '14px',
                        textTransform: 'uppercase',
                        letterSpacing: '1px'
                    }}>
                        Смотреть все товары →
                    </p>
                )}
            </div>
        </div>
    );
}