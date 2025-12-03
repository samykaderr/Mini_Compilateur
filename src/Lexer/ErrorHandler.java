package Lexer;

/**
 * Simple gestionnaire d'erreurs pour le lexer.
 * Fournir des méthodes utilitaires pour afficher des erreurs
 * et des explications destinées à l'utilisateur.
 */
public class ErrorHandler {
    // signaler une erreur avec precision  position (ligne/colonne)
    public static void reportError(String message, int line, int column) {
        System.err.printf("Erreur (ligne %d, colonne %d): %s%n", line, column, message);
    }
}

