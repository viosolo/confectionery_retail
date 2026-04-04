import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api';

const RegisterPage = () => {
    const [formData, setFormData] = useState({
        firstName: '', lastName: '', email: '', password: '', phone: ''
    });
    // НОВОЕ СОСТОЯНИЕ: для отслеживания видимости пароля
    const [showPassword, setShowPassword] = useState(false);

    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await api.post('/users/register', formData);
            alert("Регистрация успешна!");
            navigate('/login');
        } catch (err) {
            alert("Ошибка при регистрации, проверьте данные");
        }
    };

    // ФУНКЦИЯ: переключает состояние видимости
    const togglePasswordVisibility = () => {
        setShowPassword(!showPassword);
    };

    return (
        <div style={containerStyle}>
            <form onSubmit={handleSubmit} style={formStyle}>
                <h2 style={titleStyle}>Создание аккаунта</h2>
                <input name="firstName" placeholder="Имя" onChange={handleChange} required style={inputStyle} />
                <input name="lastName" placeholder="Фамилия" onChange={handleChange} required style={inputStyle} />
                <input name="email" type="email" placeholder="Email" onChange={handleChange} required style={inputStyle} />

                {/* КОНТЕЙНЕР ДЛЯ ПОЛЯ ПАРОЛЯ И ГЛАЗИКА */}
                <div style={passwordContainerStyle}>
                    <input
                        name="password"
                        // Динамически меняем тип: password -> text -> password
                        type={showPassword ? "text" : "password"}
                        placeholder="Пароль"
                        onChange={handleChange}
                        required
                        style={inputStyle}
                    />
                    {/* КНОПКА-ГЛАЗИК (используем простые символы для минимиализма) */}
                    <button
                        type="button" // Важно! Чтобы не отправлял форму
                        onClick={togglePasswordVisibility}
                        style={eyeButtonStyle}
                        title={showPassword ? "Скрыть пароль" : "Показать пароль"}
                    >
                        {/* Иконка: 👁️ (показать) или 🙈 (скрыть) */}
                        {showPassword ? "🙈" : "👁️"}
                    </button>
                </div>

                <input name="phone" placeholder="Телефон" onChange={handleChange} required style={inputStyle} />

                <button type="submit" style={buttonStyle}>ЗАРЕГИСТРИРОВАТЬСЯ</button>

                <p style={{marginTop: '20px', fontSize: '0.9rem'}}>
                    Уже есть аккаунт? <Link to="/login" style={{color: '#000', textDecoration: 'underline'}}>Войти</Link>
                </p>
            </form>
        </div>
    );
};

// --- СТИЛИ (добавь новые для глазика) ---

const containerStyle = { display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh' };
const formStyle = { width: '100%', maxWidth: '400px', padding: '40px', border: '1px solid #eee', textAlign: 'center' };
const titleStyle = { fontWeight: '300', marginBottom: '30px', letterSpacing: '1px' };

// Общий стиль для input. Важно: для пароля убери margin-bottom
const inputStyle = { width: '100%', padding: '12px', marginBottom: '15px', border: '1px solid #ddd', outline: 'none', boxSizing: 'border-box' };

// Контейнер, чтобы позиционировать глазик внутри input
const passwordContainerStyle = { position: 'relative', width: '100%' };

// Стиль для кнопки-глазика
const eyeButtonStyle = {
    position: 'absolute',
    right: '10px',
    top: '40%', // Центрируем по вертикали
    transform: 'translateY(-50%)',
    background: 'none',
    border: 'none',
    cursor: 'pointer',
    fontSize: '1.2rem',
    opacity: '0.6', // Легкая прозрачность для минимализма
    padding: '5px',
    margin: 0,
    lineHeight: 1
};

const buttonStyle = { width: '100%', padding: '15px', background: '#000', color: '#fff', border: 'none', cursor: 'pointer', letterSpacing: '1px', marginTop: '10px' };

export default RegisterPage;