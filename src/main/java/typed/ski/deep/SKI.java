package typed.ski.deep;

import typed.ski.deep.evaluator.Evaluator;
import typed.ski.deep.lang.preterm.Preterm;
import typed.ski.deep.lang.term.Term;
import typed.ski.deep.parser.Parser;
import typed.ski.deep.parser.ParserException;
import typed.ski.deep.typechecker.TypeChecker;
import typed.ski.deep.typechecker.TypeCheckerException;
import typed.ski.shallow.ShallowParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SKI {

    //B=S (K S) K;C=S (S (K B) S) (K K);toggle print style

    public static final String LE = "Rec (K True) (K (S (K (Rec False))(S (K K))))";

    public static final String B = "S (K S) K";

    public static final String C = "S (S (K B) S) (K K)";

    public static final String INSERT = "S (B RecList (C Cons [])) (C (B S (B (B C) (B (B (B B)) (S (B S (B (B B) (B (B ITE) LE))) (C (B B (B B Cons)) Cons))))) Cons)";

    public static final String INSERTION_SORT = "RecList [] (S (K K) INSERT)";

    public static final String ADD = "C Rec (K Succ)";

    public static final String MUL = "B (Rec ZERO) ((B K) ADD)";

    public static final String FAC = "((Rec (Succ ZERO)) ((B MUL) Succ))";

    public static final String CASE = "C (B B Rec) (B K)";

    public static final String ITER = "C (B B Rec) K";

    public static final String LE_ITER = "ITER (K True) (CASE False)";

    private static final List<String> reservedTokens = List.of("S", "K", "I", "False", "True", "ITE", "Rec", "Succ",
            "ZERO", "Bool", "Nat", "Str", "RecList", "Cons", "quit", "List");

    private static boolean prettyPrintStyle = true;

    private static boolean stayInREPL = true;

    private static boolean shallowEnabled = false;

    public static void main(String[] args) {

        Stream.of(args)
                .filter(arg -> arg.startsWith("-eval"))
                .findAny()
                .ifPresentOrElse(arg -> {
                    int posOfEqual = arg.indexOf("=");
                    if (posOfEqual > -1) {
                        String sourcePath = arg.substring(posOfEqual + 1);
                        loadFileInput(sourcePath);
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
            codeLine = codeLine.strip();
            if (stayInREPL) {
                if (codeLine.equals("quit")) {
                    stayInREPL = false;
                }
                else if (codeLine.equals("list defs")) {
                    finalDefs.forEach((key, value) -> System.out.println(key.concat(" = ").concat(value.toString())));
                    System.out.println("---");
                }
                else if (codeLine.equals("toggle print style")) {
                    prettyPrintStyle = !prettyPrintStyle;
                    System.out.println("Pretty printing: " + (prettyPrintStyle ? "ON" : "OFF"));
                }
                else if (codeLine.equals("toggle shallow eval")) {
                    shallowEnabled = !shallowEnabled;
                    System.out.println("Shallow evaluation: " + (shallowEnabled ? "ON" : "OFF"));
                    if (shallowEnabled) {
                        System.out.println("Definitions will be ignored in Shallow mode!");
                    }
                }
                else if (codeLine.startsWith("load ")) {
                    loadFileInput(codeLine.substring("load ".length()), finalDefs);
                }
                else {
                    try {
                        String[] parts = codeLine.split("=");
                        switch (parts.length) {
                            case 2 -> storeDefinition(parts[0].strip(), parts[1].strip(), finalDefs);
                            case 1 -> {
                                String result = executeCodeLine(parts[0].strip(), finalDefs)
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
                }
            }
        });
    }

    private static void loadFileInput(String filePath) {
        loadFileInput(filePath, null);
    }

    private static void loadFileInput(String filePath, Map<String, Preterm> definitions) {
        Path path = Paths.get(filePath);
        if (path.toFile().exists()) {
            try (Stream<String> fileStream = Files.lines(path)) {
                String code = fileStream.collect(Collectors.joining());
                executeCode(code, definitions);
            }
            catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }
        }
        else {
            System.out.println("Input file doesn't exist: \"" + filePath + "\"");
        }
    }

    private static Optional<Term> executeCodeLine(String input, Map<String, Preterm> definitions) {
        try {
            if (shallowEnabled) {
                System.out.println("--- Shallow evaluated ---");
                System.out.println(ShallowParser.parseAndEvalWithShallow(input, new HashMap<>()));
                System.out.println("--- --- --- ---");
            }
            //Parse
            Preterm parseTree = Parser.createParseTree(input, definitions);
            //TypeCheck
            Term wellTypedTree = TypeChecker.createWellTypedTree(parseTree);
            //Eval
            return Optional.of(Evaluator.eval(wellTypedTree));
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

        stayInREPL = true;
        Scanner scanner = new Scanner(System.in);

        while (stayInREPL) {
            String input = scanner.nextLine();

            if (!input.isEmpty()) {
                executeCode(input, definitions);
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
