# Mini Compilateur de Blocs Try-Catch

Ce projet est un mini-compilateur développé en Java qui se concentre sur l'analyse lexicale et syntaxique des structures `try-catch-finally` dans un code source de type JavaScript. Il est doté d'une interface graphique (GUI) simple construite avec Java Swing pour faciliter les tests.

## ✨ Fonctionnalités

- **Analyse Lexicale** : Le code source est d'abord découpé en une série de jetons (tokens) par un analyseur lexical (Lexer).
- **Analyse Syntaxique** : Les jetons sont ensuite analysés par un analyseur syntaxique (Parser) pour vérifier si la structure des blocs `try-catch-finally` est correcte.
- **Interface Graphique (GUI)** : Une interface utilisateur simple pour interagir avec le compilateur.
  - **Tester depuis un fichier** : Chargez un fichier `.js` directement dans l'application via un sélecteur de fichiers.
  - **Tester du code en direct** : Écrivez ou collez votre code directement dans une zone de texte.
  - **Console intégrée** : Affiche les messages de succès ou les erreurs de syntaxe détaillées (avec ligne et colonne) en temps réel.

## 🛠️ Technologies utilisées

- **Langage** : Java
- **Interface Graphique** : Java Swing
- **Gestion de projet** : IntelliJ IDEA (structure de projet)

## 📂 Structure du Projet

```
.
├── src/
│   ├── GUI/
│   │   └── CompilerGUI.java      # Fenêtre principale de l'interface graphique
│   ├── Lexer/
│   │   ├── Lexer.java            # Analyseur lexical (découpe le code en jetons)
│   │   ├── Token.java            # Représente un jeton (ex: 'try', '{', etc.)
│   │   └── TokenType.java        # Énumération des différents types de jetons
│   ├── Parser/
│   │   ├── TryCatchParser.java   # Analyseur syntaxique (vérifie la grammaire)
│   │   └── ParseException.java   # Exception personnalisée pour les erreurs de syntaxe
│   ├── Main.java                 # Point d'entrée de l'application (lance la GUI)
│   └── test/
│       └── input.js              # Un fichier d'exemple pour les tests
└── ...
```

## 🚀 Comment lancer le projet

### Prérequis

- JDK (Java Development Kit) 8 ou supérieur.
- Un IDE comme IntelliJ IDEA ou Eclipse (recommandé).

### Depuis un IDE (IntelliJ IDEA)

1.  Clonez ce dépôt : `git clone <url-du-repo>`
2.  Ouvrez le projet dans IntelliJ IDEA.
3.  L'IDE devrait détecter automatiquement la configuration du projet.
4.  Localisez le fichier `src/Main.java`.
5.  Faites un clic droit sur le fichier et sélectionnez **"Run 'Main.main()'"**.
6.  L'interface graphique du compilateur devrait se lancer.

### Utilisation de l'interface

1.  Cliquez sur **"Tester avec un fichier..."** pour ouvrir un sélecteur de fichiers et choisir un fichier `.js` à analyser.
2.  Ou bien, écrivez/collez directement votre code dans la zone de texte de gauche.
3.  Cliquez sur **"Tester le code ci-dessus"**.
4.  Les résultats de l'analyse s'afficheront dans la console en bas de la fenêtre.
5.  Utilisez le bouton **"Effacer"** pour vider les zones de texte.

