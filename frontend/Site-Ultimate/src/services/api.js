import axios from 'axios';

// On crée l'instance avec la configuration de base
const api = axios.create({
    // Comme on a un proxy dans vite.config.js, on met juste '/api'
    baseURL: '/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

// INTERCEPTEUR : Ajoute le token automatiquement avant chaque envoi
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// INTERCEPTEUR : Gère les erreurs de retour (ex: session expirée)
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            // Si le backend dit "401 Unauthorized", on déconnecte
            localStorage.removeItem('token');
            window.location.href = '/Connexion';
        }
        return Promise.reject(error);
    }
);

export default api;