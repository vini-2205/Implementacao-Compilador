package lexical;

import java.io.FileInputStream;
import java.io.PushbackInputStream;

public class LexicalAnalysis implements AutoCloseable {

    private int line;
    private SymbolTable st;
    private PushbackInputStream input;

    public LexicalAnalysis(String filename) {
        try {
            input = new PushbackInputStream(new FileInputStream(filename), 2);
        } catch (Exception e) {
            throw new LexicalError("Não foi possível abrir o arquivo");
        }

        st = new SymbolTable();
        line = 1;
    }

    public void close() {
        try {
            input.close();
        } catch (Exception e) {
            throw new LexicalError("Não foi possível fechar o arquivo");
        }
    }

    public int getLine() {
        return this.line;
    }

    public Lexeme nextToken() {
        Lexeme lex = new Lexeme("", TokenType.END_OF_FILE);

        int state = 1;
        while (state != 12 && state != 13) {
            int c = getc();
        
            switch (state) {
                case 1:
                    if (c == ' ' || c == '\t' || c == '\r') {
                        state = 1;
                    } else if (c == '\n') {
                        this.line++;
                    } else if (Character.isLetter(c) || c == '_') {
                        lex.token += (char) c;
                        state = 2;
                    } else if (Character.isDigit(c)) {
                        lex.token += (char) c;
                        state = 3;
                    } else if (c == '<' || c == '>' || c == '!') {
                        lex.token += (char) c;
                        state = 4;
                    } else if (c == ':') {
                        lex.token += (char) c;
                        state = 5;
                    } else if (c == '|') {
                        lex.token += (char) c;
                        state = 6;
                    } else if (c == '&') {
                        lex.token += (char) c;
                        state = 7;
                    } else if (c == '%') {
                        state = 10;
                    } else if (c == '{') {
                        state = 11;
                    } else if (c == ';' || c == ',' || c == '=' || c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')') {
                        lex.token += (char) c;
                        state = 12;
                    } else if (c == -1) {
                        lex.type = TokenType.END_OF_FILE;
                        state = 13;
                    } else {
                        lex.token += (char) c;
                        lex.type = TokenType.INVALID_TOKEN;
                        state = 13;
                    }
                    break;
                case 2:
                    if  (c == '_' || Character.isLetter(c) || Character.isDigit(c)) {
                        lex.token += (char) c;
                        state = 2;
                    } else {
                        ungetc(c);
                        state = 12;
                    }
                    break;
                case 3:
                    if(Character.isDigit(c)){
                        lex.token += (char) c;
                        state = 3;
                    } else if(c == '.'){
                        lex.token += (char) c;
                        state = 8; 
                    }else{
                        ungetc(c);
                        lex.type = TokenType.INT_NUMBER;
                        state = 13;
                    }          
                    break;
                case 4:
                    if (c == '=') {
                        lex.token += (char) c;
                        state = 12;
                    } else {
                        ungetc(c);
                        state = 12;
                    }
                    break;
                case 5:
                    if (c == '=') {
                        lex.token += (char) c;
                        state = 12;
                    } else {
                        lex.token += (char) c;
                        lex.type = TokenType.INVALID_TOKEN;
                        state = 13;
                    }          
                    break;
                case 6:
                    if (c == '|') {
                        lex.token += (char) c;
                        state = 12;
                    } else {
                        lex.token += (char) c;
                        lex.type = TokenType.INVALID_TOKEN;
                        state = 13;
                    }
                    break;
                case 7:
                    if (c == '&') {
                        lex.token += (char) c;
                        state = 12;
                    } else {
                        lex.token += (char) c;
                        lex.type = TokenType.INVALID_TOKEN;
                        state = 12;
                    }                   
                    break;
                case 8:
                    if (Character.isDigit(c)) {
                        lex.token += (char) c;
                        state = 9;
                    } else {
                        lex.token += (char) c;
                        lex.type = TokenType.INVALID_TOKEN;
                        state = 13;
                    }
                    break;
                case 9:
                    if (Character.isDigit(c)) {
                        lex.token += (char) c;
                        state = 9;
                    } else {
                        ungetc(c);
                        lex.type = TokenType.REAL_NUMBER;
                        state = 13;
                    }
                    break;
                case 10:
                    if (c == '\n') {
                        this.line++;
                        state = 1;
                    } else if (c == -1) {
                        lex.type = TokenType.END_OF_FILE;
                        state = 13;
                    } else {
                        state = 10;
                    }
                    break;
                case 11:
                    if (c == '}') { 
                        lex.type = TokenType.TEXT;
                        state = 13;
                    } else if (c == -1) {
                        throw new LexicalError(String.format("String má formada (linha %02d)", this.line));
                    } else {
                        if (c == '\n'){
                            this.line++;
                        }
                        lex.token += (char) c;
                        state = 11;
                    }
                    break;
                default:
                    throw new LexicalError("Ocorreu um erro inesperado");
            }
        }
        if (state == 12) {
            lex.type = st.find(lex.token);
        }

        return lex;
    }

    public SymbolTable getSt() {
        return this.st;
    }

    private int getc() {
        try {
            return input.read();
        } catch (Exception e) {
            throw new LexicalError("Não foi possível ler o arquivo");
        }
    }

    private void ungetc(int c) {
        if (c != -1) {
            try {
                input.unread(c);
            } catch (Exception e) {
                throw new LexicalError("Não foi possível executar (ungetc)");
            }
        }
    }
}