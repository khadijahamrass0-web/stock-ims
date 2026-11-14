# StockIMS - Gestion de stock

Application web de gestion de stock réalisée avec Java 21, Spring Boot, MySQL et Thymeleaf.

**Étudiantes**

| Étudiante | Nom et prénom |
|---|---|
| 1 | Saber Salma |
| 2 | Hamras Khadija |

---

## 1. Fonctionnalités

- Ajouter un article avec une référence unique et une désignation.
- Afficher la liste des articles avec leur stock.
- Ouvrir la fiche d'un article : stock, formulaire de mouvement et historique.
- Enregistrer une entrée ou une sortie.
- Calculer le stock : `somme des entrées - somme des sorties`.
- Un article sans mouvement a un stock de 0.
- Refuser côté serveur :
  - les champs obligatoires vides ;
  - les références en double ;
  - les quantités vides, nulles, négatives ou non entières ;
  - une sortie supérieure au stock disponible.
- Protéger les sorties simultanées avec une transaction et un verrou pessimiste.

Les contrôles fonctionnent même si JavaScript est désactivé.

## 2. Technologies

- Java 21
- Spring Boot 4.1.2
- Spring MVC et Thymeleaf
- Spring Data JPA / Hibernate
- MySQL 8
- Lombok
- Maven Wrapper
- JUnit 5 et Mockito

## 3. Base de données

Créer la base et le compte applicatif avec un compte MySQL administrateur :

```bash
mysql -u root -p < sql/init-db.sql
```

Créer les tables et insérer le jeu de données de test :

```bash
mysql -u root -p < sql/stockims.sql
```

La connexion est configurée dans `src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/stock_ims?useUnicode=true&characterEncoding=utf8&serverTimezone=Africa/Casablanca
spring.datasource.username=stock_app
spring.datasource.password=StockApp!2026
```

## 4. Lancement

Windows :

```powershell
.\mvnw.cmd spring-boot:run
```

macOS / Linux :

```bash
./mvnw spring-boot:run
```

Ouvrir ensuite : **http://localhost:8080/articles**

Construction du JAR :

```powershell
.\mvnw.cmd clean package
java -jar target/stock-ims-0.0.1-SNAPSHOT.jar
```

## 5. Tests

```powershell
.\mvnw.cmd test
```

Les 12 tests couvrent la création des articles, les quantités invalides, le calcul
`20 - 7 = 13`, le stock nul sans mouvement, le refus d'une sortie de 15 et
l'acceptation d'une sortie égale au stock.

## 6. Jeu de données de test

| Référence | Désignation | Entrées | Sorties | Stock |
|---|---|---:|---:|---:|
| TIS-001 | Rouleau de coton | 20 | 7 | **13** |
| TIS-002 | Rouleau de lin | - | - | **0** |
| FIL-001 | Bobine de fil | 40 + 15 | 12 | **43** |

## 7. Scénario de vérification

| Action | Résultat attendu |
|---|---|
| Article sans mouvement | stock 0 |
| Entrée de 20 | stock 20 |
| Sortie de 7 | stock 13 |
| Sortie de 15 | refus : "Stock insuffisant : 13 unité(s) disponible(s)." |
| Quantité vide | message : "La quantité est obligatoire." |
| Quantité 0 ou -5 | message : "La quantité doit être un entier strictement positif." |
| Référence en double | message : "Cette référence existe déjà." |
| Redémarrage de l'application | données conservées |

Une sortie refusée ne crée aucune ligne dans la table `sortie`.

## 8. Routes

| Méthode | Route | Rôle |
|---|---|---|
| GET | `/` | redirection vers `/articles` |
| GET | `/articles` | liste des articles et stock |
| GET | `/articles/nouveau` | formulaire de création |
| POST | `/articles` | enregistrement d'un article |
| GET | `/stock/{id}` | fiche de stock et historique |
| POST | `/stock/{id}/entrees` | ajout d'une entrée |
| POST | `/stock/{id}/sorties` | ajout d'une sortie |

## 9. Organisation du code

```text
src/main/java/ma/esith/stockims/
├─ model/        Article, MouvementStock, Entree, Sortie
├─ repository/   ArticleRepository, EntreeRepository, SortieRepository
├─ service/      ArticleService, StockService
└─ web/          contrôleurs et formulaires
```

- `MouvementStock` est une classe abstraite.
- `Entree` et `Sortie` héritent de `MouvementStock`.
- `StockService` utilise le polymorphisme de `quantiteSignee()` pour calculer le stock.
- Les repositories étendent `JpaRepository`.

## 10. Interface

- Page claire sur fond gris très clair, cartes blanches et accent vert.
- Mise en page responsive avec cartes, badges de stock et tableaux accessibles.
- Fragments Thymeleaf communs pour l'en-tête, le pied de page et les messages.
- Recherche dans la liste des articles, insensible aux accents.
- Confirmation avant l'enregistrement d'une sortie.
- Messages de succès refermables.

## 11. Captures d'écran

Les captures se trouvent dans `docs/captures/` :

1. `01-liste-articles.png` - liste des articles et recherche.
2. `02-fiche-stock-13.png` - fiche de stock et historique.
3. `03-refus-sortie-15.png` - refus d'une sortie supérieure au stock.

## 12. Dépôt GitHub

**https://github.com/khadijahamrass0-web/stock-ims**
