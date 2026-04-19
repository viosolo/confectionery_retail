import axios from 'axios';

const api = axios.create({
    baseURL: window.location.hostname === 'localhost'
        ? 'http://localhost:8080/api'
        : 'https://confectioneryretail-production.up.railway.app/api'
});

export default api;