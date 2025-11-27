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
//ici en ferifie si le fichier est vide donc nous somme a la fin .
    private boolean silaFin() {
        return position >= source.length();
    }
//ici on regarde le caractere courant
    private char peek() {
        return silaFin() ? '\0' : source.charAt(position);
    }

  
//ici on avance au caractere suivant pour continuser 'analyser le fichier'
    
    private char avance() {
        char c = peek();
        position++;//on vance la positon
        if (c == '\n') {
            line++;//et ici en definit les cordonne exacte de le caractere
            column = 1;
        } else {
            column++;
        }
        return c;
    }
//avec cette methode en saute les espaces blans ,_, , saut de lign ....
    private void skipWhitespace() {
        while (!silaFin()) {
            char c = peek();
            if (c == ' ' || c == '\r' || c == '\t' || c == '\n') {
                avance();
            } else {
                break;
            }
        }
    }
//cette methode est la structure de donnee qui contient les mots cles du language
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
        if (silaFin()) return new Token(TokenType.EOF, "", line, column);

        int tokenLine = line;
        int tokenColumn = column;

        char c = avance();

        // verifier le type du caracter si il est un Identifiants et mots-clés
        //Samy achouche cest des mot cle reserver a ne pas utiliseer comme cle dans e language
        if (Character.isLetter(c) || c == '_' || c == '$') {
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            while (Character.isLetterOrDigit(peek()) || peek() == '_' || peek() == '$') {
                sb.append(avance());
            }
            String lexeme = sb.toString();
            TokenType type = keywords.get(lexeme.toLowerCase());
            if (type != null) return new Token(type, lexeme, tokenLine, tokenColumn);
            return new Token(TokenType.ID, lexeme, tokenLine, tokenColumn);
        }

        // ici en veifie c'est le caractere est un Nombres (entiers et décimaux simples)
        //parce que js calsifie tout les chiffre et nombre sous le nom Number
        if (Character.isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            while (Character.isDigit(peek())) sb.append(avance());
            if (peek() == '.') {
                sb.append(avance());
                if (!Character.isDigit(peek())) {
                    // point isolé -> revenir au comportement d'opérateur DOT
                    return new Token(TokenType.NUMBER, sb.toString(), tokenLine, tokenColumn);
                }
                while (Character.isDigit(peek())) sb.append(avance());
            }
            return new Token(TokenType.NUMBER, sb.toString(), tokenLine, tokenColumn);
        }

        // ici en verifie si c'est un #String (guillemets simples ou doubles)
        if (c == '"' || c == '\'') {
            char quote = c;
            StringBuilder sb = new StringBuilder();
            sb.append(quote);
            boolean closed = false;
            while (!silaFin()) {
                char ch = avance();
                sb.append(ch);
                if (ch == '\\') {
                    // échappement : prendre le caractère suivant s'il existe
                    if (!silaFin()) {
                        sb.append(avance());
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

        // ici en rassure la gestion des commentaire Commentaires et opérateur /
        if (c == '/') {
            if (peek() == '/') {
                // commentaire ligne
                StringBuilder sb = new StringBuilder();
                sb.append("//");
                avance(); // consommer deuxième /
                while (!silaFin() && peek() != '\n') sb.append(avance());
                return new Token(TokenType.SINGLE_LINE_COMMENT, sb.toString(), tokenLine, tokenColumn);
            } else if (peek() == '*') {
                // commentaire bloc
                StringBuilder sb = new StringBuilder();
                sb.append("/*");
                avance(); // consommer '*'
                boolean closed = false;
                while (!silaFin()) {
                    char ch = avance();
                    sb.append(ch);
                    if (ch == '*' && peek() == '/') {
                        sb.append(avance()); // consommer '/'
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

        // la gestion des Opérateurs multi-caractères et symboles simples
        switch (c) {
            case '+':
                if (peek() == '+') { avance(); return new Token(TokenType.INCREMENT, "++", tokenLine, tokenColumn); }
                if (peek() == '=')  { avance(); return new Token(TokenType.PLUS_ASSIGN, "+=", tokenLine, tokenColumn); }
                return new Token(TokenType.PLUS, "+", tokenLine, tokenColumn);
            case '-':
                if (peek() == '-') { avance(); return new Token(TokenType.DECREMENT, "--", tokenLine, tokenColumn); }
                if (peek() == '=') { avance(); return new Token(TokenType.MINUS_ASSIGN, "-=", tokenLine, tokenColumn); }
                return new Token(TokenType.MINUS, "-", tokenLine, tokenColumn);
            case '*':
                if (peek() == '=') { avance(); return new Token(TokenType.MULTIPLY_ASSIGN, "*=", tokenLine, tokenColumn); }
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
                if (peek() == '=') { avance(); if (peek() == '=') { avance(); return new Token(TokenType.EQUAL_EQUAL, "===", tokenLine, tokenColumn); } return new Token(TokenType.EQUAL_EQUAL, "==", tokenLine, tokenColumn); }
                return new Token(TokenType.ASSIGN, "=", tokenLine, tokenColumn);
            case '!':
                if (peek() == '=') { avance(); if (peek() == '=') { avance(); return new Token(TokenType.NOT_EQUAL, "!==", tokenLine, tokenColumn); } return new Token(TokenType.NOT_EQUAL, "!=", tokenLine, tokenColumn); }
                return new Token(TokenType.LOGICAL_NOT, "!", tokenLine, tokenColumn);
            case '<':
                if (peek() == '=') { avance(); return new Token(TokenType.LESS_EQUAL, "<=", tokenLine, tokenColumn); }
                return new Token(TokenType.LESS, "<", tokenLine, tokenColumn);
            case '>':
                if (peek() == '=') { avance(); return new Token(TokenType.GREATER_EQUAL, ">=", tokenLine, tokenColumn); }
                return new Token(TokenType.GREATER, ">", tokenLine, tokenColumn);
            case '&':
                if (peek() == '&') { avance(); return new Token(TokenType.LOGICAL_AND, "&&", tokenLine, tokenColumn); }
                break;
            case '|':
                if (peek() == '|') { avance(); return new Token(TokenType.LOGICAL_OR, "||", tokenLine, tokenColumn); }
                break;
            default:
                // caractère inconnu
                String s = Character.toString(c);
                ErrorHandler.reportError("Symbole inconnu: '" + s + "'", tokenLine, tokenColumn);
                return new Token(TokenType.INVALID_TOKEN, s, tokenLine, tokenColumn);
        }

        // Par défaut: retourner EOF (ne devrait pas arriver)si sa arrive c'est la fin du fichier
        return new Token(TokenType.EOF, "", tokenLine, tokenColumn);
    }

    // Récupérer tous les tokens
    public List<Token> getAllTokens() {
        List<Token> tokens = new ArrayList<>();
        Token t;
        do {
            t = nextToken();
            tokens.add(t);
        } while (t.getType() != TokenType.EOF);
        return tokens;
    }
    //un test simple pour verifier le lexer c'est facultatife sont execution
    //juste un teste pendant le devellopement
    // main de test
    /*
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
    }*/
}
