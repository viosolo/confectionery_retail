import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom' // Добавили эту строку
import App from './App.jsx'
import './index.css'

ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        {/* Оборачиваем App в BrowserRouter, чтобы навигация заработала */}
        <BrowserRouter>
            <App />
        </BrowserRouter>
    </React.StrictMode>,
)