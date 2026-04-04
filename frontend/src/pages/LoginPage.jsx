import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api';

const LoginPage = () => {
    const [credentials, setCredentials] = useState({ email: '', password: '' });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        setCredentials({ ...credentials, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await api.post('/users/login', credentials);

            if (response.data) {
                localStorage.setItem('user', JSON.stringify(response.data));
                window.dispatchEvent(new Event('storage'));
                navigate('/profile');
            }
        } catch (err) {
            setError('Неверный email или пароль');
        }
    };

    return (
        <div style={containerStyle}>
            <form onSubmit={handleSubmit} style={formStyle}>
                <h2 style={titleStyle}>Вход в аккаунт</h2>
                {error && <p style={{color: 'red', fontSize: '0.8rem'}}>{error}</p>}

                <input
                    type="email"
                    name="email"
                    placeholder="Email"
                    onChange={handleChange}
                    required
                    style={inputStyle}
                />
                <input
                    type="password"
                    name="password"
                    placeholder="Пароль"
                    onChange={handleChange}
                    required
                    style={inputStyle}
                />

                <button type="submit" style={buttonStyle}>ВОЙТИ</button>

                <p style={{marginTop: '20px', fontSize: '0.9rem'}}>
                    Нет аккаунта? <Link to="/register" style={{color: '#000', textDecoration: 'underline'}}>Создать</Link>
                </p>
            </form>
        </div>
    );
};

const containerStyle = { display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh' };
const formStyle = { width: '100%', maxWidth: '400px', padding: '40px', border: '1px solid #eee', textAlign: 'center' };
const titleStyle = { fontWeight: '300', marginBottom: '30px', letterSpacing: '1px' };
const inputStyle = { width: '100%', padding: '12px', marginBottom: '15px', border: '1px solid #ddd', outline: 'none', boxSizing: 'border-box' };
const buttonStyle = { width: '100%', padding: '15px', background: '#000', color: '#fff', border: 'none', cursor: 'pointer', letterSpacing: '1px' };

export default LoginPage;