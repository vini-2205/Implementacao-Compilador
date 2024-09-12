package semantic;

import java.util.Arrays;
import java.util.HashMap;
import lexical.SymbolTable;
import lexical.TokenType;

public class SemanticAnalysis {
    private final SymbolTable st;
    private final HashMap<String, Var> vars;

    public SemanticAnalysis() {
        this.st = new SymbolTable();
        this.vars = new HashMap<>();
    }

    private boolean hasDeclartion(String token) {
        return vars.containsKey(token);
    }

    private boolean isReserved(String token) {
        return st.contains(token);
    }

    public void isBoolean(IdType type) throws SemanticException {
        if (!type.equals(IdType.BOOLEAN)) {
            throw new SemanticException("O expressão condicional precisa ser um booleano");
        }
    }

    public void checkDeclaration(String token) throws SemanticException {
        if (!hasDeclartion(token)) {
            throw new SemanticException("Variável não declarada");
        }
    }

    public void checkHasAssign(String token, IdType type) throws SemanticException {
        Var variable = getVar(token);
        if (variable.type.equals(IdType.APP)) {
            throw new SemanticException("O nome da aplicação não pode ser atribuído a uma variável");
        }
        if (variable.type.equals(IdType.REAL_NUMBER) && type.equals(IdType.INT_NUMBER)) {
            return;
        }
        if (!variable.type.equals(type)) {
            throw new SemanticException("Tipo da variável não corresponde ao tipo da expressão");
        }
    }

    public IdType checkOp(IdType leftType, IdType rightType, TokenType op) throws SemanticException {
        if (op.equals(TokenType.OR)) {
            if (leftType.equals(IdType.BOOLEAN) && rightType.equals(IdType.BOOLEAN)) {
                return IdType.BOOLEAN;
            } else {
                throw new SemanticException("Tipo da variável não corresponde ao tipo da expressão");
            }
        } else if (Arrays.asList(TokenType.ADD, TokenType.SUB).contains(op)) {
            if (leftType.equals(IdType.APP) || rightType.equals(IdType.APP)) {
                throw new SemanticException("O nome da aplicação não pode ser utilizado em uma operação aritmética");
            }
            if (leftType.equals(IdType.REAL_NUMBER) || rightType.equals(IdType.REAL_NUMBER)) {
                return IdType.REAL_NUMBER;
            }
            return IdType.INT_NUMBER;
        }
        throw new SemanticException("Operação inválida");
    }

    public IdType checkCompOp(IdType type1, IdType type2) throws SemanticException {
        if (type1.equals(IdType.APP) || type2.equals(IdType.APP)) {
            throw new SemanticException("O nome da aplicação não pode ser utilizado em uma operação de comparação");
        }
        return IdType.BOOLEAN;
    }

    public IdType checkBooleanOp(IdType type1, IdType type2) throws SemanticException {
        if (type1.equals(IdType.APP) || type2.equals(IdType.APP)) {
            throw new SemanticException("O nome da aplicação não pode ser utilizado em uma operação lógica");
        }
        if (type1.equals(IdType.BOOLEAN) && type2.equals(IdType.BOOLEAN)) {
            return IdType.BOOLEAN;
        }
        throw new SemanticException("Tipo da variável não corresponde ao tipo da expressão");
    }

    public void checkUnaryOp(IdType type) throws SemanticException {
        if (type.equals(IdType.APP)) {
            throw new SemanticException("O nome da aplicação não pode ser utilizado em uma operação aritmética");
        }
        if (!type.equals(IdType.INT_NUMBER) && !type.equals(IdType.REAL_NUMBER)) {
            throw new SemanticException("Tipo da variável não corresponde ao tipo da expressão");
        }
    }

    public void addVar(String token, IdType type) throws SemanticException {
        if (isReserved(token)) {
            throw new SemanticException("O nome da variável não pode ser uma palavra reservada");
        }
        if (hasDeclartion(token)) {
            throw new SemanticException("A variável já foi declarada");
        }

        vars.put(token, new Var(token, type));
    }

    public Var getVar(String token) throws SemanticException {
        return vars.get(token);
    }
}