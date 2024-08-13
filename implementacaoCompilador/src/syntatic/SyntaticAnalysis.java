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
        procProgram();
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
    private void procProgram() {
        System.out.println("program");
        eat(TokenType.APP);
        eat(TokenType.NAME);
        procBody();
    }

    // body ::= [ var decl-list] init stmt-list return
    private void procBody() {
        System.out.println("body");
        if (current.type == TokenType.VAR) {
            advance();
            procDeclList();
        }
        eat(TokenType.INIT);
        procStmtList();
        eat(TokenType.RETURN);
    }

    // decl-list ::= decl {";" decl}
    private void procDeclList() {
        System.out.println("decl-list");
        procDecl();
        while (current.type == TokenType.SEMICOLON) {
            advance();
            procDecl();
        }
    }

    // decl ::= type ident-list
    private void procDecl() {
        System.out.println("decl");
        procType();
        procIdentList();
    }

    // ident-list ::= identifier {"," identifier}
    private void procIdentList() {
        System.out.println("ident-list");
        eat(TokenType.NAME);
        while (current.type == TokenType.COMMA) {
            advance();
            eat(TokenType.NAME);
        }
    }

    // type ::= integer | real
    private void procType() {
        System.out.println("type");
        switch (current.type) {
            case INTEGER:
                advance();
                break;
            case REAL:
                advance();
                break;
            default:
                showError();
        }
    }

    // stmt-list ::= stmt {";" stmt}
    private void procStmtList() {
        System.out.println("stmt-list");
        procStmt();
        while (current.type == TokenType.SEMICOLON) {
            advance();
            procStmt();
        }
    }

    // stmt ::= assign-stmt | if-stmt | repeat-stmt | read-stmt | write-stmt
    private void procStmt() {
        System.out.println("stmt");
        switch (current.type) {
            case NAME:
                procAssignStmt();
                break;
            case IF:
                procIfStmt();
                break;
            case REPEAT:
                procRepeatStmt();
                break;
            case READ:
                procReadStmt();
                break;
            case WRITE:
                procWriteStmt();
                break;
            default:
                showError();
        }
    }


    // assign-stmt ::= identifier ":=" simple_expr
    private void procAssignStmt() {
        System.out.println("assign-stmt");
        eat(TokenType.NAME);
        eat(TokenType.ASSIGN);
        procSimpleExpr();
    }

    // if-stmt ::= if condition then stmt-list end | if condition then stmt-list else stmt-list end
    private void procIfStmt() {
        System.out.println("if-stmt");
        eat(TokenType.IF);
        procCondition();
        eat(TokenType.THEN);
        procStmtList();

        if (current.type == TokenType.ELSE) {
            advance();
            procStmtList();
        }

        eat(TokenType.END);
    }

    // repeat-stmt ::= repeat stmt-list stmt-suffix
    private void procRepeatStmt() {
        System.out.println("repeat-stmt");
        eat(TokenType.REPEAT);
        procStmtList();
        procStmtSuffix();
    }

    // stmt-suffix ::= until condition
    private void procStmtSuffix() {
        System.out.println("stmt-suffix");
        eat(TokenType.UNTIL);
        procCondition();
    }

    // read-stmt ::= read "(" identifier ")"
    private void procReadStmt() {
        System.out.println("rrad-stmt");
        eat(TokenType.READ);
        eat(TokenType.OPEN_PAR);
        eat(TokenType.NAME);
        eat(TokenType.CLOSE_PAR);
    }

    // write-stmt ::= write "(" writable ")"
    private void procWriteStmt() {
        System.out.println("write-stmt");
        eat(TokenType.WRITE);
        eat(TokenType.OPEN_PAR);
        procWritable();
        eat(TokenType.CLOSE_PAR);
    }

    // writable ::= simple-expr | literal
    private void procWritable() {
        System.out.println("writable");
        switch (current.type) {
            case NAME:
            case INT_NUMBER:
            case REAL_NUMBER:
            case OPEN_PAR:
            case NOT:
            case SUB:
                procSimpleExpr();
                break;
            case TEXT:
                advance();
                break;
            default:
                showError();
        }
    }

    // condition ::= expression
    private void procCondition() {
        System.out.println("proCondition");
        procExpr();
    }

    // expression ::= simple-expr | simple-expr relop simple-expr
    private void procExpr() {
        System.out.println("expression");
        procSimpleExpr();

        switch (current.type) {
            case EQUAL:
            case GREATER_THAN:
            case GREATER_EQUAL:
            case LOWER_THAN:
            case LOWER_EQUAL:
            case NOT_EQUAL:
                advance();
                procSimpleExpr();
                break;
            default:
                break;
        }
    }

    // simple-expr ::= term | simple-expr addop term
    private void procSimpleExpr() {
        System.out.println("simpleExpr");
        switch (current.type) {
            case NAME:
            case INT_NUMBER:
            case REAL_NUMBER:
            case OPEN_PAR:
            case NOT:
            case SUB:
                procTerm();
                break;
            default:
                procSimpleExpr();
                eat(TokenType.ADD);
                procTerm();
                break;
        }
    }

    // term ::= factor-a | term mulop factor-a
    private void procTerm() {
        System.out.println("term");
        switch (current.type) {
            case NAME:
            case INT_NUMBER:
            case REAL_NUMBER:
            case OPEN_PAR:
            case NOT:
            case SUB:
                procFactorA();
                break;
            default:
                procTerm();
                eat(TokenType.MUL);
                procFactorA();
                break;
        }
    }

    // fator-a ::= factor | "!" factor | "-" factor
    private void procFactorA() {
        System.out.println("factorA");
        switch (current.type) {
            case NOT:
            case SUB:
                advance();
                procFactor();
                break;
            default:
                procFactor();
                break;
        }
    }

    // factor ::= identifier | constant | "(" expression ")"
    private void procFactor() {
        System.out.println("factor");
        switch (current.type) {
            case NAME:
                advance();
                break;
            case INT_NUMBER:
                advance();
                break;
            case REAL_NUMBER:
                advance();
                break;
            case OPEN_PAR:
                advance();
                procExpr();
                eat(TokenType.CLOSE_PAR);
                break;
            default:
                throw new RuntimeException("Erro de sintaxe");
        }
    }
}