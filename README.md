# Mini Compilateur Try-Catch pour JavaScript

Ce projet est un mini compilateur conçu pour analyser la syntaxe des blocs `try-catch` dans du code JavaScript. Il comprend une analyse lexicale et une analyse syntaxique. Une interface graphique simple en Java Swing permet de tester le compilateur facilement.

## Fonctionnalités

-   **Analyseur Lexical** : Transforme le code source en une séquence de jetons (tokens).
-   **Analyseur Syntaxique** : Vérifie si la séquence de jetons respecte la grammaire des blocs `try-catch`.
-   **Interface Graphique (GUI)** : Permet d'interagir avec le compilateur de manière conviviale.
    -   Tester du code directement depuis une zone de texte.
    -   Tester le code depuis un fichier local.
    -   Tester rapidement un fichier d'exemple par défaut.
    -   Afficher les erreurs de syntaxe dans une console intégrée.

## Comment utiliser

### Prérequis

-   Java Development Kit (JDK) 8 ou supérieur.

### Lancement de l'application

1.  Compilez le projet. Si vous utilisez un IDE comme IntelliJ IDEA ou Eclipse, vous pouvez généralement le faire via un menu "Build" ou "Run".
2.  Exécutez la classe `Main.java` qui se trouve dans le package `src`.

Cela lancera l'interface graphique du compilateur.

### Utilisation de l'interface

L'interface se compose de trois parties principales :

1.  **Zone de code** : C'est ici que vous pouvez écrire ou coller le code JavaScript que vous souhaitez analyser.
2.  **Boutons d'action** :
    -   `Tester avec un fichier...` : Ouvre une boîte de dialogue pour sélectionner un fichier `.js` sur votre ordinateur. Le contenu du fichier sera chargé dans la zone de code et analysé.
    -   `Tester Fichier Défaut` : Charge et analyse automatiquement le fichier `src/test/input.js` inclus dans le projet.
    -   `Tester le code ci-dessus` : Analyse le code actuellement présent dans la zone de texte.
    -   `Effacer` : Vide la zone de code et la console.
3.  **Console** : Affiche les résultats de l'analyse.
    -   Si le code est correct, un message de succès s'affichera.
    -   En cas d'erreur de syntaxe, un message détaillé indiquera le type d'erreur, la ligne et la colonne où elle s'est produite.

## Structure du projet

-   `src/GUI/CompilerGUI.java` : Contient le code de l'interface graphique Swing.
-   `src/Lexer/` : Contient les classes pour l'analyse lexicale (`Lexer.java`, `Token.java`, etc.).
-   `src/Parser/` : Contient les classes pour l'analyse syntaxique (`TryCatchParser.java`, `ParseException.java`).
-   `src/test/input.js` : Un fichier d'exemple pour tester le compilateur.
-   `src/Main.java` : Le point d'entrée de l'application qui lance l'interface graphique.

