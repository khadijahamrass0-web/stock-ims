# StockIMS — Gestion de stock avec Spring Boot

Application web de gestion de stock réalisée dans le cadre du projet Java 21 /
Spring Boot (séances 9 et 10). Elle permet de gérer un catalogue d'articles, d'enregistrer
des entrées et des sorties, et de calculer le stock disponible à partir des mouvements.

**Étudiants**

| Étudiant | Nom et prénom |
|---|---|
| 1 | Saber Salma |
| 2 | Hamras Khadija |

**Contexte** : ESITH — IMS 2e année, Java et programmation orientée objet.

---

## 1. Fonctionnalités

- Ajouter un article avec une **référence unique** et une désignation.
- Consulter la liste des articles avec leur **stock calculé**.
- Ouvrir la fiche d'un article : stock, formulaire de mouvement, historique.
- Enregistrer une **entrée** ou une **sortie** pour un article.
- Calculer le stock : `somme des entrées − somme des sorties`.
- Un article sans mouvement a un stock de **0**.
- Refuser côté serveur :
  - les champs obligatoires vides ;
  - les références en double (contrôle applicatif + contrainte `unique` MySQL) ;
  - les quantités vides, nulles, négatives ou non entières ;
  - une sortie supérieure au stock disponible, avec un message lisible.
- Protéger les sorties simultanées par une transaction et un **verrou pessimiste**.
- Interface responsive avec recherche instantanée et confirmation avant une sortie.

Les validations fonctionnent **même si JavaScript est désactivé** : elles sont réalisées
par Jakarta Validation, les constructeurs métier et le service.

---

## 2. Technologies

| Élément | Version / usage |
|---|---|
| Java | 21 (Temurin) |
| Spring Boot | 4.1.2 |
| Spring MVC + Thymeleaf | pages HTML rendues côté serveur |
| Spring Data JPA / Hibernate | persistance |
| MySQL | 8.0, base `stock_ims` |
| Lombok | getters, constructeurs |
| Maven Wrapper | `mvnw` / `mvnw.cmd` fournis |
| JUnit 5 + Mockito | tests métier |

---

## 3. Structure du projet

```text
stock-ims/
├─ pom.xml
├─ mvnw, mvnw.cmd, .mvn/wrapper/
├─ sql/
│  ├─ init-db.sql               création de la base et du compte stock_app
│  ├─ stockims.sql              structure + données de test
│  ├─ stockims-export.sql       export réel de la base (structure + données)
│  └─ reinitialiser-donnees.sql remise à zéro du jeu de démonstration
├─ docs/
│  ├─ INSTALLATION-MYSQL.md
│  ├─ PRESENTATION.md
│  ├─ captures/                 captures d'écran
│  └─ remise/                   fichiers Moodle (.txt et .docx)
├─ src/main/java/ma/esith/stockims/
│  ├─ model/        Article, MouvementStock, Entree, Sortie
│  ├─ repository/   ArticleRepository, EntreeRepository, SortieRepository
│  ├─ service/      ArticleService, StockService, EtatStock, MouvementLigne
│  └─ web/          ArticleForm, MouvementForm, ArticleController, StockController
├─ src/main/resources/
│  ├─ application.properties
│  ├─ messages.properties
│  ├─ static/css/app.css
│  ├─ static/js/app.js
│  └─ templates/
│     ├─ fragments/layout.html
│     ├─ articles/liste.html, articles/formulaire.html
│     ├─ stock/fiche.html
│     └─ error.html
└─ src/test/java/…  tests des règles métier
```

---

## 4. Installation et lancement

### 4.1 Prérequis

- JDK 21 (`java -version`).
- MySQL 8 démarré sur `localhost:3306`.
- Aucun Maven à installer : le wrapper est fourni.
- Connexion Internet au premier lancement pour télécharger les dépendances Maven.

### 4.2 Créer la base et le compte MySQL

Dans MySQL Workbench (connecté en `root`) ou en ligne de commande :

```bash
mysql -u root -p < sql/init-db.sql
mysql -u root -p < sql/stockims.sql
```

Le fichier `sql/stockims-export.sql` est un export `mysqldump` réel de la base
(structure + données). Il peut aussi être importé directement :

```bash
mysql -u root -p < sql/stockims-export.sql
```

Ou, avec le compte applicatif :

```bash
mysql -u stock_app -p stock_ims < sql/stockims.sql
```

Les scripts créent :

- la base `stock_ims` (`utf8mb4`) ;
- le compte `stock_app` avec le mot de passe local `StockApp!2026` ;
- les tables `article`, `entree`, `sortie` et leur clé étrangère `article_id`.

> Si le mot de passe `root` est inconnu, consultez
> [`docs/INSTALLATION-MYSQL.md`](docs/INSTALLATION-MYSQL.md).

### 4.3 Adapter la connexion

Fichier `src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/stock_ims?useUnicode=true&characterEncoding=utf8&serverTimezone=Africa/Casablanca
spring.datasource.username=stock_app
spring.datasource.password=StockApp!2026
```

Adaptez l'URL, l'utilisateur et le mot de passe si votre installation diffère.

### 4.4 Démarrer l'application

Windows :

```powershell
.\mvnw.cmd spring-boot:run
```

macOS / Linux :

```bash
./mvnw spring-boot:run
```

