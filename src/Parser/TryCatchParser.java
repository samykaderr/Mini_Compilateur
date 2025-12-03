package Parser;

import Lexer.Token;
import Lexer.TokenType;
import Lexer.Lexer;

public class TryCatchParser {
    private final Lexer lexer;
    private Token current;

    public TryCatchParser(Lexer lexer) {
        this.lexer = lexer;
        this.current = lexer.Tokensuivant();
    }

    // Avancer et regarder le prochain token (LL(1) : une seule entrée regardée)
    private void parcourire() throws ParseException {
        current = lexer.Tokensuivant();
        // Ignorer les commentaires
        while (current.getType() == TokenType.SINGLE_LINE_COMMENT || current.getType() == TokenType.MULTI_LINE_COMMENT) {
            current = lexer.Tokensuivant();
        }
        if (current.getType() == TokenType.INVALID_TOKEN) {
            ParseException pe = new ParseException("Erreur lexicale: " + current.getValue(), current.getLine(), current.getColumn());
            ErrorHandler.reportError(pe.getMessage(), pe.getLine(), pe.getColumn());
            ErrorHandler.diagnose(pe);
            throw pe;
        }
    }

    private boolean verifierType(TokenType type) {
        return current.getType() == type;
    }

    // Lance une ParseException détaillée en indiquant le token trouvé et sa valeur
    private void erreurAttendu(String expected) throws ParseException {
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
        if (!verifierType(type)) {
            erreurAttendu("'" + type + "'");
        }
        parcourire();
    }

    // Entrée principale : analyse tout le programme et detecter les erreurs try/catch
    public void anlysertoust() throws ParseException {
        if (current.getType() == TokenType.INVALID_TOKEN) {
            ParseException pe = new ParseException("Erreur lexicale: " + current.getValue(), current.getLine(), current.getColumn());
            ErrorHandler.reportError(pe.getMessage(), pe.getLine(), pe.getColumn());
            ErrorHandler.diagnose(pe);
            throw pe;
        }
        // Si le token initial est un commentaire, avancer jusqu'au premier token significatif
        while (current.getType() == TokenType.SINGLE_LINE_COMMENT || current.getType() == TokenType.MULTI_LINE_COMMENT) {
            parcourire();
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
        if (verifierType(TokenType.TRY)) {
            parseTryStatement();
        } else if (verifierType(TokenType.LET) || verifierType(TokenType.VAR) || verifierType(TokenType.CONST)) {
            parseVariableDeclaration();
        } else if (verifierType(TokenType.LBRACE)) {
            parseBlock();
        } else if (verifierType(TokenType.ID)) {
            // Could be an assignment or a function call
            parseExpressionStatement();
        } else if (verifierType(TokenType.SEMICOLON)) {
            // Empty statement
            parcourire();
        }
        else {
            // Ignorer les autres types de statements pour le moment
            parcourire();
        }
    }

    private void parseExpressionStatement() throws ParseException {
        parseExpression();
        if(verifierType(TokenType.SEMICOLON)){
            expect(TokenType.SEMICOLON);
        }
    }


    private void parseVariableDeclaration() throws ParseException {
        parcourire(); // Consomer 'let', 'var', or 'const'
        expect(TokenType.ID);
        if (verifierType(TokenType.ASSIGN)) {
            parcourire();
            parseExpression();
        }

        if(verifierType(TokenType.SEMICOLON)){
            parcourire();
        }
    }

    // New expression parsing hierarchy
    private void parseExpression() throws ParseException {
        parseAssignment();
    }

    private void parseAssignment() throws ParseException {
        parseComparison(); // Higher precedence

        if (verifierType(TokenType.ASSIGN)) {
            parcourire(); // consume '='
            parseAssignment(); // Right-associative
        }
    }

    private void parseComparison() throws ParseException {
        parsePrimary(); // Higher precedence

        while (isComparisonOperator(current.getType())) {
            parcourire();
            parsePrimary();
        }
    }

    private void parsePrimary() throws ParseException {
        if (verifierType(TokenType.ID) || verifierType(TokenType.NUMBER) || verifierType(TokenType.STRING) || verifierType(TokenType.TRUE) || verifierType(TokenType.FALSE)) {
            parcourire();
        } else if (verifierType(TokenType.LPAREN)) {
            parcourire();
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
        if (!verifierType(TokenType.CATCH)) {
            error("Après 'try' attendu 'catch'");
        }

        parseCatchClause();

        // Finally optionnel
        if (verifierType(TokenType.FINALLY)) {
            parseFinallyClause();
        }
    }

    // CatchClause -> 'catch' '(' IDENTIFIER ')' Block
    private void parseCatchClause() throws ParseException {
        expect(TokenType.CATCH);
        expect(TokenType.LPAREN);

        // Cas explicite: catch ()
        if (verifierType(TokenType.RPAREN)) {
            // On pointe sur la parenthèse fermante : le message indique qu'on a trouvé ')' sans identifiant
            ParseException pe = new ParseException("Paramètre de 'catch' manquant (identifiant attendu) — parenthèses vides", current.getLine(), current.getColumn());
            ErrorHandler.reportError(pe.getMessage(), pe.getLine(), pe.getColumn());
            ErrorHandler.diagnose(pe);
            throw pe;
        }

        if (!verifierType(TokenType.ID)) {
            error("Paramètre de 'catch' attendu (identifiant)");
        }
        parcourire(); // consommer l'identifiant

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
        while (!verifierType(TokenType.RBRACE) && !verifierType(TokenType.EOF)) {
            parseStatement();
        }
        expect(TokenType.RBRACE);
    }



}
