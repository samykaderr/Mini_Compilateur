package Parser;

import java.util.Locale;

/**
 * Simple gestionnaire d'erreurs pour le parser.
 * Fournit des méthodes utilitaires pour afficher des erreurs
 * et produire une courte explication/diagnostic destiné à l'utilisateur.
 */
public class ErrorHandler {
    // Affiche une erreur avec position
    public static void reportError(String message, int line, int column) {
        System.err.printf("Erreur de parsing (ligne %d, colonne %d): %s%n", line, column, message);
    }

    // Affiche une explication/diagnostic succinct basé sur le message d'erreur
    public static void diagnose(ParseException pe) {
        String msg = pe.getMessage().toLowerCase(Locale.ROOT);
        if (msg.contains("attendu") && msg.contains("catch")) {
            System.out.println("Explication: Après un bloc 'try', un 'catch' est attendu. Vérifiez la présence de 'catch (e) { ... }'.");
            return;
        }
        if (msg.contains("paramètre de 'catch' manquant") || msg.contains("identifiant")) {
            System.out.println("Explication: Le catch doit recevoir un identifiant entre parenthèses, par exemple: catch (e) { ... }");
            return;
        }
        if (msg.contains("bloc non fermé") || msg.contains("demarré")) {
            System.out.println("Explication: Un '{' n'a pas été fermé par un '}'. Vérifiez les accolades imbriquées à proximité.");
            return;
        }
        if (msg.contains("bloc attendu")) {
            System.out.println("Explication: Un bloc (accolades) était attendu ici, commencez par '{' pour ouvrir un bloc.");
            return;
        }
        if (msg.contains("erreur lexicale") || msg.contains("symbole inconnu") || msg.contains("chaîne non terminée") || msg.contains("commentaire de bloc non fermé")) {
            System.out.println("Explication: Il s'agit d'une erreur lexicale — vérifiez les chaînes, commentaires ou symboles invalides.");
            return;
        }

        // Par défaut, afficher une explication générique
        System.out.println("Explication: Vérifiez la syntaxe à l'endroit indiqué. Message: " + pe.getMessage());
    }

    // Affiche une courte explication libre
    public static void explain(String shortMessage) {
        System.out.println("Explication: " + shortMessage);
    }

    // Affiche une exception inattendue (pile d'appels) pour le debug
    public static void reportException(Exception e) {
        System.err.println("Exception inattendue dans le parser: " + e.getMessage());
        e.printStackTrace(System.err);
    }
}
