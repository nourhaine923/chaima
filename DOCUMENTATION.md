# Documentation du Projet BeautyManager (Unifié)

Ce document détaille la structure du projet unifié, les dépendances nécessaires et les prochaines étapes pour le développement de l'application mobile Android.

## 1. Structure du Projet

Le projet a été unifié sous le package `com.example.beautymanager` et organisé autour des fonctionnalités clés du Cahier des Charges.

| Fichier/Dossier | Origine | Fonctionnalité |
| :--- | :--- | :--- |
| `MainActivity.java` | `BeautyManager.rar` | Point d'entrée initial, redirige vers `DashboardActivity`. |
| `DashboardActivity.java` | `BeautyManager.rar` (modifié) | **Tableau de Bord** principal, lie toutes les fonctionnalités. |
| `GestionRendezVousActivity.java` | **Nouveau** | **Gestion des Rendez-vous** (Fonctionnalité manquante). |
| `FacturationActivity.java` | `eya.zip` | **Gestion de la Facturation**. |
| `GestionPrestationsActivity.java` | `eya.zip` | **Gestion des Prestations**. |
| `ManageInventoryActivity.java` | `BeautyManager.rar` | **Gestion du Stock**. |
| `PreferencesClientActivity.java` | `eya.zip` | **Gestion des Clients/Préférences**. |
| `RendezVous.java` | **Nouveau** | Modèle de données pour les Rendez-vous. |
| `Product.java` | `BeautyManager.rar` | Modèle de données pour le Stock. |
| `Facture.java`, `Prestation.java`, `Cliente.java`, etc. | `eya.zip` | Modèles de données pour les autres modules. |

## 2. Dépendances Nécessaires (Fichier `app/build.gradle.kts`)

Pour exécuter le projet, les dépendances suivantes sont cruciales et ont été ajoutées au fichier `app/build.gradle.kts` :

### A. Firebase

Le projet utilise Firebase pour la base de données (Firestore) et l'authentification.

```kotlin
implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
implementation("com.google.firebase:firebase-auth")
implementation("com.google.firebase:firebase-firestore")
```

### B. Composants UI et Graphiques

Les composants Android de base et la librairie de graphiques sont nécessaires pour les interfaces existantes.

```kotlin
// Composants UI
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("com.google.android.material:material:1.11.0")
implementation("androidx.constraintlayout:constraintlayout:2.1.4")
implementation("androidx.recyclerview:recyclerview:1.3.2")
implementation("androidx.cardview:cardview:1.0.0")

// Graphiques (pour DashboardActivity)
implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
```

**Note Importante :** Le plugin `com.google.gms.google-services` doit être appliqué dans les fichiers `build.gradle.kts` de niveau projet et de niveau module.

## 3. Prochaines Étapes (Liaison Firebase)

La prochaine étape consiste à remplacer les données de démonstration (mock data) dans chaque activité par des appels réels à Firebase Firestore.

1.  **Authentification :** Implémenter une activité de connexion/enregistrement pour la gérante (mono-utilisateur).
2.  **Liaison des Données :**
    *   `GestionPrestationsActivity` : Lire/Écrire les `Prestation` dans Firestore.
    *   `ManageInventoryActivity` : Lire/Écrire les `Product` dans Firestore.
    *   `FacturationActivity` : Lire/Écrire les `Facture` dans Firestore.
    *   `PreferencesClientActivity` : Lire/Écrire les `Cliente` dans Firestore.
    *   `GestionRendezVousActivity` : Lire/Écrire les `RendezVous` dans Firestore.

Je suis prêt à commencer l'implémentation de la liaison Firebase pour chaque module.
