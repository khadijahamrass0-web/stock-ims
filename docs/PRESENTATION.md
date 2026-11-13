# Présentation des choix de personnalisation

**Projet** : StockIMS — Gestion de stock avec Spring Boot  
**Étudiants** : Saber Salma · Hamras Khadija  
**Identité retenue** : « Minimal clair »

---

## 1. Intention visuelle

L'interface devait rester immédiatement compréhensible pour un utilisateur qui
découvre l'application, tout en donnant une identité plus soignée que les pages
brutes du guide. Le parti pris est un **minimalisme clair** :

- fond très légèrement coloré, cartes blanches et bordures fines ;
- un seul accent fort, l'émeraude, réservé aux actions et aux stocks disponibles ;
- rouge réservé aux erreurs, aux ruptures et aux sorties ;
- beaucoup d'espace blanc, peu d'ombres, aucune décoration inutile.

Cette direction met en valeur les trois informations essentielles : l'article,
la quantité et le stock.

## 2. Palette

| Variable | Couleur | Usage |
|---|---|---|
| `--fond` | `#f5f7f8` | fond général |
| `--surface` | `#ffffff` | cartes, tableaux, formulaires |
| `--encre` | `#17202a` | texte principal |
| `--encre-douce` | `#52606d` | textes secondaires |
| `--muet` | `#7b8794` | aides, légendes |
| `--bordure` | `#e3e8ec` | séparateurs |
| `--accent` | `#0f766e` | boutons primaires, liens, stock |
| `--accent-doux` | `#e6f4f1` | badges, icônes, survols |
| `--danger` | `#b42318` | erreurs, ruptures, sorties |
| `--succes` | `#067647` | messages de succès, entrées |

Les contrastes texte/fond respectent les seuils de lisibilité usuels
(texte sombre sur fond clair, texte blanc sur émeraude ou rouge foncé).

## 3. Typographie

- Pile système : `Segoe UI`, `system-ui`, sans-serif → aucune police à télécharger,
  rendu rapide et cohérent hors ligne.
- Titres avec un espacement négatif léger pour un rendu compact et moderne.
- Chiffres de stock en `font-variant-numeric: tabular-nums` : les colonnes restent alignées.
- Références d'articles en police monospace dans une pastille, pour les distinguer
  immédiatement des désignations.

## 4. Organisation HTML

Un fragment Thymeleaf commun, `templates/fragments/layout.html`, regroupe :

- le `<head>` (titre, favicon, feuille de style) ;
- l'en-tête avec la marque, la navigation et l'état actif ;
- le pied de page avec les noms des étudiantes ;
- les messages flash de succès ;
- l'inclusion du script JavaScript.

Chaque page appelle ces fragments (`th:replace`), ce qui évite la duplication et
garde les liaisons Thymeleaf (`th:action`, `th:object`, `th:field`, `th:errors`)
exactement au bon endroit.

Choix de structuration :

- **Fil d'Ariane** sur le formulaire et la fiche : on sait toujours où l'on est.
- **Liste** : bandeau de trois indicateurs (articles, stock total, ruptures), barre
  de recherche, tableau avec référence, désignation, stock et action.
- **Fiche** : grand stock affiché, formulaire de mouvement à côté, historique dessous.
- **Formulaires** : label explicite, texte d'aide, astérisque pour les champs
  obligatoires, message d'erreur sous le champ concerné.
- **Tableaux** : `caption` en lecture d'écran, en-têtes `scope="col"`, colonnes
  numériques alignées à droite.
- **Historique** : date, type (Entrée/Sortie), quantité saisie et effet signé.
  Cela rend visible la règle `stock = entrées − sorties`, y compris pour une sortie
  dont la quantité enregistrée reste positive mais dont l'effet est négatif.

## 5. CSS

- Variables CSS dans `:root` pour centraliser les couleurs, l'ombre et le rayon.
- Cartes avec bordure fine et ombre très légère ; pas d'empilement d'effets.
- Badges de stock : pastille verte « disponible » ou rouge « rupture ».
- Boutons :
  - primaire émeraude pour l'action principale ;
  - discret bordé pour les actions secondaires ;
  - rouge bordé pour une sortie, rouge plein dans la boîte de confirmation.
