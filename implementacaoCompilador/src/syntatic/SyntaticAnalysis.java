package syntatic;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;

public class SyntaticAnalysis {

    private LexicalAnalysis lex;
    private Lexeme current;
    // private Map<String,Variable> memory;

    public SyntaticAnalysis(LexicalAnalysis lex) {
        this.lex = lex;
        this.current = lex.nextToken();
        // memory = new HashMap<String,Variable>();
    }

    public void start() {
        program();
        eat(TokenType.END_OF_FILE);
    }

    private void advance() {
        System.out.println("Advanced (\"" + current.token + "\", " +
            current.type + ")");
        current = lex.nextToken();
    }

    private void eat(TokenType type) {
        System.out.println("Expected (..., " + type + "), found (\"" + 
            current.token + "\", " + current.type + ")");
        if (type == current.type) {
            current = lex.nextToken();
        } else {
            showError();
        }
    }

    private void showError() {
        System.out.printf("%02d: ", lex.getLine());

        switch (current.type) {
            case INVALID_TOKEN:
                System.out.printf("Lexema inválido [%s]\n", current.token);
                break;
            case UNEXPECTED_EOF:
            case END_OF_FILE:
                System.out.printf("Fim de arquivo inesperado\n");
                break;
            default:
                System.out.printf("Lexema não esperado [%s]\n", current.token);
                break;
        }

        System.exit(1);
    }

    // program ::= app identifier body
    private void program() {
        System.out.println("program");
        eat(TokenType.APP);
        eat(TokenType.NAME);
        body();
    }

    // body ::= var decl-list init stmt-list return | init stmt-list return 
    private void body() {
        System.out.println("body");
        if (current.type == TokenType.VAR) {
            advance();
            declList();
        }
        eat(TokenType.INIT);
        stmtList();
        eat(TokenType.RETURN);
    }

    // decl-list ::= decl decl-tail
    private void declList() {
        System.out.println("decl-list");
        decl();
        declTail();
    }

    // decl-tail ::= “;” decl decl-tail | lambda 
    private void declTail() {
        System.out.println("decl-tail");
        switch (current.type) {
            case SEMICOLON:
                advance();
                decl();
                declTail();
                break;
            case INIT:
                break;
            default:
                showError();
                break;
        }
    }

    // decl ::= type ident-list
    private void decl() {
        System.out.println("decl");
        type();
        identList();
    }

    // ident-list ::= identifier ident-tail 
    private void identList() {
        System.out.println("ident-list");
        eat(TokenType.NAME);
        identTail();
    }

    // ident-tail ::= "," identifier ident-tail | lambda 
    private void identTail() {
        System.out.println("ident-tail");
        switch (current.type) {
            case COMMA:
                advance();
                eat(TokenType.NAME);
                identTail();
                break;
            case INIT:
            case SEMICOLON:
                break;
            default:
                showError();
                break;
        }
    }

    // type ::= integer | real
    private void type() {
        System.out.println("type");
        switch (current.type) {
            case INTEGER:
            case REAL:
                advance();
                break;
            default:
                showError();
        }
    }

    // stmt-list ::= stmt stmt-tail
    private void stmtList() {
        System.out.println("stmt-list");
        stmt();
        stmtTail();
    }

    // stmt-tail ::= ";" stmt stmt-tail | lambda 
    private void stmtTail() {
        System.out.println("stmtTail");
        switch (current.type) {
            case SEMICOLON:
                advance();
                stmt();
                stmtTail();
                break;
            case RETURN:
            case END:
            case ELSE:
            case UNTIL:
                break;
            default:
                showError();
                break;
        }
    }

    // stmt ::= assign-stmt | if-stmt | repeat-stmt | read-stmt | write-stmt
    private void stmt() {
        System.out.println("stmt");
        switch (current.type) {
            case NAME:
                assignStmt();
                break;
            case IF:
                ifStmt();
                break;
            case REPEAT:
                repeatStmt();
                break;
            case READ:
                readStmt();
                break;
            case WRITE:
                writeStmt();
                break;
            default:
                showError();
        }
    }


    // assign-stmt ::= identifier ":=" simple_expr
    private void assignStmt() {
        System.out.println("assign-stmt");
        eat(TokenType.NAME);
        eat(TokenType.ASSIGN);
        simpleExpr();
    }

    // if-stmt ::= if condition then stmt-list if-tail
    private void ifStmt() {
        System.out.println("if-stmt");
        eat(TokenType.IF);
        condition();
        eat(TokenType.THEN);
        stmtList();
        ifTail();
    }

    // if-tail ::= end | else stmt-list end
    private void ifTail() {
        System.out.println("if-tail");
        if (current.type == TokenType.ELSE) {
            advance();
            stmtList();
        }
        eat(TokenType.END);
    }

    // repeat-stmt ::= repeat stmt-list stmt-suffix
    private void repeatStmt() {
        System.out.println("repeat-stmt");
        eat(TokenType.REPEAT);
        stmtList();
        stmtSuffix();
    }

    // stmt-suffix ::= until condition
    private void stmtSuffix() {
        System.out.println("stmt-suffix");
        eat(TokenType.UNTIL);
        condition();
    }

    // read-stmt ::= read "(" identifier ")"
    private void readStmt() {
        System.out.println("rrad-stmt");
        eat(TokenType.READ);
        eat(TokenType.OPEN_PAR);
        eat(TokenType.NAME);
        eat(TokenType.CLOSE_PAR);
    }