Ouvrir ensuite : **http://localhost:8080/articles**

Pour construire le JAR exécutable :

```powershell
.\mvnw.cmd clean package
java -jar target/stock-ims-0.0.1-SNAPSHOT.jar
```

### 4.5 Tests automatisés

```powershell
.\mvnw.cmd test
```

12 tests couvrent la création des articles, les quantités invalides, le calcul
`20 − 7 = 13`, le stock nul sans mouvement, le refus d'une sortie de 15 et
l'acceptation d'une sortie égale au stock.

---

## 5. Jeu de données de test

| Référence | Désignation | Entrées | Sorties | Stock |
|---|---|---:|---:|---:|
| TIS-001 | Rouleau de coton | 20 | 7 | **13** |
| TIS-002 | Rouleau de lin | — | — | **0** |
| FIL-001 | Bobine de fil | 40 + 15 | 12 | **43** |

Le script `sql/stockims.sql` affiche un contrôle SQL du stock en fin d'exécution.

---

## 6. Scénario de vérification demandé

Sur un article neuf (stock initial 0) :

| Action | Résultat attendu |
|---|---|
| Ouvrir la fiche | stock 0 |
| Entrée 20 | stock 20 |
| Sortie 7 | stock 13 |
| Sortie 15 | **refus** « Stock insuffisant : 13 unité(s) disponible(s). », stock **13** |
| Quantité vide | message « La quantité est obligatoire. » |
| Quantité 0 ou -5 | message « La quantité doit être un entier strictement positif. » |
| Référence en double | message « Cette référence existe déjà. » |
| Redémarrer Spring Boot | les articles et les stocks sont conservés |

Vérification SQL :

```sql
USE stock_ims;
SELECT a.reference,
       COALESCE((SELECT SUM(e.quantite) FROM entree e WHERE e.article_id = a.id), 0)
     - COALESCE((SELECT SUM(s.quantite) FROM sortie s WHERE s.article_id = a.id), 0) AS stock
FROM article a
ORDER BY a.reference;
```

Une sortie refusée ne crée **aucune ligne** dans la table `sortie`.

---

## 7. Routes de l'application

| Méthode | Route | Rôle |
|---|---|---|
| GET | `/` | redirection vers `/articles` |
| GET | `/articles` | liste + stock + recherche |
| GET | `/articles/nouveau` | formulaire de création |
| POST | `/articles` | enregistrement, doublons et champs invalides refusés |
| GET | `/stock/{id}` | fiche : stock, formulaire, historique |
| POST | `/stock/{id}/entrees` | ajout d'une entrée |
| POST | `/stock/{id}/sorties` | ajout d'une sortie, refus si stock insuffisant |

---

## 8. Organisation objet

| Notion | Utilisation dans StockIMS |
|---|---|
| Encapsulation | champs `private` dans `Article` et `MouvementStock`, constructeurs qui contrôlent les valeurs, pas de `@Setter` sur `Article` |
| Abstraction / héritage | `MouvementStock` est abstraite ; `Entree` et `Sortie` héritent de ses champs et règles |
| Polymorphisme | la boucle de `StockService.calculerStock` appelle `quantiteSignee()` : `+quantite` pour une entrée, `−quantite` pour une sortie |
| Interface et génériques | `ArticleRepository extends JpaRepository<Article, Long>`, idem pour les mouvements |
| Collections et exceptions | `List<MouvementStock>`, `addAll`, `IllegalArgumentException`, `ResponseStatusException` |
| Transactions et verrous | `@Transactional` + `@Lock(PESSIMISTIC_WRITE)` pour sérialiser les sorties d'un même article |

`ArticleForm` et `MouvementForm` ne sont **pas des entités** : ce sont des objets de
saisie web, sans table associée.

---

## 9. Personnalisation de l'interface

Identité **« Minimal clair »** : fond ardoise très clair, cartes blanches, accent
émeraude, typographie système.

- **HTML** : fragment commun `layout.html`, navigation claire, fil d'Ariane,
  tableaux accessibles, formulaires avec aides et messages d'erreur, historique des mouvements.
- **CSS** : variables de couleurs, cartes, badges de stock, boutons, alertes,
  adaptation aux écrans < 900 px et < 720 px, styles de focus.
- **JavaScript** : filtre de recherche instantané (insensible aux accents),
  dialogue de confirmation avant une sortie avec rappel du stock, fermeture automatique
  des messages de succès. Sans JavaScript, les formulaires et validations serveur restent opérationnels.

Les choix détaillés sont présentés dans [`docs/PRESENTATION.md`](docs/PRESENTATION.md).

---

## 10. Captures d'écran

Les trois captures demandées se trouvent dans `docs/captures/` :

1. `01-liste-articles.png` — liste personnalisée, stocks et recherche.
2. `02-fiche-stock-13.png` — stock calculé (13) et historique.
3. `03-refus-sortie-15.png` — message de refus d'une sortie de 15.

---

## 11. Remise

- Dépôt GitHub : **https://github.com/khadijahamrass0-web/stock-ims**
- Le professeur `amozher` (`mozher.abdeslam@gmail.com`) est invité comme collaborateur.
- Fichiers Moodle : `StockIMS_Saber_Hamras.txt` et `StockIMS_Saber_Hamras.docx`
  (dossier `docs/remise/`).
