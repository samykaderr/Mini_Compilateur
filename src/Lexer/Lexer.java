package Lexer;

import java.util.*;

public class Lexer {
    private final String source;
    private int position = 0, line = 1, column = 1;

    public Lexer(String source) {
        this.source = (source == null) ? "" : source;
    }

    private boolean sichianeterminer() {
        return position >= source.length();
    }

    private char voirleprochaine() {
        return sichianeterminer() ? '\0' : source.charAt(position);
    }

    private char avanceanalyse() {
        char c = voirleprochaine();
        position++;
        if (c == '\n') { line++; column = 1; } else { column++; }
        return c;
    }

    private void sauterblanc() {
        while (!sichianeterminer() && " \r\t\n".indexOf(voirleprochaine()) != -1) {
            avanceanalyse();
        }
    }

    private boolean isLetter(char c) {
        return ((c<='Z' && c>='A') || (c<='z' && c>='a'));

    }

    private boolean isDigit(char c) {
        return (c<='9' && c>='0');
    }

    private boolean isLetterOrDigit(char c) {
        return Character.isLetterOrDigit(c);
    }

    private final Map<String, TokenType> motsCles = Map.ofEntries(
        Map.entry("let", TokenType.LET),
        Map.entry("var", TokenType.VAR),
        Map.entry("const", TokenType.CONST),
        Map.entry("function", TokenType.FUNCTION),
        Map.entry("return", TokenType.RETURN),
        Map.entry("if", TokenType.IF),
        Map.entry("else", TokenType.ELSE),
        Map.entry("while", TokenType.WHILE),
        Map.entry("do", TokenType.DO),
        Map.entry("for", TokenType.FOR),
        Map.entry("try", TokenType.TRY),
        Map.entry("catch", TokenType.CATCH),
        Map.entry("finally", TokenType.FINALLY),
        Map.entry("throw", TokenType.THROW),
        Map.entry("true", TokenType.TRUE),
        Map.entry("false", TokenType.FALSE),
        Map.entry("null", TokenType.NULL),
        Map.entry("undefined", TokenType.UNDEFINED),
        Map.entry("console", TokenType.CONSOLE),
        Map.entry("log", TokenType.LOG),
        Map.entry("samy", TokenType.SAMY),
        Map.entry("achouche", TokenType.ACHOUCHE),
        Map.entry("abdelkader", TokenType.ABDELKADER)
    );