    // write-stmt ::= write "(" writable ")"
    private void writeStmt() {
        System.out.println("write-stmt");
        eat(TokenType.WRITE);
        eat(TokenType.OPEN_PAR);
        writable();
        eat(TokenType.CLOSE_PAR);
    }

    // writable ::= simple-expr | literal
    private void writable() {
        System.out.println("writable");
        switch (current.type) {
            case NAME:
            case INT_NUMBER:
            case REAL_NUMBER:
            case OPEN_PAR:
            case NOT:
            case SUB:
                simpleExpr();
                break;
            case TEXT:
                advance();
                break;
            default:
                showError();
                break;
        }
    }

    // condition ::= expression
    private void condition() {
        System.out.println("proCondition");
        expr();
    }

    // expression ::= simple-expr expr-tail
    private void expr() {
        System.out.println("expression");
        simpleExpr();
        exprTail();
    }

    // expr-tail ::= relop simple-expr | lambda 
    private void exprTail() {
        System.out.println("exprTail");
        switch (current.type) {
            case EQUAL:
            case GREATER_THAN:
            case GREATER_EQUAL:
            case LOWER_THAN:
            case LOWER_EQUAL:
            case NOT_EQUAL:
                relop();
                simpleExpr();
                break;
            case CLOSE_PAR:
            case THEN:
            case RETURN:
            case SEMICOLON:
            // case ADD:
            // case SUB:
            // case OR:
            case END:
            case ELSE:
            case UNTIL:
                break;
            default:
                showError();
                break;
        }
    }

    // simple-expr ::= term simple
    private void simpleExpr() {
        System.out.println("simpleExpr");
        switch (current.type) {
            case NAME:
            case INT_NUMBER:
            case REAL_NUMBER:
            case OPEN_PAR:
            case NOT:
            case SUB:
                term();
                simple();
                break;
            default:
                showError();
                break;
        }
    }

    // simple ::= addop term simple | lambda
    private void simple() {
        System.out.println("simple");
        switch (current.type) {
            case ADD:
            case SUB:
            case OR:
                addop();
                term();
                simple();
                break;
            case CLOSE_PAR:
            case THEN:
            case RETURN:
            case SEMICOLON:
            case EQUAL:
            case GREATER_THAN:
            case GREATER_EQUAL:
            case LOWER_THAN:
            case LOWER_EQUAL:
            case NOT_EQUAL:
            case END:
            case ELSE:
            case UNTIL:
                break;
            default:
                showError();
                break;
        }
    }

    // term ::= factor-a term-tail
    private void term() {
        System.out.println("term");
        switch (current.type) {
            case NOT:
            case SUB:
            case NAME:
            case OPEN_PAR:
            case INT_NUMBER:
            case REAL_NUMBER:
                factorA();
                termTail();
                break;
            default:
                showError();
                break;
        }
    }

    // term-tail ::= mulop factor-a term-tail | lambda
    private void termTail() {
        System.out.println("termTail");
        switch (current.type) {
            case MUL:
            case DIV:
            case AND:
                mulop();
                factorA();
                termTail();
                break;
            case CLOSE_PAR:
            case THEN:
            case RETURN:
            case SEMICOLON:
            case EQUAL:
            case GREATER_THAN:
            case GREATER_EQUAL:
            case LOWER_THAN:
            case LOWER_EQUAL:
            case NOT_EQUAL:
            case ADD:
            case SUB:
            case OR:
            case END:
            case ELSE:
            case UNTIL:
                break;
            default:
                showError();
                break;
        }
    }

    // factor-a ::= factor | "!" factor | "-" factor
    private void factorA() {
        System.out.println("factorA");
        switch (current.type) {
            case NAME:
            case OPEN_PAR:
            case INT_NUMBER:
            case REAL_NUMBER:
                factor();
                break;
            case NOT:
            case SUB:
                advance();
                factor();
                break;
            default:
                showError();
                break;
        }
    }

    // factor ::= identifier | constant | "(" expression ")"
    private void factor() {
        System.out.println("factor");
        switch (current.type) {
            case NAME:
                advance();
                break;
            case INT_NUMBER:
            case REAL_NUMBER:
                constant();
                break;
            case OPEN_PAR:
                advance();
                expr();
                eat(TokenType.CLOSE_PAR);
                break;
            default:
                showError();
                break;
        }
    }

    // addop ::= ADD |  SUB | OR
    private void addop() {
        System.out.println("addop");
        switch (current.type) {
            case ADD:
            case SUB:
            case OR:
                advance();
                break;
            default:
                showError();
                break;
        }
    }

    // relop ::= EQUAL | GREATER_THAN | LOWER_THAN | LOWER_EQUAL | GREATER_EQUAL | NOT_EQUAL
    private void relop() {
        System.out.println("relop");
        switch (current.type) {
            case EQUAL:
            case GREATER_THAN:
            case GREATER_EQUAL:
            case LOWER_THAN:
            case LOWER_EQUAL:
            case NOT_EQUAL:
                advance();
                break;
            default:
                showError();
                break;
        }
    }

    // mulop ::= MUL | DIV | AND
    private void mulop() {
        System.out.println("mulop");
        switch (current.type) {
            case MUL:
            case DIV:
            case AND:
                advance();
                break;
            default:
                showError();
                break;
        }
    }

    // constant ::= INTEGER | REAL
    private void constant() {
        System.out.println("constant");
        switch (current.type) {
            case INT_NUMBER:
            case REAL_NUMBER:
                advance();
                break;
            default:
                showError();
                break;
        }
    }
}