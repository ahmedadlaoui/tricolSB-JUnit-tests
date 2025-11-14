# Tests du Service de Stock et FIFO - Résumé

## Vue d'ensemble

Ce document décrit les tests unitaires développés pour la couche Service qui gère le mécanisme de sortie de stock FIFO (First-In-First-Out) dans l'application Tricol-V2-SB.

## Dépendances vérifiées ✅

Le projet utilise les dépendances suivantes pour les tests :

- **JUnit 5 (Jupiter)** : Version 5.12.2
  - `junit-jupiter-api`
  - `junit-jupiter-params`
  - `junit-jupiter-engine`
  
- **Mockito** : Version 5.11.0
  - `mockito-core`
  - `mockito-junit-jupiter`

- **Spring Boot Test** : Inclut AssertJ et autres outils de test

## Fichiers créés

### 1. `StockServiceTest.java`
**Emplacement** : `src/test/java/com/example/tricolv2sb/Service/StockServiceTest.java`

**Objectif** : Tests unitaires pour les opérations de base du service de stock

**Tests implémentés** (7 tests) :
- ✅ Récupération du stock global pour tous les produits
- ✅ Récupération des détails de stock avec ordre FIFO des lots
- ✅ Gestion d'exception quand un produit n'existe pas
- ✅ Calcul de la valorisation totale du stock
- ✅ Récupération des alertes de stock (sous seuil de réapprovisionnement)
- ✅ Gestion des alertes vides
- ✅ Gestion des produits sans lots de stock

### 2. `GoodsIssueServiceFIFOTest.java`
**Emplacement** : `src/test/java/com/example/tricolv2sb/Service/GoodsIssueServiceFIFOTest.java`

**Objectif** : Tests unitaires pour l'algorithme FIFO de sortie de stock

**Tests implémentés** (9 tests) :

#### A. Mécanisme de Sortie de Stock FIFO

##### ✅ **Scénario 1 : Sortie simple consommant partiellement un seul lot**

**État initial** :
- LOT-001 : 100 unités @ 10.00€ (plus ancien, entrée 01/01/2025)

**Action** :
- Sortie de 30 unités

**Résultat attendu** :
- LOT-001 : 70 unités restantes (100 - 30)
- 1 mouvement de stock créé (OUT, 30 unités depuis LOT-001)
- FIFO respecté (lot le plus ancien consommé en premier)

**Vérifications** :
- ✅ Quantité restante du lot mise à jour correctement
- ✅ Mouvement de stock OUT créé avec la bonne quantité
- ✅ Mouvement lié au bon lot et à la ligne de sortie
- ✅ Statut de la sortie de marchandises passé à VALIDATED

---

##### ✅ **Scénario 2 : Sortie nécessitant la consommation de plusieurs lots successifs**

**État initial** :
- LOT-001 : 50 unités @ 10.00€ (plus ancien, entrée 01/01/2025)
- LOT-002 : 80 unités @ 12.00€ (moyen, entrée 15/01/2025)
- LOT-003 : 70 unités @ 11.50€ (plus récent, entrée 01/02/2025)

**Action** :
- Sortie de 120 unités

**Résultat attendu** :
- LOT-001 : 0 unités restantes (50 consommées - épuisé)
- LOT-002 : 10 unités restantes (70 consommées sur 80)
- LOT-003 : 70 unités restantes (0 consommées - pas atteint)
- 2 mouvements de stock créés :
  1. OUT 50 unités depuis LOT-001 (plus ancien consommé en premier)
  2. OUT 70 unités depuis LOT-002 (deuxième plus ancien)

**Vérifications** :
- ✅ Consommation dans l'ordre chronologique (FIFO)
- ✅ Premier lot complètement épuisé
- ✅ Deuxième lot partiellement consommé
- ✅ Troisième lot non touché
- ✅ Deux mouvements de stock créés dans le bon ordre
- ✅ Quantités correctes pour chaque mouvement

