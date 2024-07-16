import lexical.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // if (args.length != 1) {
        //     System.out.println("Usage: java mdi [miniDart file]");
        //     return;
        // }

        try (LexicalAnalysis l = new LexicalAnalysis("ImplementacaoCompilador/src/file.txt")) {
            Lexeme lex;
            do {
                lex = l.nextToken();
                System.out.printf("%02d: (\"%s\", %s)\n", l.getLine(),
                    lex.token, lex.type);
            } while (lex.type != TokenType.END_OF_FILE &&
                     lex.type != TokenType.INVALID_TOKEN &&
                     lex.type != TokenType.UNEXPECTED_EOF);
        } catch (Exception e) {
            System.err.println("Internal error: " + e.getMessage());
        }
    }
}
