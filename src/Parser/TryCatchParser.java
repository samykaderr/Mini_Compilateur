package Parser;

import Lexer.Token;
import Lexer.TokenType;
import Lexer.Lexer;

public class TryCatchParser {
    private final Lexer lexer;
    private Token current;

    public TryCatchParser(Lexer lexer) {
        this.lexer = lexer;
        this.current = lexer.nextToken();
    }

    // Avancer et regarder le prochain token (LL(1) : une seule entrée regardée)
    private void advance() throws ParseException {
        current = lexer.nextToken();
        // Ignorer les commentaires
        while (current.getType() == TokenType.SINGLE_LINE_COMMENT || current.getType() == TokenType.MULTI_LINE_COMMENT) {
            current = lexer.nextToken();
        }
        if (current.getType() == TokenType.INVALID_TOKEN) {
            ParseException pe = new ParseException("Erreur lexicale: " + current.getValue(), current.getLine(), current.getColumn());
            ErrorHandler.reportError(pe.getMessage(), pe.getLine(), pe.getColumn());
            ErrorHandler.diagnose(pe);
            throw pe;
        }
    }

    private boolean check(TokenType type) {
        return current.getType() == type;
    }

    // Lance une ParseException détaillée en indiquant le token trouvé et sa valeur
    private void errorExpected(String expected) throws ParseException {
        ParseException pe = new ParseException("Attendu " + expected + " mais trouvé " + current.getType() + " ('" + current.getValue() + "')", current.getLine(), current.getColumn());
        ErrorHandler.reportError(pe.getMessage(), pe.getLine(), pe.getColumn());
        ErrorHandler.diagnose(pe);
        throw pe;
    }

    // Lance une erreur générique placée sur le token courant
    private void error(String message) throws ParseException {
        ParseException pe = new ParseException(message + " — trouvé " + current.getType() + " ('" + current.getValue() + "')", current.getLine(), current.getColumn());
        ErrorHandler.reportError(pe.getMessage(), pe.getLine(), pe.getColumn());
        ErrorHandler.diagnose(pe);
        throw pe;
    }

    private void expect(TokenType type) throws ParseException {
        if (!check(type)) {
            errorExpected("'" + type + "'");
        }
        advance();
    }

    // Entrée principale : analyse tout le programme
    public void parseAll() throws ParseException {
        if (current.getType() == TokenType.INVALID_TOKEN) {
            ParseException pe = new ParseException("Erreur lexicale: " + current.getValue(), current.getLine(), current.getColumn());
            ErrorHandler.reportError(pe.getMessage(), pe.getLine(), pe.getColumn());
            ErrorHandler.diagnose(pe);
            throw pe;
        }
        // Si le token initial est un commentaire, avancer jusqu'au premier token significatif
        while (current.getType() == TokenType.SINGLE_LINE_COMMENT || current.getType() == TokenType.MULTI_LINE_COMMENT) {
            advance();
        }
        parseProgram();
    }

    // Program -> { Statement }
    private void parseProgram() throws ParseException {
        while (current.getType() != TokenType.EOF) {
            parseStatement();
        }
    }

    // Statement -> TryStatement | other (ignored)
    private void parseStatement() throws ParseException {
        if (check(TokenType.TRY)) {
            parseTryStatement();
        } else if (check(TokenType.LET) || check(TokenType.VAR) || check(TokenType.CONST)) {
            parseVariableDeclaration();
        } else if (check(TokenType.LBRACE)) {
            parseBlock();
        } else if (check(TokenType.IDENTIFIER)) {
            // Could be an assignment or a function call
            parseExpressionStatement();
        } else if (check(TokenType.SEMICOLON)) {
            // Empty statement
            advance();
        }
        else {
            // Ignorer les autres types de statements pour le moment
            advance();
        }
    }

    private void parseExpressionStatement() throws ParseException {
        parseExpression();
        if(check(TokenType.SEMICOLON)){
            expect(TokenType.SEMICOLON);
        }
    }


