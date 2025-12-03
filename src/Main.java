
import Lexer.*;
import Parser.TryCatchParser;
import Parser.ParseException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        // Si aucun argument, utiliser le fichier de test par défaut
        if (args.length == 0) {
            System.out.println("Aucun fichier fourni. Passage au fichier de test 'test\\input.js'.");
            args = new String[] { "test\\input.js" };
        }

        // Lire et analyser le fichier fourni (ou le fichier de test par défaut)
        try {
            Path path = Paths.get(args[0]);
            if (!Files.exists(path)) {
                System.err.println("Fichier introuvable : " + path);
                return;
            }

            // Créer le lexer
            Lexer lexer;


            // Re-créer un lexer pour le parser (car getAllTokens aurait consommé tout)
            lexer = new Lexer(new String(Files.readAllBytes(path)));

            TryCatchParser parser = new TryCatchParser(lexer);
            try {
                parser.anlysertoust();
                System.out.println("Analyse réussie: aucune erreur de syntaxe try/catch détectée.");
            } catch (ParseException pe) {
                System.err.printf("Erreur de syntaxe: %s (ligne %d, colonne %d)\n", pe.getMessage(), pe.getLine(), pe.getColumn());
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de l'analyse : " + e.getMessage());
            e.printStackTrace();
        }
    }
}

