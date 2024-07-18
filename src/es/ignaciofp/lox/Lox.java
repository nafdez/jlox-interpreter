package es.ignaciofp.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    private static final Interpreter interpreter = new Interpreter();
    public static boolean hadError = false;
    public static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }

    private static void runPrompt() throws IOException {
        BufferedReader br = new BufferedReader(
            new InputStreamReader(System.in)
        );

        while (true) {
            System.out.print("> ");
            String line = br.readLine();
            if (line == null) break;
            run(line);
            hadError = false;
            System.out.println();
        }
    }

    private static void run(String source) {
        Scanner sc = new Scanner(source);
        List<Token> tokens = sc.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> expression = parser.parse();

        if (hadError) return;
        
        interpreter.interpret(expression);
    }

    public static void error(int line, String message) {
        report(line, "", message);
    }

    public static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    public static void runtimeError(RuntimeError error) {
        System.err.printf(String.format("%s\n[line %s]", error.getMessage(), error.token.line));
        hadRuntimeError = true;
    }

    private static void report(int line, String where, String message) {
        System.err.printf("[line %s] Error%s: %s", line, where, message);
        hadError = true;
    }
}
