## API Ultimate (apiultimate)

Bienvenue sur la documentation de l'API Ultimate. Il s'agit d'une API RESTful basée sur Spring Boot conçue pour gérer des équipes, des joueurs et des matchs dans le contexte de l'Ultimate Frisbee (ou tout autre sport d'équipe).

L'API expose des endpoints pour les opérations CRUD (Create, Read, Update, Delete) sur les entités principales : `Equipe`, `Joueur`, et `Match`.

-----

## 🚀 Modèles de Données

L'API s'articule autour de trois modèles principaux :

* **`Equipe`** : Représente une équipe.
  * `id_equipe` (Long) : Identifiant unique.
  * `nom_equipe` (String) : Nom de l'équipe.
  * `joueurs` (List\<Joueur\>) : Liste des joueurs appartenant à cette équipe.
* **`Joueur`** : Représente un joueur.
  * `id_joueur` (Long) : Identifiant unique.
  * `nom_joueur` (String) : Nom du joueur.
  * `prenom_joueur` (String) : Prénom du joueur.
  * `genre` (Enum: `MALE`, `FEMALE`) : Genre du joueur.
  * `equipe` (Equipe) : L'équipe à laquelle le joueur est assigné (relation ManyToOne).
* **`Match`** : Représente un match entre deux équipes.
  * `matchId` (Long) : Identifiant unique.
  * `equipe1` (Equipe) : La première équipe.
  * `equipe2` (Equipe) : La seconde équipe.
  * `score_equipe1` (Long) : Score de l'équipe 1.
  * `score_equipe2` (Long) : Score de l'équipe 2.
  * `date_debut` (LocalDateTime) : Heure de début du match.
  * `date_fin` (LocalDateTime) : Heure de fin du match.
  * `status` (Enum: `WAITING`, `FINISHED`, `ONGOING`, `PAUSED`) : Statut actuel du match.

-----

## 📖 Documentation de l'API (Endpoints)

La base de l'URL pour tous les endpoints est `/api`.

### 🎽 Gestion des Équipes (`/api/equipe`)

Endpoints pour gérer les équipes.

| Méthode | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/equipe` | Récupère la liste de toutes les équipes. |
| `GET` | `/api/equipe/{id}` | Récupère une équipe spécifique par son ID. |
| `POST` | `/api/equipe` | Crée une nouvelle équipe. |
| `DELETE` | `/api/equipe/{id}` | Supprime une équipe par son ID. |

**Exemple de Payload (POST /api/equipe)**

```json
{
  "nom_equipe": "Les Disc-Jockeys"
}
```
-----

### 🏃 Gestion des Joueurs (`/api/joueur`)

Endpoints pour gérer les joueurs et leur assignation aux équipes.

| Méthode  | Endpoint                                     | Description                                        |
|:---------|:---------------------------------------------|:---------------------------------------------------|
| `GET`    | `/api/joueur`                                | Récupère la liste de tous les joueurs.             |
| `GET`    | `/api/joueur/{id}`                           | Récupère un joueur spécifique par son ID.          |
| `POST`   | `/api/joueur`                                | Crée un nouveau joueur (non assigné).              |
| `PUT`    | `/api/joueur/{id_joueur}/equipe/{id_equipe}` | Assigne un joueur existant à une équipe existante. |
| `DELETE` | `/api/joueur/{id}`                           | Supprime un joueur par son ID.                     |
| `DELETE` | `/api/joueur/{id_joueur}/equipe/{id_equipe}` | Retire un joueur d'une équipe spécifique.          |

**Exemple de Payload (POST /api/joueur)**

```json
{
  "nom_joueur": "Dupont",
  "prenom_joueur": "Jean",
  "genre": "MALE"
}
```
-----

### 🏆 Gestion des Matchs (`/api/match`)

Endpoints pour créer des matchs et gérer leur déroulement.

| Méthode | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/match` | Récupère la liste de tous les matchs. |
| `GET` | `/api/match/{id}` | Récupère un match spécifique par son ID. |
| `POST` | `/api/match/equipe1/{id_equipe1}/equipe2/{id_equipe2}` | Crée un nouveau match (statut `WAITING`). |
| `PUT` | `/api/match/{id}/start` | Démarre le match (passe à `ONGOING`). |
| `PUT` | `/api/match/{id}/pause` | Met le match en pause (passe à `PAUSED`). |
| `PUT` | `/api/match/{id}/resume` | Reprend le match (passe à `ONGOING`). |
| `PUT` | `/api/match/{id}/end` | Termine le match (passe à `FINISHED`). |
| `PUT` | `/api/match/{id_match}/addPoint/{id_equipe}` | Ajoute un point à l'équipe spécifiée. |
| `PUT` | `/api/match/{id_match}/removePoint/{id_equipe}` | Retire un point à l'équipe spécifiée. |
| `DELETE` | `/api/match/{id}` | Supprime un match par son ID. |

**Exemple de création (POST)**

> **Note :** La création d'un match ne nécessite pas de payload JSON. Il suffit de faire un appel `POST` à l'URL en spécifiant les ID des deux équipes.

Par exemple, pour créer un match entre l'équipe avec l'ID `1` et l'équipe avec l'ID `2` :

`POST /api/match/equipe1/1/equipe2/2`