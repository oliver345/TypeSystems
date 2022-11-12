package typed.ski.deep;

import typed.ski.deep.evaluator.Evaluator;
import typed.ski.deep.lang.preterm.Preterm;
import typed.ski.deep.lang.term.Term;
import typed.ski.deep.parser.Parser;
import typed.ski.deep.typechecker.TypeChecker;

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

    public static void main(String[] args) {
        Optional<String> optionalArg = Stream.of(args)
                .filter(arg -> arg.startsWith("-eval"))
                .findFirst();

        if (optionalArg.isPresent()) {
            String argInput = optionalArg.get();
            int posOfEqual = argInput.indexOf("=");
            if (posOfEqual > -1) {
                String sourcePath = argInput.substring(posOfEqual + 1);
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
                throw new IllegalArgumentException(argInput);
            }
        }
        else {
            runInREPL();
        }
    }

    public static void executeCode(String input) {
        executeCode(input, null);
    }

    public static void executeCode(String input, Map<String, Preterm> definitions) {
        String[] codeLines = input.split(";");
        final Map<String, Preterm> finalDefs = definitions == null ? new LinkedHashMap<>() : definitions;

        Stream.of(codeLines).forEach(codeLine -> {
            String[] parts = codeLine.trim().split("=");
            switch (parts.length) {
                case 2 -> storeDefinition(parts[0], parts[1], finalDefs);
                case 1 -> {
                    Term result = executeCodeLine(parts[0], finalDefs);
                    System.out.println(parts[0] + " ==> " + result);
                }
                default -> throw new IllegalStateException("Invalid statement: " + codeLine);
            }
        });
    }

    public static Term executeCodeLine(String input, Map<String, Preterm> definitions) {
        return Evaluator.eval(TypeChecker.createWellTypedTree(Parser.createParseTree(input, definitions)));
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
                case "list defs" -> definitions.forEach((key, value) -> System.out.println(key.concat(" = ").concat(value.toString())));
                case "" -> {}
                default -> executeCode(input, definitions);
            }
        }
    }

    private static void storeDefinition(String key, String statement, Map<String, Preterm> definitions) {
        if (!reservedTokens.contains(key) && key.matches("[^(:)\\]\\[<>{}=\\s-]+")) {
            definitions.put(key, Parser.createParseTree(statement, definitions));
            System.out.println("Definition stored:\n" + key + " = " + statement);
        }
        else {
            throw new IllegalStateException("Invalid definition key: " + key);
        }
    }
}
