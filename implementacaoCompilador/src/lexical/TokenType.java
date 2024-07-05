package lexical;

public enum TokenType {
    // SPECIALS
    UNEXPECTED_EOF,
    INVALID_TOKEN,
    END_OF_FILE,

    // SYMBOLS
    COMMA,         // ,
    COLON,         // :
    SEMICOLON,     // ;
    OPEN_PAR,      // (
    CLOSE_PAR,     // )
    OPEN_CUR,      // {
    CLOSE_CUR,     // }

    // OPERATORS
    ASSIGN,        // :=
    EQUAL,         // =
    AND,           // &&
    OR,            // ||
    LOWER_THAN,    // <
    GREATER_THAN,  // >
    LOWER_EQUAL,   // <=
    GREATER_EQUAL, // >=
    NOT_EQUAL,     // !=
    ADD,           // +
    SUB,           // -
    MUL,           // *
    DIV,           // /
    NOT,           // !
    
    // KEYWORDS
    APP,           // app
    VAR,           // var
    INIT,          // init
    RETURN,        // return
    INTEGER,       // integer
    REAL,          // real
    IF,            // if
    ELSE,          // else
    THEN,          // then
    END,           // end
    REPEAT,        // REPEAT
    UNTIL,         // UNTIL
    WRITE,         // WRITE
    READ,          // read
    
    // OTHERS
    NAME,          // identifier
    NUMBER,        // real or integer 
    COMMENT,       // %
    
};