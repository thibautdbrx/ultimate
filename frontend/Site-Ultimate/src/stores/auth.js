import { defineStore } from "pinia";
import router from "@/router";

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(";").shift();
    return null;
}

export const useAuthStore = defineStore("auth", {
    state: () => ({
        token: null,
        email: null,
        role: null,
    }),
    getters: {
    // Ces variables seront calculées automatiquement
        isAdmin: (state) => state.role === 'ROLE_ADMIN',
        isArbitre: (state) => state.role === 'ROLE_ARBITRE',
        isVisiteur: (state) => state.role === 'ROLE_VISITEUR'
    },

    actions: {
        loadToken() {
            // récupère le token dans le cookie
            const token = getCookie("token");
            if (!token) return;

            this.token = token;

            const payload = JSON.parse(atob(token.split(".")[1]));

            const now = Math.floor(Date.now() / 1000);
            if (payload.exp && payload.exp < now) {
                this.logout();
                router.push("/Connexion");
                return;
            }

            this.email = payload.sub;
            this.role = payload.role;
        },

        logout() {
            this.token = null;
            this.email = null;
            this.role = null;

            // supprimer le cookie
            document.cookie = "token=; Max-Age=0";
        }
    }
});