- Alertes différenciées par la couleur **et** par une icône, pas uniquement par la couleur.
- Focus visible (`outline` contrasté) sur tous les éléments interactifs.
- Adaptation :
  - sous 900 px, la fiche passe sur une colonne ;
  - sous 720 px, l'en-tête se replie, les boutons occupent la largeur, les tableaux
    défilent horizontalement ;
  - sous 480 px, les statistiques passent sur une colonne.
- `prefers-reduced-motion` désactive les animations pour les utilisateurs qui le demandent.

## 6. JavaScript

Le script `static/js/app.js` reste léger et sans dépendance.

### 6.1 Filtre de recherche instantané

- Filtre les lignes du tableau sur la référence et la désignation.
- Normalise les accents (`NFD`) et la casse : « rouleau », « ROULEAU » et
  « rouleaux » se recherchent de la même manière.
- Met à jour le compteur d'articles visibles.
- Affiche un état « aucun résultat ».
- Bouton « Effacer » pour revenir à la liste complète.

### 6.2 Confirmation avant une sortie

- Le bouton « Enregistrer une sortie » ouvre une boîte `<dialog>` native.
- Le dialogue rappelle l'article, la quantité, le stock actuel et le stock après sortie.
- Si la quantité dépasse le stock, un avertissement s'affiche **mais la confirmation
  reste possible** : c'est le serveur qui refuse et affiche le message métier.
  On peut ainsi démontrer le contrôle serveur et capturer le message de refus.
- `requestSubmit(submitter)` conserve le `formaction` du bouton de sortie.
- Fermeture par « Annuler » ou par la touche Échap.

### 6.3 Messages de succès

- Les alertes de succès disparaissent après 7 secondes ou via le bouton de fermeture.
- Les erreurs, elles, restent affichées : l'utilisateur doit pouvoir les lire.

### 6.4 Amélioration progressive

Aucune validation ne dépend de JavaScript. Si le script est désactivé :

- le formulaire se soumet directement ;
- les quantités invalides sont refusées par Jakarta Validation ;
- les sorties excessives sont refusées par `StockService` ;
- les messages d'erreur Thymeleaf restent affichés.

## 7. Accessibilité et expérience utilisateur

- Libellés associés aux champs (`label for`), textes d'aide reliés par `aria-describedby`.
- Lien d'évitement absent volontairement (navigation très courte), mais structure
  `header` / `main` / `footer` et titres hiérarchisés.
- Dialogue natif : gestion du focus et de la touche Échap par le navigateur.
- Tableaux lisibles au clavier et défilement horizontal sur petit écran.
- Messages d'erreur explicites en français, sans jargon technique.
- Compteur de résultats en `aria-live="polite"`.

## 8. Vérifications réalisées

| Vérification | Résultat |
|---|---|
| Champs vides | messages sous les champs, aucune écriture |
| Référence en double | message « Cette référence existe déjà. » |
| Quantité 0 / négative / texte | refus serveur |
| Entrée 20 puis sortie 7 | stock 13 |
| Sortie 15 avec stock 13 | refus, stock conservé à 13 |
| Article sans mouvement | stock 0 |
| Redémarrage de l'application | données MySQL conservées |
| JavaScript désactivé | validations serveur toujours actives |

Les captures correspondantes sont dans `docs/captures/`.

## 9. Ce que nous avons volontairement écarté

- Une bibliothèque CSS ou un framework JavaScript : inutile pour trois pages,
  et cela aurait masqué le travail HTML/CSS demandé.
- Un tableau de bord séparé : les indicateurs sont intégrés à la liste pour éviter
  un clic supplémentaire.
- Une animation d'entrée sur chaque carte : trop de mouvement pour une application
  de gestion.

## 10. Fichiers concernés

| Fichier | Rôle |
|---|---|
| `templates/fragments/layout.html` | structure commune, navigation, alertes, pied de page |
| `templates/articles/liste.html` | indicateurs, recherche, tableau |
| `templates/articles/formulaire.html` | création d'article |
| `templates/stock/fiche.html` | stock, mouvements, historique, dialogue |
| `templates/error.html` | page 404 / erreur |
| `static/css/app.css` | palette, composants, responsive |
| `static/js/app.js` | filtre, confirmation, messages |
