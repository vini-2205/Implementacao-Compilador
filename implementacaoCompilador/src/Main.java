import lexical.*;

public class Main {
    public static void main(String[] args) throws Exception {
        StringBuilder output = new StringBuilder();
        boolean error = false;
        
        try (LexicalAnalysis l = new LexicalAnalysis("src/file.txt")) {
            Lexeme lex;
            do {
                lex = l.nextToken();
                if (lex.type == TokenType.INVALID_TOKEN || lex.type == TokenType.UNEXPECTED_EOF) {
                    error = true;
                    break;
                }
                output.append(String.format("%02d: (\"%s\", %s)\n", l.getLine(), lex.token, lex.type));
            } while (lex.type != TokenType.END_OF_FILE);

            if (error) {
                System.out.println("Caracter desconhecido:" + lex.token + " (linha " + l.getLine() + ")");
            } else {
                System.out.println(output.toString());
            }

        } catch (Exception e) {
            System.err.println("Internal error: " + e.getMessage());
        }
    }
}