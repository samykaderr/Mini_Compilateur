# Mini-Compilateur Try-Catch

Ce document explique le fonctionnement de l'interface graphique du mini-compilateur Try-Catch.

## Composants de l'interface

L'interface principale (`CompilerGUI.java`) est une fenêtre Swing qui se divise en plusieurs parties :

1.  **Zone de Code (`codeTextArea`)**
    *   C'est une grande zone de texte (`JTextArea`) située dans la partie supérieure de la fenêtre.
    *   Les utilisateurs peuvent y écrire, coller ou charger du code JavaScript à analyser.

2.  **Console (`consoleTextArea`)**
    *   Située dans la partie inférieure, cette zone de texte n'est pas modifiable (`setEditable(false)`).
    *   Elle sert à afficher les résultats de l'analyse :
        *   Messages de succès si le code est syntaxiquement correct.
        *   Messages d'erreur détaillés (type d'erreur, ligne, colonne) si une `ParseException` est levée par l'analyseur.
        *   Autres messages d'erreur (par exemple, si un fichier ne peut pas être lu).
    *   **Redirection de la sortie** : La sortie standard (`System.out`) et la sortie d'erreur (`System.err`) de Java sont redirigées vers cette zone de texte grâce à la classe interne `CustomOutputStream`. Tout ce qui est normalement imprimé dans la console du terminal apparaîtra ici.

3.  **Panneau de Boutons**
    *   Un ensemble de boutons (`JButton`) permet à l'utilisateur de lancer des actions.

## Actions des Boutons

1.  **`Tester avec un fichier...` (`testFileButton`)**
    *   **Action** : Ouvre une boîte de dialogue (`JFileChooser`) qui permet à l'utilisateur de naviguer dans son système de fichiers et de sélectionner un fichier.
    *   **Logique (`chooseAndTestFile` method)** :
        1.  Le contenu du fichier sélectionné est lu.
        2.  Le texte lu est affiché dans la `codeTextArea`.
        3.  Un message indiquant le nom du fichier testé est affiché dans la `consoleTextArea`.
        4.  La méthode `analyzeCode` est appelée pour analyser le contenu du fichier.
        5.  En cas d'erreur de lecture du fichier, un message d'erreur est affiché dans la console.

2.  **`Tester Fichier Défaut` (`testDefaultButton`)**
    *   **Action** : Permet de tester rapidement un fichier d'exemple sans avoir à le chercher manuellement.
    *   **Logique (`testDefaultFile` method)** :
        1.  Le chemin du fichier par défaut est défini sur `"src/test/input.js"`.
        2.  Le programme vérifie si le fichier existe à cet emplacement. Sinon, un message d'erreur est affiché.
        3.  Si le fichier existe, son contenu est lu et chargé dans la `codeTextArea`.
        4.  La console est mise à jour pour indiquer que le test est effectué avec le fichier par défaut.
        5.  La méthode `analyzeCode` est appelée pour effectuer l'analyse.

3.  **`Tester le code ci-dessus` (`testCodeButton`)**
    *   **Action** : Analyse le code qui se trouve actuellement dans la `codeTextArea`.
    *   **Logique (`testEditorCode` method)** :
        1.  Récupère le texte de la `codeTextArea`.
        2.  Vérifie si la zone de texte n'est pas vide. Si c'est le cas, une boîte de dialogue d'avertissement apparaît.
        3.  Affiche un message dans la console pour indiquer que l'analyse provient de l'éditeur.
        4.  Appelle la méthode `analyzeCode` pour analyser le code.

4.  **`Effacer` (`clearButton`)**
    *   **Action** : Nettoie l'interface.
    *   **Logique (`clearFields` method)** :
        1.  Le contenu de la `codeTextArea` est effacé.
        2.  Le contenu de la `consoleTextArea` est effacé.

## Mécanisme d'Analyse (`analyzeCode` method)

C'est la méthode centrale qui orchestre l'analyse du code source.

1.  Elle prend une chaîne de caractères (`String code`) en entrée.
2.  Elle instancie le `Lexer` avec ce code pour le transformer en une liste de jetons (tokens).
3.  Elle instancie le `TryCatchParser` avec le lexer.
4.  Elle appelle la méthode principale de l'analyseur (`parser.anlysertoust()`).
5.  **Gestion des erreurs** :
    *   Un bloc `try...catch` entoure l'appel à l'analyseur.
    *   Si `anlysertoust()` se termine sans exception, un message de succès est imprimé dans la console.
    *   Si une `ParseException` est attrapée, cela signifie qu'une erreur de syntaxe a été trouvée. Un message d'erreur formaté, incluant le message de l'exception, la ligne et la colonne, est imprimé dans la console d'erreur (`System.err`), qui s'affiche dans la `consoleTextArea`.
    *   Un `catch` générique pour `Exception` est aussi présent pour gérer toute autre erreur inattendue pendant le processus.
