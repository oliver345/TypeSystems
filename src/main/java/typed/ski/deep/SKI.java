package typed.ski.deep;

import typed.ski.deep.evaluator.Evaluator;
import typed.ski.deep.lang.preterm.Preterm;
import typed.ski.deep.lang.term.Term;
import typed.ski.deep.parser.Parser;
import typed.ski.deep.parser.ParserException;
import typed.ski.deep.typechecker.TypeChecker;
import typed.ski.deep.typechecker.TypeCheckerException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SKI {

    public static final String LE = "Rec (K True) (K (S (K (Rec False))(S (K K))))";

    public static final String B = "S (K S) K";

    public static final String C = "S (S (K B) S) (K K)";

    public static final String INSERT = "S (B RecList (C Cons [])) (C (B S (B (B C) (B (B (B B)) (S (B S (B (B B) (B (B ITE) LE))) (C (B B (B B Cons)) Cons))))) Cons)";

    public static final String INSERTION_SORT = "RecList [] (S (K K) INSERT)";

    private static final List<String> reservedTokens = List.of("S", "K", "I", "False", "True", "ITE", "Rec", "Succ",
            "ZERO", "Bool", "Nat", "Str", "RecList", "Cons", "quit", "List");

    private static boolean prettyPrintStyle = true;

    public static void main(String[] args) {

        Stream.of(args)
                .filter(arg -> arg.startsWith("-simplePrintStyle"))
                .findAny()
                .ifPresent(arg -> prettyPrintStyle = false);

        Stream.of(args)
                .filter(arg -> arg.startsWith("-eval"))
                .findAny()
                .ifPresentOrElse(arg -> {
                    int posOfEqual = arg.indexOf("=");
                    if (posOfEqual > -1) {
                        String sourcePath = arg.substring(posOfEqual + 1);
                        Path path = Paths.get(sourcePath);
                        try {
                            String code = Files.lines(path).collect(Collectors.joining());
                            executeCode(code);
                        }
                        catch (IOException ioException) {
                            throw new RuntimeException(ioException);
                        }
                    }
                    else {
                        throw new IllegalArgumentException(arg);
                    }
                },
                SKI::runInREPL);
    }

    public static void executeCode(String input) {
        executeCode(input, null);
    }

    public static void executeCode(String input, Map<String, Preterm> definitions) {
        String[] codeLines = input.split(";");
        final Map<String, Preterm> finalDefs = definitions == null ? new LinkedHashMap<>() : definitions;

        Stream.of(codeLines).forEach(codeLine -> {
            try {
                String[] parts = codeLine.trim().split("=");
                switch (parts.length) {
                    case 2 -> storeDefinition(parts[0], parts[1], finalDefs);
                    case 1 -> {
                        String result = executeCodeLine(parts[0], finalDefs)
                                .map(term -> term.toString(prettyPrintStyle))
                                .orElse("<<NO RESULT>>");
                        System.out.println(parts[0] + " ==> " + result);
                    }
                    default -> System.out.println("Invalid statement \"" + codeLine + "\"");
                }
            }
            catch (ParserException parserException) {
                handleException(parserException, parserException.getMessage());
            }
        });
    }

    private static Optional<Term> executeCodeLine(String input, Map<String, Preterm> definitions) {
        try {
            return Optional.of(Evaluator.eval(TypeChecker.createWellTypedTree(Parser.createParseTree(input, definitions))));
        }
        catch (ParserException parserException) {
            handleException(parserException, "Could not parse \"" + input + "\"");
        }
        catch (TypeCheckerException typeCheckerException) {
            handleException(typeCheckerException, "Type check failed on \"" + input + "\"");
        }
        //Catch general exceptions!

        return Optional.empty();
    }

    public static void runInREPL() {
        System.out.println("Welcome to Typed SKI!");
        Map<String, Preterm> definitions = new LinkedHashMap<>();

        boolean stayInREPL = true;
        Scanner scanner = new Scanner(System.in);

        while (stayInREPL) {
            String input = scanner.nextLine();

            switch (input) {
                case "quit" -> stayInREPL = false;
                case "list defs" -> {
                    definitions.forEach((key, value) -> System.out.println(key.concat(" = ").concat(value.toString())));
                    System.out.println("---");
                }
                case "toggle print style" -> {
                    prettyPrintStyle = !prettyPrintStyle;
                    System.out.println("Pretty printing: " + (prettyPrintStyle ? "ON" : "OFF"));
                }
                case "" -> {}
                default -> executeCode(input, definitions);
            }
        }
    }

    private static void storeDefinition(String key, String statement, Map<String, Preterm> definitions) throws ParserException {
        if (!reservedTokens.contains(key) && key.matches("[^(:)\\]\\[<>{}=\\s-]+")) {
            try {
                definitions.put(key, Parser.createParseTree(statement, definitions));
                System.out.println("Definition stored:\n" + key + " = " + statement);
            }
            catch (Exception exception) {
                throw new ParserException("Could not store definition because parsing error", exception);
            }
        }
        else {
            throw new ParserException("Invalid definition key, definition not stored \"" + key + "\"");
        }
    }

    private static void handleException(Exception exception, String message) {
        System.out.println(message);
        if (!prettyPrintStyle) {
            exception.printStackTrace(System.out);
        }
    }
}
