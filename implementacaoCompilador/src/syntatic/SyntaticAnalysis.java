package syntatic;

import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;
import semantic.SemanticAnalysis;
import semantic.SemanticException;
import semantic.IdType;

public class SyntaticAnalysis {

    private LexicalAnalysis lex;
    private SemanticAnalysis s;
    private Lexeme current;

    public SyntaticAnalysis(LexicalAnalysis lex) {
        this.lex = lex;
        this.current = lex.nextToken();
        this.s = new SemanticAnalysis();
    }

    public void start() {
        program();
        eat(TokenType.END_OF_FILE);
    }

    private void advance() {
        // System.out.println("Advanced (\"" + current.token + "\", " +
        //     current.type + ")");
        current = lex.nextToken();
    }

    private void eat(TokenType type) {
        // System.out.println("Expected (..., " + type + "), found (\"" + 
        //     current.token + "\", " + current.type + ")");
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

    private void showError(String msg) {
        System.out.printf("%02d: " + msg, lex.getLine());

        System.exit(1);
    }

    // program ::= app identifier body
    private void program() {
        // System.out.println("program");
        eat(TokenType.APP);
        try {
            s.addVar(current.token, IdType.APP);
        } catch (SemanticException e) {
            showError(e.getMessage());
        }
        eat(TokenType.NAME);
        body();
    }

    // body ::= var decl-list init stmt-list return | init stmt-list return 
    private void body() {
        // System.out.println("body");
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
        // System.out.println("decl-list");
        decl();
        declTail();
    }

    // decl-tail ::= “;” decl decl-tail | lambda 
    private void declTail() {
        // System.out.println("decl-tail");
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
        // System.out.println("decl");
        IdType idType = type();
        identList(idType);
    }

    // ident-list ::= identifier ident-tail 
    private void identList(IdType idType) {
        // System.out.println("ident-list");
        try {
            s.addVar(current.token, idType);
        } catch (SemanticException e) {
            showError(e.getMessage());
        }
        eat(TokenType.NAME);
        identTail(idType);
    }

    // ident-tail ::= "," identifier ident-tail | lambda 
    private void identTail(IdType idType) {
        // System.out.println("ident-tail");
        switch (current.type) {
            case COMMA:
                advance();

                try {
                    s.addVar(current.token, idType);
                } catch (SemanticException e) {
                    showError(e.getMessage());
                }

                eat(TokenType.NAME);
                identTail(idType);
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
    private IdType type() {
        // System.out.println("type");
        switch (current.type) {
            case INTEGER:
                advance();
                return IdType.INT_NUMBER;
            case REAL:
                advance();
                return IdType.REAL_NUMBER;
            default:
                showError();
                return null;
        }
    }

    // stmt-list ::= stmt stmt-tail
    private void stmtList() {
        // System.out.println("stmt-list");
        stmt();
        stmtTail();
    }

    // stmt-tail ::= ";" stmt stmt-tail | lambda 
    private void stmtTail() {
        // System.out.println("stmtTail");
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
        // System.out.println("stmt");

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
        // System.out.println("assign-stmt");
        String identifier = current.token;
        eat(TokenType.NAME);
        eat(TokenType.ASSIGN);
        IdType type = simpleExpr();

        try {
            s.checkDeclaration(identifier);
        } catch (SemanticException e) {
            showError(e.getMessage());
        }

        //System.out.println(identifier + " " + type);

        try {
            s.checkHasAssign(identifier, type);
        } catch (SemanticException e) {
            showError(e.getMessage());
        }
    }

    // if-stmt ::= if condition then stmt-list if-tail
    private void ifStmt() {
        // System.out.println("if-stmt");
        eat(TokenType.IF);
        IdType conditionType = condition();

        try {
            s.isBoolean(conditionType);
        } catch (SemanticException e) {
            showError(e.getMessage());
        }

        eat(TokenType.THEN);
        stmtList();
        ifTail();
    }

    // if-tail ::= end | else stmt-list end
    private void ifTail() {
        // System.out.println("if-tail");
        if (current.type == TokenType.ELSE) {
            advance();
            stmtList();
        }
        eat(TokenType.END);
    }

    // repeat-stmt ::= repeat stmt-list stmt-suffix
    private void repeatStmt() {
        // System.out.println("repeat-stmt");
        eat(TokenType.REPEAT);
        stmtList();
        stmtSuffix();
    }

    // stmt-suffix ::= until condition
    private void stmtSuffix() {
        // System.out.println("stmt-suffix");
        eat(TokenType.UNTIL);
        IdType conditionType = condition();

        try {
            s.isBoolean(conditionType);
        } catch (SemanticException e) {
            showError(e.getMessage());
        }
    }

    // read-stmt ::= read "(" identifier ")"
    private void readStmt() {
        // System.out.println("rrad-stmt");
        eat(TokenType.READ);
        eat(TokenType.OPEN_PAR);
        eat(TokenType.NAME);
        eat(TokenType.CLOSE_PAR);
    }

    // write-stmt ::= write "(" writable ")"
    private void writeStmt() {
        // System.out.println("write-stmt");
        eat(TokenType.WRITE);
        eat(TokenType.OPEN_PAR);
        writable();
        eat(TokenType.CLOSE_PAR);
    }

    // writable ::= simple-expr | literal
    private void writable() {
        // System.out.println("writable");
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
    private IdType  condition() {
        // System.out.println("proCondition");
        return expr();
    }

    // expression ::= simple-expr expr-tail
    private IdType expr() {
        // System.out.println("expression");
        IdType lexpr = simpleExpr();
        IdType aux = exprTail(lexpr);
        
        return aux != null ? aux : lexpr;
    }

    // expr-tail ::= relop simple-expr | lambda 
    private IdType exprTail(IdType lexpr) {
        // System.out.println("exprTail");
        switch (current.type) {
            case EQUAL:
            case GREATER_THAN:
            case GREATER_EQUAL:
            case LOWER_THAN:
            case LOWER_EQUAL:
            case NOT_EQUAL:
                relop();
                IdType rexpr = simpleExpr();
                try {
                    lexpr = s.checkCompOp(lexpr, rexpr);
                } catch (SemanticException e) {
                    showError(e.getMessage());
                }
                return lexpr;
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
                return null;
            default:
                showError();
                return null;
        }
    }

    // simple-expr ::= term simple
    private IdType simpleExpr() {
        // System.out.println("simpleExpr");
        switch (current.type) {
            case NAME:
            case INT_NUMBER:
            case REAL_NUMBER:
            case OPEN_PAR:
            case NOT:
            case SUB:
                IdType lexpr = term();
                IdType aux = simple(lexpr);
                return aux != null ? aux : lexpr;
            default:
                showError();
                return null;
        }
    }

    // simple ::= addop term simple | lambda
    private IdType simple(IdType lexpr) {
        // System.out.println("simple");
        switch (current.type) {
            case ADD:
            case SUB:
            case OR:
                TokenType op = current.type;
                addop();
                IdType rexpr = term();
                //System.out.println(lexpr + " " + rexpr + " " + op);
                try {
                    lexpr = s.checkOp(lexpr, rexpr, op);
                } catch (SemanticException e) {
                    showError(e.getMessage());
                }
                IdType aux = simple(lexpr);
                return aux != null ? aux : lexpr;
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
                return null;
            default:
                showError();
                return null;
        }
    }

    // term ::= factor-a term-tail
    private IdType term() {
        // System.out.println("term");
        switch (current.type) {
            case NOT:
            case SUB:
            case NAME:
            case OPEN_PAR:
            case INT_NUMBER:
            case REAL_NUMBER:
                IdType lexpr = factorA();
                IdType aux = termTail(lexpr);
                return aux != null ? aux : lexpr;
            default:
                showError();
                return null;
        }
    }

    // term-tail ::= mulop factor-a term-tail | lambda
    private IdType termTail(IdType lexpr) {
        // System.out.println("termTail");
        switch (current.type) {
            case MUL:
            case DIV:
            case AND:
                TokenType op = current.type;
                mulop();
                IdType rexpr = factorA();

                try {
                    lexpr = s.checkOp(lexpr, rexpr, op);
                } catch (SemanticException e) {
                    showError(e.getMessage());
                }

                IdType aux = termTail(lexpr);
                return aux != null ? aux : lexpr;
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
                return null;
            default:
                showError();
                return null;
        }
    }

    // factor-a ::= factor | "!" factor | "-" factor
    private IdType factorA() {
        // System.out.println("factorA");
        IdType type;
        switch (current.type) {
            case NAME:
            case OPEN_PAR:
            case INT_NUMBER:
            case REAL_NUMBER:
                type = factor();
                return type;
            case NOT:
                advance();
                type = factor();

                try {
                    s.isBoolean(type);
                } catch (SemanticException e) {
                    showError(e.getMessage());
                }

                return type;
            case SUB:
                advance();
                type = factor();

                try {
                    s.checkUnaryOp(type);
                } catch (SemanticException e) {
                    showError(e.getMessage());
                }

                return type;
            default:
                showError();
                return null;
        }
    }

    // factor ::= identifier | constant | "(" expression ")"
    private IdType factor() {
        // System.out.println("factor");
        IdType type = null;
        switch (current.type) {
            case NAME:
                String id = current.token;
                advance();
            
                try {
                    type = s.getVar(id).type;
                } catch (SemanticException e) {
                    showError(e.getMessage());
                }

                return type;
            case INT_NUMBER:
                constant();
                type = IdType.INT_NUMBER;
                return type;
            case REAL_NUMBER:
                constant();
                type = IdType.REAL_NUMBER;
                return type;
            case OPEN_PAR:
                advance();
                type = expr();
                eat(TokenType.CLOSE_PAR);
                return type;
            default:
                showError();
                return null;
        }
    }

    // addop ::= ADD |  SUB | OR
    private void addop() {
        // System.out.println("addop");
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
        // System.out.println("relop");
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
        // System.out.println("mulop");
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
        // System.out.println("constant");
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