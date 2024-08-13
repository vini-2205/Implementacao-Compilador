package lexical;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    private Map<String, TokenType> st;
    private Map<String, Integer> ids;
    private Integer idNumber;

    public SymbolTable() {
        st = new HashMap<String, TokenType>();
        ids = new HashMap<String, Integer>();
        idNumber = 0;

        // SYMBOLS
        st.put(";", TokenType.SEMICOLON);
        st.put(",", TokenType.COMMA);
        st.put("(", TokenType.OPEN_PAR);
        st.put(")", TokenType.CLOSE_PAR);

        // OPERATORS
        st.put(":=", TokenType.ASSIGN);
        st.put("!", TokenType.NOT);
        st.put("=", TokenType.EQUAL);
        st.put(">", TokenType.GREATER_THAN);
        st.put(">=", TokenType.GREATER_EQUAL);
        st.put("<", TokenType.LOWER_THAN);
        st.put("<=", TokenType.LOWER_EQUAL);
        st.put("!=", TokenType.NOT_EQUAL);
        st.put("+", TokenType.ADD);
        st.put("-", TokenType.SUB);
        st.put("||", TokenType.OR);
        st.put("*", TokenType.MUL);
        st.put("/", TokenType.DIV);
        st.put("&&", TokenType.AND);

        // KEYWORDS
        st.put("app", TokenType.APP);
        st.put("var", TokenType.VAR);
        st.put("init", TokenType.INIT);
        st.put("return", TokenType.RETURN);
        st.put("integer", TokenType.INTEGER);
        st.put("real", TokenType.REAL);
        st.put("if", TokenType.IF);
        st.put("else", TokenType.ELSE);
        st.put("then", TokenType.THEN);
        st.put("end", TokenType.END);
        st.put("repeat", TokenType.REPEAT);
        st.put("until", TokenType.UNTIL);
        st.put("read", TokenType.READ);
        st.put("write", TokenType.WRITE);
    }

    public boolean contains(String token) {
        return st.containsKey(token);
    }

    public TokenType find(String token) {
        if (this.contains(token)){
            return st.get(token);
        } else {
            this.addId(token);
            return TokenType.NAME;
        }
    }

    private void addId(String token) {
        if (!ids.containsKey(token)) {
            ids.put(token, idNumber);
            idNumber++;
        }
    }

    public void printSymbolTable() {
        System.out.println("Identifiers:");
        for (Map.Entry<String, Integer> entry : ids.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}