    public Token Tokensuivant() {
        sauterblanc();
        if (sichianeterminer()) return new Token(TokenType.EOF, "", line, column);

        int tokenLine = line, tokenColumn = column;
        char c = avanceanalyse();

        if (isLetter(c) || c == '_' || c == '$') {
            StringBuilder sb = new StringBuilder().append(c);

            while (isLetterOrDigit(voirleprochaine()) || "_$".indexOf(voirleprochaine()) != -1) {
                sb.append(avanceanalyse());
            }
            String lexeme = sb.toString();
            return new Token(motsCles.getOrDefault(lexeme.toLowerCase(), TokenType.ID), lexeme, tokenLine, tokenColumn);
        }

        if (isDigit(c)) {
            StringBuilder sb = new StringBuilder().append(c);
            while (isDigit(voirleprochaine())) sb.append(avanceanalyse());
            if (voirleprochaine() == '.') {
                sb.append(avanceanalyse());
                while (isDigit(voirleprochaine())) sb.append(avanceanalyse());
            }
            return new Token(TokenType.NUMBER, sb.toString(), tokenLine, tokenColumn);
        }

        if (c == '"' || c == '\'') {
            char quote = c;
            StringBuilder sb = new StringBuilder().append(quote);
            boolean closed = false;
            while (!sichianeterminer()) {
                char ch = avanceanalyse();
                sb.append(ch);
                if (ch == '\\' && !sichianeterminer()) sb.append(avanceanalyse());
                else if (ch == quote) { closed = true; break; }
            }
            if (!closed) {
                ErrorHandler.reportError("Unterminated string", tokenLine, tokenColumn);
                return new Token(TokenType.INVALID_TOKEN, sb.toString(), tokenLine, tokenColumn);
            }
            return new Token(TokenType.STRING, sb.toString(), tokenLine, tokenColumn);
        }

        if (c == '/') {
            if (voirleprochaine() == '/') {
                StringBuilder sb = new StringBuilder("//");
                avanceanalyse();
                while (!sichianeterminer() && voirleprochaine() != '\n') sb.append(avanceanalyse());
                return new Token(TokenType.SINGLE_LINE_COMMENT, sb.toString(), tokenLine, tokenColumn);
            } else if (voirleprochaine() == '*') {
                StringBuilder sb = new StringBuilder("/*");
                avanceanalyse();
                boolean closed = false;
                while (!sichianeterminer()) {
                    char ch = avanceanalyse();
                    sb.append(ch);
                    if (ch == '*' && voirleprochaine() == '/') {
                        sb.append(avanceanalyse());
                        closed = true;
                        break;
                    }
                }
                if (!closed) {
                    ErrorHandler.reportError("Bloc commentaire non terminer ", tokenLine, tokenColumn);
                    return new Token(TokenType.INVALID_TOKEN, sb.toString(), tokenLine, tokenColumn);
                }
                return new Token(TokenType.MULTI_LINE_COMMENT, sb.toString(), tokenLine, tokenColumn);
            } else {
                return new Token(TokenType.DIVIDE, "/", tokenLine, tokenColumn);
            }
        }

        switch (c) {
            case '+': return gererOperateur(c, tokenLine, tokenColumn, "++", TokenType.INCREMENT, "+=", TokenType.PLUS_ASSIGN, TokenType.PLUS);
            case '-': return gererOperateur(c, tokenLine, tokenColumn, "--", TokenType.DECREMENT, "-=", TokenType.MINUS_ASSIGN, TokenType.MINUS);
            case '*': return gererOperateur(c, tokenLine, tokenColumn, null, null, "*=", TokenType.MULTIPLY_ASSIGN, TokenType.MULTIPLY);
            case '%': return new Token(TokenType.MODULO, "%", tokenLine, tokenColumn);
            case ';': return new Token(TokenType.SEMICOLON, ";", tokenLine, tokenColumn);
            case ',': return new Token(TokenType.COMMA, ",", tokenLine, tokenColumn);
            case '.': return new Token(TokenType.DOT, ".", tokenLine, tokenColumn);
            case '(': return new Token(TokenType.LPAREN, "(", tokenLine, tokenColumn);
            case ')': return new Token(TokenType.RPAREN, ")", tokenLine, tokenColumn);
            case '{': return new Token(TokenType.LBRACE, "{", tokenLine, tokenColumn);
            case '}': return new Token(TokenType.RBRACE, "}", tokenLine, tokenColumn);
            case '[': return new Token(TokenType.LBRACKET, "[", tokenLine, tokenColumn);
            case ']': return new Token(TokenType.RBRACKET, "]", tokenLine, tokenColumn);
            case '=': return gererEgaliter(c, tokenLine, tokenColumn, "==", TokenType.EQUAL_EQUAL, "===", TokenType.EQUAL_EQUAL);
            case '!': return gererEgaliter(c, tokenLine, tokenColumn, "!=", TokenType.NOT_EQUAL, "!==", TokenType.NOT_EQUAL);
            case '<': return gererOperateur(c, tokenLine, tokenColumn, null, null, "<=", TokenType.LESS_EQUAL, TokenType.LESS);
            case '>': return gererOperateur(c, tokenLine, tokenColumn, null, null, ">=", TokenType.GREATER_EQUAL, TokenType.GREATER);
            case '&': return gererLogiqueOp(c, tokenLine, tokenColumn, "&&", TokenType.LOGICAL_AND);
            case '|': return gererLogiqueOp(c, tokenLine, tokenColumn, "||", TokenType.LOGICAL_OR);
            default:
                ErrorHandler.reportError(" symbol non connue : '" + c + "'", tokenLine, tokenColumn);
                return new Token(TokenType.INVALID_TOKEN, Character.toString(c), tokenLine, tokenColumn);
        }
    }

    private Token gererOperateur(char c, int line, int column, String doubleOp, TokenType doubleType, String assignOp, TokenType assignType, TokenType singleType) {
        if (doubleOp != null && voirleprochaine() == doubleOp.charAt(1)) { avanceanalyse(); return new Token(doubleType, doubleOp, line, column); }
        if (assignOp != null && voirleprochaine() == assignOp.charAt(1)) { avanceanalyse(); return new Token(assignType, assignOp, line, column); }
        return new Token(singleType, Character.toString(c), line, column);
    }

    private Token gererEgaliter(char c, int line, int column, String doubleOp, TokenType doubleType, String tripleOp, TokenType tripleType) {
        if (voirleprochaine() == '=') {
            avanceanalyse();
            if (voirleprochaine() == '=') { avanceanalyse(); return new Token(tripleType, tripleOp, line, column); }
            return new Token(doubleType, doubleOp, line, column);
        }
        return new Token(TokenType.ASSIGN, Character.toString(c), line, column);
    }

    private Token gererLogiqueOp(char c, int line, int column, String doubleOp, TokenType doubleType) {
        if (voirleprochaine() == doubleOp.charAt(1)) { avanceanalyse(); return new Token(doubleType, doubleOp, line, column); }
        return new Token(TokenType.INVALID_TOKEN, Character.toString(c), line, column);
    }

    public List<Token> getAllTokens() {
        List<Token> tokens = new ArrayList<>();
        Token t;
        do {
            t = Tokensuivant();
            tokens.add(t);
        } while (t.getType() != TokenType.EOF);
        return tokens;
    }
}
