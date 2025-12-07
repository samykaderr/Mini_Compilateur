import Lexer.Lexer;
import Lexer.Token;
import Lexer.TokenType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TestLexer {
    public static void main(String[] args) throws Exception {
        String path = (args.length == 0) ? "src/test/input.js" : args[0];
        String content = new String(Files.readAllBytes(Paths.get(path)));
        Lexer lexer = new Lexer(content);
        List<Token> tokens = lexer.getAllTokens();
        for (Token t : tokens) {
            System.out.printf("%s -> '%s' (line %d, col %d)%n", t.getType(), t.getValue(), t.getLine(), t.getColumn());
        }
    }
}