    private void parseVariableDeclaration() throws ParseException {
        advance(); // Consume 'let', 'var', or 'const'
        expect(TokenType.IDENTIFIER);
        if (check(TokenType.ASSIGN)) {
            advance();
            parseExpression();
        }
        // Semicolon is optional here
        if(check(TokenType.SEMICOLON)){
            advance();
        }
    }

    // New expression parsing hierarchy
    private void parseExpression() throws ParseException {
        parseAssignment();
    }

    private void parseAssignment() throws ParseException {
        parseComparison(); // Higher precedence

        if (check(TokenType.ASSIGN)) {
            advance(); // consume '='
            parseAssignment(); // Right-associative
        }
    }

    private void parseComparison() throws ParseException {
        parsePrimary(); // Higher precedence

        while (isComparisonOperator(current.getType())) {
            advance();
            parsePrimary();
        }
    }

    private void parsePrimary() throws ParseException {
        if (check(TokenType.IDENTIFIER) || check(TokenType.NUMBER) || check(TokenType.STRING) || check(TokenType.TRUE) || check(TokenType.FALSE)) {
            advance();
        } else if (check(TokenType.LPAREN)) {
            advance();
            parseExpression();
            expect(TokenType.RPAREN);
        } else {
            error("Expression invalide : attendu un identifiant, un nombre, une chaîne ou une expression entre parenthèses.");
        }
    }

    private boolean isComparisonOperator(TokenType type) {
        switch (type) {
            case EQUAL_EQUAL:
            case STRICT_EQUAL:
            case NOT_EQUAL:
            case STRICT_NOT_EQUAL:
            case LESS:
            case LESS_EQUAL:
            case GREATER:
            case GREATER_EQUAL:
                return true;
            default:
                return false;
        }
    }

    // TryStatement -> 'try' Block CatchClause FinallyClause?
    private void parseTryStatement() throws ParseException {
        // current == TRY
        expect(TokenType.TRY);

        parseBlock();

        // Catch obligatoire en JS (à la forme try { } catch (e) { } )
        if (!check(TokenType.CATCH)) {
            error("Après 'try' attendu 'catch'");
        }

        parseCatchClause();

        // Finally optionnel
        if (check(TokenType.FINALLY)) {
            parseFinallyClause();
        }
    }

    // CatchClause -> 'catch' '(' IDENTIFIER ')' Block
    private void parseCatchClause() throws ParseException {
        expect(TokenType.CATCH);
        expect(TokenType.LPAREN);

        // Cas explicite: catch ()
        if (check(TokenType.RPAREN)) {
            // On pointe sur la parenthèse fermante : le message indique qu'on a trouvé ')' sans identifiant
            ParseException pe = new ParseException("Paramètre de 'catch' manquant (identifiant attendu) — parenthèses vides", current.getLine(), current.getColumn());
            ErrorHandler.reportError(pe.getMessage(), pe.getLine(), pe.getColumn());
            ErrorHandler.diagnose(pe);
            throw pe;
        }

        if (!check(TokenType.IDENTIFIER)) {
            error("Paramètre de 'catch' attendu (identifiant)");
        }
        advance(); // consommer l'identifiant

        expect(TokenType.RPAREN);

        parseBlock();
    }

    // FinallyClause -> 'finally' Block
    private void parseFinallyClause() throws ParseException {
        expect(TokenType.FINALLY);
        parseBlock();
    }

    // Block -> '{' BlockBody '}'
    // BlockBody is implemented via simple brace counting pour accepter n'importe quel contenu
    private void parseBlock() throws ParseException {
        expect(TokenType.LBRACE);
        while (!check(TokenType.RBRACE) && !check(TokenType.EOF)) {
            parseStatement();
        }
        expect(TokenType.RBRACE);
    }

    // Méthode simple de synchronisation (non utilisée dans la version actuelle, fournie pour extension)
    // Synchronize to next token that can start a statement or EOF
    private void synchronize() {
        while (current.getType() != TokenType.EOF) {
            if (current.getType() == TokenType.TRY || current.getType() == TokenType.RBRACE || current.getType() == TokenType.SEMICOLON) {
                return;
            }
            try {
                advance();
            } catch (ParseException e) {
                // En cas d'erreur lexicale lors de la synchronisation, on arrête la synchronisation
                return;
            }
        }
    }
}
