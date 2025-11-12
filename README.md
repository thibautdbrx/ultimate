
# 🥏 Ultimate - Projet Web & API

## 📌 Description
Ce projet est composé de deux parties :
- **backend/** → API REST développée avec **Spring Boot**
- **frontend/** → Site web qui consomme l’API

L’objectif est de faciliter la gestion des tournois d’Ultimate (équipes, matchs, résultats, etc.).

---

## ⚙️ Installation du projet

### 1️⃣ Cloner le dépôt
```bash
git clone https://gitlab.univ-lorraine.fr/coll/l-inp/polytech/ia2r-fise-promo-2027/pierre-ludmann-ultimate.git
cd pierre-ludmann-ultimate
```

### 2️⃣ Installation du backend
```bash
cd backend/API-Ultimate
mvn clean install
```

💡 *Si Maven n’est pas installé :*
- **Mac** → `brew install maven`
- **Windows** → [télécharger Maven](https://maven.apache.org/download.cgi)

Lancer ensuite le serveur :
```bash
mvn spring-boot:run
```
➡️ L’API sera accessible sur [http://localhost:8080](http://localhost:8080)

### 3️⃣ Installation du frontend

C’est un projet avec un framework (Vue) :
```bash
cd frontend
npm install
npm run dev
```

---

## 🌳 Workflow Git – Organisation du projet

### 🧱 Structure des branches

| Branche | Rôle |
|----------|------|
| **main** | Version stable, validée et livrable |
| **dev** | Branche principale de développement |
| **feature/...** | Nouvelle fonctionnalité |
| **fix/...** | Correction de bug |

---

### 🧩 Règles à suivre

1. **Ne jamais travailler directement sur `main`**
2. Toujours créer une branche à partir de `dev`
3. Une fois la fonctionnalité terminée, faire une **Merge Request** vers `dev`
4. `dev` → `main` uniquement quand le projet est stable et validé

---

### 🚀 Commandes utiles

Créer et passer sur une branche de développement :
```bash
git checkout dev
git pull
git checkout -b feature/nom-fonctionnalite
```

Vérifier les branches existantes :
```bash
git branch -a
```

Sauvegarder ton travail :
```bash
git add .
git commit -m "Ajout de la fonctionnalité X"
git push -u origin feature/nom-fonctionnalite
```

Fusionner ta branche dans `dev` :
```bash
git checkout dev
git pull
git merge feature/nom-fonctionnalite
git push
```

Supprimer une branche locale et distante une fois fusionnée :
```bash
git branch -d feature/nom-fonctionnalite
git push origin --delete feature/nom-fonctionnalite
```

---

### 🧠 Bonnes pratiques Git

- Toujours faire un `git pull` avant de commencer à coder  
- Nommer clairement les branches :  
  - `feature/backend-api`  
  - `feature/frontend-login`  
  - `fix/navbar-bug`  
- Commits fréquents avec des messages explicites  
- Une seule fonctionnalité par branche  
- Merge uniquement via des **Merge Requests** (pas de push direct sur `main`)

---

## 👥 Équipe

- Becker Florent
- Caudron Corentin
- Debreux Thibaut
- Gicquel Loup
- Joseph-Schenfeigel Simeon
- Thirion Valentine

---

✅ **Résumé rapide :**
```bash
# Cloner le projet
git clone https://gitlab.univ-lorraine.fr/coll/l-inp/polytech/ia2r-fise-promo-2027/pierre-ludmann-ultimate.git
cd pierre-ludmann-ultimate

# Créer une branche
git checkout dev
git checkout -b feature/ma-fonction

# Travailler puis envoyer
git add .
git commit -m "Ajout de ma fonctionnalité"
git push -u origin feature/ma-fonction
```