---

#### B. Scénarios supplémentaires

##### ✅ **Scénario 3 : Consommation exacte d'un lot (respectant le seuil de réapprovisionnement)**
- Test de consommation avec respect du point de réapprovisionnement

##### ✅ **Scénario 4 : Consommation épuisant plusieurs lots exactement**
- Test d'épuisement exact de multiples lots

#### C. Gestion des erreurs

##### ✅ **Erreur : Stock insuffisant**
- Vérification de l'exception levée quand le stock disponible est insuffisant
- Message d'erreur contenant les quantités requises et disponibles

##### ✅ **Erreur : Stock en dessous du seuil de réapprovisionnement**
- Vérification que la sortie est bloquée si elle réduit le stock sous le seuil
- Message d'erreur contenant le point de réapprovisionnement et le stock résultant

##### ✅ **Erreur : Sortie de marchandises introuvable**
- Exception levée pour ID inexistant

##### ✅ **Erreur : Validation d'une sortie non-DRAFT**
- Seules les sorties en statut DRAFT peuvent être validées

##### ✅ **Erreur : Validation sans lignes de sortie**
- Une sortie doit contenir au moins une ligne

## Résultats des tests

```
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Détails :
- **StockServiceTest** : 7 tests passés ✅
- **GoodsIssueServiceFIFOTest** : 9 tests passés ✅
- **Total** : 16 tests passés avec succès

## Technologies et bonnes pratiques utilisées

### Frameworks de test
- **JUnit 5** : Framework de test moderne avec annotations `@Test`, `@DisplayName`, `@BeforeEach`
- **Mockito** : Mocking des dépendances avec `@Mock`, `@InjectMocks`
- **AssertJ** : Assertions fluides et lisibles

### Patterns et techniques
1. **Arrange-Act-Assert (AAA)** : Structure claire des tests
2. **Mocking des dépendances** : Isolation complète de la couche Service
3. **ArgumentCaptor** : Vérification des objets passés aux méthodes mockées
4. **Tests paramétrés** : Documentation complète dans les javadoc
5. **Assertions précises** : Vérification de tous les aspects critiques

### Couverture de test
- ✅ Chemins nominaux (happy path)
- ✅ Cas limites (edge cases)
- ✅ Gestion d'erreurs
- ✅ Algorithme FIFO
- ✅ Calculs de valorisation
- ✅ Contraintes métier (seuil de réapprovisionnement)

## Comment exécuter les tests

### Tous les tests de stock
```bash
mvn test -Dtest="StockServiceTest,GoodsIssueServiceFIFOTest"
```

### Tests du service de stock uniquement
```bash
mvn test -Dtest=StockServiceTest
```

### Tests FIFO uniquement
```bash
mvn test -Dtest=GoodsIssueServiceFIFOTest
```

### Tous les tests du projet
```bash
mvn test
```

## Structure des tests

```
src/test/java/com/example/tricolv2sb/Service/
├── StockServiceTest.java           # Tests du service de stock
└── GoodsIssueServiceFIFOTest.java  # Tests de l'algorithme FIFO
```

## Prochaines étapes suggérées

1. **Tests d'intégration** : Tester avec une base de données réelle
2. **Tests de performance** : Vérifier les performances avec de gros volumes
3. **Tests de concurrence** : Vérifier le comportement multi-thread
4. **Couverture de code** : Exécuter avec JaCoCo pour mesurer la couverture

## Notes importantes

- Les tests utilisent des mocks pour isoler la couche Service
- Tous les tests sont indépendants et peuvent s'exécuter dans n'importe quel ordre
- Les tests vérifient le comportement, pas l'implémentation
- Les messages d'erreur utilisent le formatage de nombres selon la locale (virgule comme séparateur décimal)

---

**Date de création** : 12 novembre 2025  
**Framework** : Spring Boot 3.5.7  
**Java Version** : 17
