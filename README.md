# Safety Net Alerts - Documentation Technique

## Table des matières

1. [Architecture Générale](#architecture-générale)
2. [Composants Principaux](#composants-principaux)
3. [Flux de Données](#flux-de-données)
4. [Interface Utilisateur](#interface-utilisateur)
5. [Points d'Intérêt Technique](#points-dintérêt-technique)

## Architecture Générale

L'application Safety Net Alerts est une application Spring Boot qui gère les informations relatives aux casernes de pompiers et aux personnes qu'elles couvrent. Elle suit une architecture en couches :

- **Controller** : Gère les requêtes HTTP et la communication avec le client
- **Service** : Contient la logique métier
- **Repository** : Gère l'accès aux données
- **Model** : Définit les entités de données
- **DTO** : Objets de transfert de données

## Composants Principaux

### 1. FireStationController

```java
@RestController
@RequestMapping("/firestation")
public class FireStationController {
    // ...
}
```

- **Rôle** : Point d'entrée REST pour les requêtes liées aux casernes de pompiers

- **Endpoints** :
  - `GET /firestation?stationNumber={numero}` : Liste des personnes couvertes par une station
  - `GET /firestation/debug-stations` : Informations de débogage sur toutes les stations

### 2. DataRepository

```java
@Component
public class DataRepository {
    @Value("${data.file.path:classpath:data.json}")
    private String dataFilePath;
    // ...
}
```

- **Rôle** : Gestion de la persistance des données
- **Fonctionnalités** :
  - Chargement des données depuis un fichier JSON
  - Sauvegarde des données
  - Gestion des erreurs de chargement

### 3. AlertService

- **Rôle** : Logique métier pour le traitement des alertes
- **Fonctionnalités principales** :
  - Calcul de l'âge des personnes
  - Filtrage des personnes par station
  - Comptage des adultes et enfants

### 4. Modèles de Données

- **Person** : Informations personnelles (nom, prénom, adresse, etc.)
- **FireStation** : Informations sur les casernes (numéro, adresses couvertes)
- **MedicalRecord** : Dossiers médicaux (date de naissance, médicaments, allergies)
- **Data** : Conteneur principal regroupant toutes les données

### 5. DTOs (Data Transfer Objects)

- **PersonDTO** : Transfert des informations personnelles
- **FireStationDTO** : Transfert des informations de station avec compteurs

## Flux de Données

1. **Chargement Initial** :

   ```java
   @PostConstruct
   public void loadData() {
       // Chargement depuis data.json
   }
   ```

2. **Requête Client** :

   ```javascript
   fetch(`/firestation?stationNumber=${stationNumber}`)
   ```

3. **Traitement** :

   ```java
   @GetMapping
   public ResponseEntity<FireStationDTO> getPersonsCoveredByStation(@RequestParam int stationNumber) {
       FireStationDTO response = alertService.getPersonsCoveredByStation(stationNumber);
       // Logging et traitement
       return ResponseEntity.ok(response);
   }
   ```

4. **Réponse** :

   ```javascript
   .then(data => {
       // Affichage dans le tableau HTML
   })
   ```

## Interface Utilisateur

### Structure HTML

```html
<div class="test-section">
    <h2>Tester l'API FireStation</h2>
    <!-- Formulaire de test -->
    <div id="result">
        <!-- Tableau des résultats -->
    </div>
    <div id="debug">
        <!-- Informations de débogage -->
    </div>
</div>
```

### Fonctionnalités Frontend

1. **Test de Station** :
   - Saisie du numéro de station
   - Affichage des résultats dans un tableau
   - Compteurs d'adultes et d'enfants

2. **Débogage** :
   - Affichage des données brutes
   - Informations sur toutes les stations
   - Logs détaillés

## Points d'Intérêt Technique

### 1. Gestion des Données

- Utilisation de Jackson pour la sérialisation/désérialisation JSON
- Chargement flexible depuis classpath ou système de fichiers
- Gestion des erreurs robuste

### 2. Logging

```java
private final Logger logger = LoggerFactory.getLogger(FireStationController.class);
```

- Logging détaillé à chaque étape
- Traçabilité des données
- Débogage facilité

### 3. Sécurité

- Validation des entrées
- Gestion des erreurs HTTP
- Protection contre les données invalides

### 4. Performance

- Utilisation de streams Java 8
- Optimisation des requêtes
- Mise en cache des données en mémoire

## Bonnes Pratiques Implémentées

1. **Séparation des Responsabilités**
   - Controllers pour la couche présentation
   - Services pour la logique métier
   - Repository pour l'accès aux données

2. **Gestion des Erreurs**
   - Exceptions personnalisées
   - Logging approprié
   - Réponses HTTP appropriées

3. **Documentation**
   - JavaDoc complet
   - Code auto-documenté
   - Interface utilisateur intuitive

4. **Maintenabilité**
   - Code modulaire
   - Tests facilités
   - Configuration externalisée 