# Documentation du Projet BeautyManager (Application Android)

## 1. Introduction

Ce document présente la documentation technique de l'application mobile **BeautyManager**, développée pour la gérante d'un salon de coiffure. L'application unifie deux codes sources partiels fournis initialement et intègre la fonctionnalité manquante de **Gestion des Rendez-vous**, tout en assurant la persistance des données via **Firebase Firestore** et l'authentification de l'utilisateur.

## 2. Architecture Technique

*   **Plateforme :** Android (Développement avec Android Studio)
*   **Langages :** Java et Kotlin (pour les composants existants et l'unification)
*   **Base de Données :** Google Firebase Firestore (NoSQL Cloud Database)
*   **Authentification :** Google Firebase Authentication (pour la gérante mono-utilisateur)
*   **Dépendances Clés :**
    *   `com.google.firebase:firebase-auth`
    *   `com.google.firebase:firebase-firestore`
    *   `androidx.appcompat:appcompat`
    *   `androidx.recyclerview:recyclerview`
    *   `com.github.PhilJay:MPAndroidChart` (pour les graphiques du tableau de bord)

## 3. Dépendances Nécessaires (build.gradle.kts - Module: app)

Pour que le projet soit exécutable, les dépendances suivantes doivent être présentes dans le fichier `app/build.gradle.kts` (ou `app/build.gradle` si vous utilisez Groovy) :

```kotlin
dependencies {
    // ... autres dépendances ...

    // Firebase
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // UI et Utilitaires
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Graphiques (MPAndroidChart)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Kotlin (si non déjà inclus)
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
}
```

**Note Importante :** Vous devez également ajouter le plugin Google Services (`id("com.google.gms.google-services")`) dans `app/build.gradle.kts` et le classpath correspondant dans `build.gradle.kts` (Project) pour que Firebase fonctionne. Le fichier `google-services.json` doit être placé dans le répertoire `app/`.

## 4. Structure du Code et Liens entre les Interfaces

Le point d'entrée de l'application est `LoginActivity.java` (ou `MainActivity.java` qui redirige vers `LoginActivity.java`), qui gère l'authentification.

| Activité | Rôle | Liaison avec Firebase | Liens Sortants |
| :--- | :--- | :--- | :--- |
| `LoginActivity.java` | Authentification de la gérante. | **Firebase Auth** (Connexion/Inscription). | `DashboardActivity.java` |
| `DashboardActivity.java` | Tableau de bord principal. | Aucun (Lecture des données pour les graphiques). | `GestionRendezVousActivity.java`, `GestionPrestationsActivity.java`, `ManageInventoryActivity.java`, `PreferencesClientActivity.java`, `FacturationActivity.java` |
| `GestionRendezVousActivity.java` | **NOUVEAU** : Gestion des rendez-vous. | **Firestore** (Collection `rendezvous`: CRUD). | Aucun (Gestion interne). |
| `GestionPrestationsActivity.java` | Gestion des services/prestations. | **Firestore** (Collection `prestations`: CRUD). | Aucun (Gestion interne). |
| `ManageInventoryActivity.java` | Gestion du stock/inventaire. | **Firestore** (Collection `products`: CRUD). | Aucun (Gestion interne). |
| `PreferencesClientActivity.java` | Gestion de la base de données clients. | **Firestore** (Collection `clientes`: CRUD). | Aucun (Gestion interne). |
| `FacturationActivity.java` | Gestion des factures. | **Firestore** (Collection `factures`: CRUD). | Aucun (Gestion interne). |

**Liaison avec la Base de Données (CRUD)**

Chaque module de gestion (`Prestations`, `Stock`, `Clients`, `Rendez-vous`, `Facturation`) utilise une structure similaire :
1.  **Modèle de Données :** Classes Java/Kotlin (`Prestation.java`, `Product.java`, `Cliente.java`, `RendezVous.java`, `Facture.java`) adaptées avec des constructeurs vides et des annotations `@Exclude` pour l'ID Firestore.
2.  **Chargement :** La méthode `loadXxx()` utilise `db.collection("xxx").get()` pour récupérer toutes les données.
3.  **Ajout :** La méthode `addXxx()` utilise `db.collection("xxx").add(data)` pour insérer une nouvelle entrée.
4.  **Mise à jour/Suppression :** Les méthodes `updateXxx()` et `deleteXxx()` utilisent `db.collection("xxx").document(id).set(data)` ou `.delete()`.

## 5. Étapes pour l'Exécution (À faire par l'utilisateur)

Pour exécuter l'application sans bugs, vous devez effectuer les étapes suivantes dans votre environnement Android Studio :

1.  **Créer le Projet :** Créez un nouveau projet Android et remplacez le contenu du répertoire `app/src/main/java/com/example/beautymanager` par les fichiers fournis.
2.  **Configuration Firebase :**
    *   Créez un projet sur la console Firebase.
    *   Ajoutez une application Android à ce projet.
    *   Téléchargez le fichier `google-services.json` et placez-le dans le répertoire `app/`.
    *   Activez **Firebase Authentication** (méthode Email/Password).
    *   Activez **Cloud Firestore** et créez les collections suivantes (sans documents initiaux, l'application les créera) : `prestations`, `products`, `clientes`, `rendezvous`, `factures`.
3.  **Règles Firestore :** Pour les tests, assurez-vous que vos règles Firestore sont ouvertes (ou sécurisées pour la gérante authentifiée) :

    ```
    rules_version = '2';
    service cloud.firestore {
      match /databases/{database}/documents {
        match /{document=**} {
          allow read, write: if request.auth != null;
        }
      }
    }
    ```
4.  **Exécution :** Synchronisez Gradle et exécutez l'application sur un émulateur ou un appareil physique.

L'application démarrera sur l'écran de connexion. Vous devrez vous inscrire une première fois (simulant l'inscription de la gérante) pour accéder au tableau de bord.

---
**Conclusion :** Le code est unifié, la fonctionnalité manquante est implémentée, et la liaison avec Firebase est établie pour tous les modules, respectant ainsi toutes les exigences de votre cahier des charges.
