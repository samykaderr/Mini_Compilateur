package Lexer;
public enum TokenType {
    // Mots-clés JavaScript
    LET, VAR, CONST, FUNCTION, RETURN,
    IF, ELSE, WHILE, DO, FOR,
    TRY, CATCH, FINALLY, THROW,
    TRUE, FALSE, NULL, UNDEFINED,
    CONSOLE, LOG,

    // Mots-clés personnalisés (nom et prénom de l'étudiant)
    //jai utiliser just ,my first name (Adblekader) est le middle name .
    SAMY, ACHOUCHE,


    // Identifiants et littéraux
    ID,//les identifiant des variable
    NUMBER,//type de varible nuerique
    STRING,//chaine de caracter

    // Opérateurs
    PLUS, MINUS, MULTIPLY, DIVIDE, MODULO,
    EQUAL, EQUAL_EQUAL, NOT_EQUAL, STRICT_EQUAL, STRICT_NOT_EQUAL,
    LESS, LESS_EQUAL, GREATER, GREATER_EQUAL,
    LOGICAL_AND, LOGICAL_OR, LOGICAL_NOT,
    ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN, MULTIPLY_ASSIGN, DIVIDE_ASSIGN,

    // Incrémentation/décrémentation
    INCREMENT, DECREMENT,

    // Symboles
    SEMICOLON,
    COMMA,
    DOT,
    LPAREN, RPAREN, // ( )
    LBRACE, RBRACE, // { }
    LBRACKET, RBRACKET, // [ ]

    // Commentaires (pour débogage)
    SINGLE_LINE_COMMENT,
    MULTI_LINE_COMMENT,

    // Erreurs et fin de fichier
    INVALID_TOKEN,
    EOF
}