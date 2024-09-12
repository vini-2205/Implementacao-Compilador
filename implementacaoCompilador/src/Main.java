import lexical.*;
import syntatic.SyntaticAnalysis;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder output = new StringBuilder();
        boolean error = false;
        
        System.out.print("Digite o nome do arquivo: ");
        String fileName = scanner.next();
        
        try (LexicalAnalysis lexical = new LexicalAnalysis("testes/" + fileName)) {
            SyntaticAnalysis syntatic = new SyntaticAnalysis(lexical);
            syntatic.start();
            System.out.println("Análise semântica realizada com sucesso!");   

            // Lexeme lex;
            // do {
            //     lex = lexical.nextToken();
            //     if (lex.type == TokenType.INVALID_TOKEN || lex.type == TokenType.UNEXPECTED_EOF) {
            //         error = true;
            //         break;
            //     }
            //     output.append(String.format("%02d: (\"%s\", %s)\n", lexical.getLine(), lex.token, lex.type));
            // } while (lex.type != TokenType.END_OF_FILE);


            // if (error) {
            //     System.out.println("Caracter desconhecido: " + lex.token + " (linha " + lexical.getLine() + ")");
            // } else {
            //     System.out.println(output);
            //     lexical.getSt().printSymbolTable();
            // }

        } 
        catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}