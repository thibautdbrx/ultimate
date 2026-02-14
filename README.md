# 🥏 Ultimate Tournament Manager

### 📌 Présentation

L’objectif est de faciliter la gestion des tournois d’Ultimate (équipes, matchs, résultats, etc.).

- **Backend :** API REST avec Spring Boot.
- **Frontend :** Interface réactive et moderne développée avec Vue.js.

---

## ⚙️ Installation du projet

### 1. Cloner le dépôt

```bash
git clone https://github.com/thibautdbrx/ultimate.git
cd gestion-ultimate
```

### 2. Lancer le Backend (API)

Le backend utilise une base de données **H2 (en mémoire)** par défaut pour le développement. Aucune installation de base de données n'est requise.

```bash
cd backend/API-Ultimate
mvn clean install
mvn spring-boot:run
```

API : http://localhost:8080

Console H2 : http://localhost:8080/h2-console

Swagger UI : http://localhost:8080/documentation

### 3. Lancer le Frontend (Web)

```bash
cd frontend
npm install
npm run dev
```

L'application est disponible sur : `http://localhost:5173` (ou port affiché dans le terminal).

---

## 🛠 Architecture & Workflow

### 🌳 Stratégie de Branches

| Branche     | Usage                                                          |
| ----------- | -------------------------------------------------------------- |
| `main`      | **Production** : Code stable et testé.                         |
| `dev`       | **Développement** : Intégration des nouvelles fonctionnalités. |
| `feature/*` | **Features** : Branches isolées pour chaque tâche.             |
| `fix/*`     | **Hotfix** : Corrections de bugs.                              |

### 🧩 Workflow Git

1. **Pull** : systématique avant de coder et push : `git pull origin dev`
2. **Feature branching** : Créer une branche explicite (`feature/api-auth`).
3. **Commits** : Messages clairs et précis.
4. **Pull Requests** : Fusion vers la branche après relecture.

---

## 💻 Aperçu des fonctionnalités

- ✅ Gestion des équipes et des joueurs.
- ✅ Génération de calendriers de matchs.
- ✅ Saisie des scores et mise à jour automatique des classements.
- ✅ Dashboard administrateur.
- ✅ Swagger disponible.

---

## 👥 L'Équipe

Projet réalisé par :

- Florent Becker
- Thibaut Debreux ([@thibautdbrx](https://github.com/thibautdbrx))
- Loup Gicquel
- Simeon Joseph-Schenfeigel
- Valentine Thirion
- Corentin Caudron ([@CaudronCorentin](https://github.com/CaudronCorentin))
