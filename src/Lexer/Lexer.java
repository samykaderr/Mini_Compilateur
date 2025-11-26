package Lexer;

import java.io.*;
import java.util.*;

public class Lexer {
    private final String source;
    private int position = 0;
    private int line = 1;
    private int column = 1;

    public Lexer(String source) {
        this.source = source == null ? "" : source;
    }

    private boolean isAtEnd() {
        return position >= source.length();
    }

    private char peek() {
        return isAtEnd() ? '\0' : source.charAt(position);
    }

   // private char peekNext() {
   //     return (position + 1) >= source.length() ? '\0' : source.charAt(position + 1);
   // }

    private char advance() {
        char c = peek();
        position++;
        if (c == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        return c;
    }

    private void skipWhitespace() {
        while (!isAtEnd()) {
            char c = peek();
            if (c == ' ' || c == '\r' || c == '\t' || c == '\n') {
                advance();
            } else {
                break;
            }
        }
    }

    private final Map<String, TokenType> keywords = new HashMap<>();
    {
        keywords.put("let", TokenType.LET);
        keywords.put("var", TokenType.VAR);
        keywords.put("const", TokenType.CONST);
        keywords.put("function", TokenType.FUNCTION);
        keywords.put("return", TokenType.RETURN);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("do", TokenType.DO);
        keywords.put("for", TokenType.FOR);
        keywords.put("try", TokenType.TRY);
        keywords.put("catch", TokenType.CATCH);
        keywords.put("finally", TokenType.FINALLY);
        keywords.put("throw", TokenType.THROW);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("null", TokenType.NULL);
        keywords.put("undefined", TokenType.UNDEFINED);
        // 'console' => CONSOLE, 'log' => LOG
        keywords.put("console", TokenType.CONSOLE);
        keywords.put("log", TokenType.LOG);
        // personnalisés
        keywords.put("samy", TokenType.SAMY);
        keywords.put("achouche", TokenType.ACHOUCHE);

    }

    public Token nextToken() {
        skipWhitespace();
        if (isAtEnd()) return new Token(TokenType.EOF, "", line, column);

        int tokenLine = line;
        int tokenColumn = column;

        char c = advance();

        // Identifiants et mots-clés
        if (Character.isLetter(c) || c == '_' || c == '$') {
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            while (Character.isLetterOrDigit(peek()) || peek() == '_' || peek() == '$') {
                sb.append(advance());
            }
            String lexeme = sb.toString();
            TokenType type = keywords.get(lexeme.toLowerCase());
            if (type != null) return new Token(type, lexeme, tokenLine, tokenColumn);
            return new Token(TokenType.IDENTIFIER, lexeme, tokenLine, tokenColumn);
        }

        // Nombres (entiers et décimaux simples)
        if (Character.isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            while (Character.isDigit(peek())) sb.append(advance());
            if (peek() == '.') {
                sb.append(advance());
                if (!Character.isDigit(peek())) {
                    // point isolé -> revenir au comportement d'opérateur DOT
                    return new Token(TokenType.NUMBER, sb.toString(), tokenLine, tokenColumn);
                }
                while (Character.isDigit(peek())) sb.append(advance());
            }
            return new Token(TokenType.NUMBER, sb.toString(), tokenLine, tokenColumn);
        }

        // Chaînes
        if (c == '"' || c == '\'') {
            char quote = c;
            StringBuilder sb = new StringBuilder();
            sb.append(quote);
            boolean closed = false;
            while (!isAtEnd()) {
                char ch = advance();
                sb.append(ch);
                if (ch == '\\') {
                    // échappement : prendre le caractère suivant s'il existe
                    if (!isAtEnd()) {
                        sb.append(advance());
                    }
                    continue;
                }
                if (ch == quote) {
                    closed = true;
                    break;
                }
            }
            String lexeme = sb.toString();
            if (!closed) {
                ErrorHandler.reportError("Chaîne non terminée", tokenLine, tokenColumn);
                ErrorHandler.explain("Vérifiez la présence de la guillemet fermante '" + quote + "'");
                return new Token(TokenType.INVALID_TOKEN, lexeme, tokenLine, tokenColumn);
            }
            return new Token(TokenType.STRING, lexeme, tokenLine, tokenColumn);
        }

        // Commentaires et opérateur /
        if (c == '/') {
            if (peek() == '/') {
                // commentaire ligne
                StringBuilder sb = new StringBuilder();
                sb.append("//");
                advance(); // consommer deuxième /
                while (!isAtEnd() && peek() != '\n') sb.append(advance());
                return new Token(TokenType.SINGLE_LINE_COMMENT, sb.toString(), tokenLine, tokenColumn);
            } else if (peek() == '*') {
                // commentaire bloc
                StringBuilder sb = new StringBuilder();
                sb.append("/*");
                advance(); // consommer '*'
                boolean closed = false;
                while (!isAtEnd()) {
                    char ch = advance();
                    sb.append(ch);
                    if (ch == '*' && peek() == '/') {
                        sb.append(advance()); // consommer '/'
                        closed = true;
                        break;
                    }
                }
                if (!closed) {
                    ErrorHandler.reportError("Commentaire de bloc non fermé", tokenLine, tokenColumn);
                    ErrorHandler.explain("Fermez le commentaire avec '*/'");
                    return new Token(TokenType.INVALID_TOKEN, sb.toString(), tokenLine, tokenColumn);
                }
                return new Token(TokenType.MULTI_LINE_COMMENT, sb.toString(), tokenLine, tokenColumn);
            } else {
                return new Token(TokenType.DIVIDE, "/", tokenLine, tokenColumn);
            }
        }

        // Opérateurs multi-caractères et symboles simples
        switch (c) {
            case '+':
                if (peek() == '+') { advance(); return new Token(TokenType.INCREMENT, "++", tokenLine, tokenColumn); }
                if (peek() == '=')  { advance(); return new Token(TokenType.PLUS_ASSIGN, "+=", tokenLine, tokenColumn); }
                return new Token(TokenType.PLUS, "+", tokenLine, tokenColumn);
            case '-':
                if (peek() == '-') { advance(); return new Token(TokenType.DECREMENT, "--", tokenLine, tokenColumn); }
                if (peek() == '=') { advance(); return new Token(TokenType.MINUS_ASSIGN, "-=", tokenLine, tokenColumn); }
                return new Token(TokenType.MINUS, "-", tokenLine, tokenColumn);
            case '*':
                if (peek() == '=') { advance(); return new Token(TokenType.MULTIPLY_ASSIGN, "*=", tokenLine, tokenColumn); }
                return new Token(TokenType.MULTIPLY, "*", tokenLine, tokenColumn);
            case '%': return new Token(TokenType.MODULO, "%", tokenLine, tokenColumn);
            case ';': return new Token(TokenType.SEMICOLON, ";", tokenLine, tokenColumn);
            case ',': return new Token(TokenType.COMMA, ",", tokenLine, tokenColumn);
            case '.': return new Token(TokenType.DOT, ".", tokenLine, tokenColumn);
            case '(' : return new Token(TokenType.LPAREN, "(", tokenLine, tokenColumn);
            case ')' : return new Token(TokenType.RPAREN, ")", tokenLine, tokenColumn);
            case '{' : return new Token(TokenType.LBRACE, "{", tokenLine, tokenColumn);
            case '}' : return new Token(TokenType.RBRACE, "}", tokenLine, tokenColumn);
            case '[' : return new Token(TokenType.LBRACKET, "[", tokenLine, tokenColumn);
            case ']' : return new Token(TokenType.RBRACKET, "]", tokenLine, tokenColumn);
            case '=':
                if (peek() == '=') { advance(); if (peek() == '=') { advance(); return new Token(TokenType.EQUAL_EQUAL, "===", tokenLine, tokenColumn); } return new Token(TokenType.EQUAL_EQUAL, "==", tokenLine, tokenColumn); }
                return new Token(TokenType.ASSIGN, "=", tokenLine, tokenColumn);
            case '!':
                if (peek() == '=') { advance(); if (peek() == '=') { advance(); return new Token(TokenType.NOT_EQUAL, "!==", tokenLine, tokenColumn); } return new Token(TokenType.NOT_EQUAL, "!=", tokenLine, tokenColumn); }
                return new Token(TokenType.LOGICAL_NOT, "!", tokenLine, tokenColumn);
            case '<':
                if (peek() == '=') { advance(); return new Token(TokenType.LESS_EQUAL, "<=", tokenLine, tokenColumn); }
                return new Token(TokenType.LESS, "<", tokenLine, tokenColumn);
            case '>':
                if (peek() == '=') { advance(); return new Token(TokenType.GREATER_EQUAL, ">=", tokenLine, tokenColumn); }
                return new Token(TokenType.GREATER, ">", tokenLine, tokenColumn);
            case '&':
                if (peek() == '&') { advance(); return new Token(TokenType.LOGICAL_AND, "&&", tokenLine, tokenColumn); }
                break;
            case '|':
                if (peek() == '|') { advance(); return new Token(TokenType.LOGICAL_OR, "||", tokenLine, tokenColumn); }
                break;
            default:
                // caractère inconnu
                String s = Character.toString(c);
                ErrorHandler.reportError("Symbole inconnu: '" + s + "'", tokenLine, tokenColumn);
                return new Token(TokenType.INVALID_TOKEN, s, tokenLine, tokenColumn);
        }

        // Par défaut: retourner EOF (ne devrait pas arriver)
        return new Token(TokenType.EOF, "", tokenLine, tokenColumn);
    }

    // Récupérer tous les tokens (utilitaire)
    public List<Token> getAllTokens() {
        List<Token> tokens = new ArrayList<>();
        Token t;
        do {
            t = nextToken();
            tokens.add(t);
        } while (t.getType() != TokenType.EOF);
        return tokens;
    }

    // main de test
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: Lexer <source-file>");
            return;
        }
        try {
            StringBuilder content = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(args[0]));
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append('\n');
            }
            br.close();
            Lexer lexer = new Lexer(content.toString());
            for (Token tk : lexer.getAllTokens()) {
                System.out.println(tk);
            }
        } catch (IOException e) {
            ErrorHandler.reportException(e);
        }
    }
}
