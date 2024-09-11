package semantic;

import lexical.SymbolTable;
import lexical.TokenType;

import java.util.Arrays;
import java.util.HashMap;

public class SemanticAnalysis {
    private final SymbolTable st;
    private final HashMap<String, Var> variables;

    public SemanticAnalysis() {
        this.st = new SymbolTable();
        this.variables = new HashMap<>();
    }

    private boolean isReservedKeyword(String token) {
        return st.contains(token);
    }

    private boolean isVariableDeclared(String token) {
        return variables.containsKey(token);
    }

    public void addVariable(String token, IdType type) {
        if (isReservedKeyword(token)) {
            throw new RuntimeException("Var name cannot be a reserved keyword");
        }
        if (isVariableDeclared(token)) {
            throw new RuntimeException("Var already declared");
        }

        variables.put(token, new Var(token, type));
    }

    public void checkVariableDeclared(String token) {
        if (!isVariableDeclared(token)) {
            throw new RuntimeException("Var not declared");
        }
    }

    public void checkAssignment(String token, IdType type) {
        Var variable = getVariable(token);
        if (variable.type.equals(IdType.APP)) {
            throw new RuntimeException("Cannot assign to app name");
        }
        if (variable.type.equals(IdType.REAL_NUMBER) && type.equals(IdType.INT_NUMBER)) {
            return;
        }
        if (!variable.type.equals(type)) {
            throw new RuntimeException("checkAssignment Type mismatch");
        }
    }

    public IdType checkArithmeticOperation(IdType type1, IdType type2) {
        if (type1.equals(IdType.APP) || type2.equals(IdType.APP)) {
            throw new RuntimeException("Cannot perform arithmetic operation with app name");
        }
        if (type1.equals(IdType.REAL_NUMBER) || type2.equals(IdType.REAL_NUMBER)) {
            return IdType.REAL_NUMBER;
        }
        return IdType.INT_NUMBER;
    }

    public IdType checkComparisonOperation(IdType type1, IdType type2) {
        if (type1.equals(IdType.APP) || type2.equals(IdType.APP)) {
            throw new RuntimeException("Cannot perform comparison operation with app name");
        }
        return IdType.BOOLEAN;
    }

    public IdType checkLogicalOperation(IdType type1, IdType type2) {
        if (type1.equals(IdType.APP) || type2.equals(IdType.APP)) {
            throw new RuntimeException("Cannot perform logical operation with app name");
        }
        if (type1.equals(IdType.BOOLEAN) && type2.equals(IdType.BOOLEAN)) {
            return IdType.BOOLEAN;
        }
        throw new RuntimeException("checkLogicalOperation Type mismatch");
    }

    public IdType checkArithmeticOrLogicalOperation(IdType leftType, IdType rightType, TokenType op) {
        if (op.equals(TokenType.OR)) {
            if (leftType.equals(IdType.BOOLEAN) && rightType.equals(IdType.BOOLEAN)) {
                return IdType.BOOLEAN;
            } else {
                throw new RuntimeException("checkArithmeticOrLogicalOperation Type mismatch");
            }
        } else if (Arrays.asList(TokenType.ADD, TokenType.SUB).contains(op)) {
            return checkArithmeticOperation(leftType, rightType);
        }
        throw new RuntimeException("Invalid operation");
    }

    public void isBooleanCondition(IdType type) {
        if (!type.equals(IdType.BOOLEAN)) {
            throw new RuntimeException("Condition must be boolean");
        }
    }

    public Var getVariable(String token) {
        return variables.get(token);
    }

    public void checkUnaryArithmeticOperation(IdType type) {
        if (type.equals(IdType.APP)) {
            throw new RuntimeException("Cannot perform unary arithmetic operation with app name");
        }
        if (!type.equals(IdType.INT_NUMBER) && !type.equals(IdType.REAL_NUMBER)) {
            throw new RuntimeException("checkUnaryArithmeticOperation Type mismatch");
        }
    }
}