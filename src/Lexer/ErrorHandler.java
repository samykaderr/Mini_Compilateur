package Lexer;

/**
 * Simple gestionnaire d'erreurs pour le lexer.
 * Fournir des méthodes utilitaires pour afficher des erreurs
 * et des explications destinées à l'utilisateur.
 */
public class ErrorHandler {
    // erreur position (ligne/colonne)
    public static void reportError(String message, int line, int column) {
        System.err.printf("Erreur (ligne %d, colonne %d): %s%n", line, column, message);
    }

    // Affiche une exception inattendue (pile d'appels) pour le debug
    public static void reportException(Exception e) {
        System.err.println("Exception inattendue: " + e.getMessage());
        e.printStackTrace(System.err);
    }

    // Affiche une courte explication ou conseil à l'utilisateur
    public static void explain(String shortMessage) {
        System.out.println("Explication: " + shortMessage);
    }
}
