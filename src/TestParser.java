import Lexer.Lexer;
import Parser.TryCatchParser;
import Parser.ParseException;
import Parser.ErrorHandler;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestParser {
    public static void main(String[] args) throws Exception {
        String path = (args.length == 0) ? "src/test/input.js" : args[0];
        String content = new String(Files.readAllBytes(Paths.get(path)));
        Lexer lexer = new Lexer(content);
        TryCatchParser parser = new TryCatchParser(lexer);
        try {
            parser.anlysertoust();
            System.out.println("Aucune erreur de syntaxe try/catch détectée.");
        } catch (ParseException pe) {
            // Utiliser le nouveau ErrorHandler pour afficher des informations enrichies
            System.err.printf("SyntaxError: %s at line %d, column %d%n", pe.getMessage(), pe.getLine(), pe.getColumn());

            // Afficher la ligne source correspondante et une caret '^' sous la colonne reportée
            String[] lines = content.split("\\r?\\n", -1);
            int lineIndex = Math.max(0, Math.min(lines.length - 1, pe.getLine() - 1));
            String errorLine = lines[lineIndex];
            System.err.println(errorLine);
            // Construire la caret (prendre en compte colonne minimale = 1)
            int caretPos = Math.max(1, pe.getColumn());
            StringBuilder caret = new StringBuilder();
            for (int i = 1; i < caretPos; i++) {
                // si la position dépasse la longueur de la ligne, on place caret à la fin
                caret.append(i <= errorLine.length() ? (errorLine.charAt(i-1) == '\t' ? '\t' : ' ') : ' ');
            }
            caret.append('^');
            System.err.println(caret);

            // Détails : message complet
            System.err.println("Détails : " + pe.getMessage());

            // Diagnostic/contextual explanation via Parser.ErrorHandler
            ErrorHandler.diagnose(pe);
        }
    }
}
