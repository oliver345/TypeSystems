package typed.ski.deep;

import typed.ski.deep.evaluator.Evaluator;
import typed.ski.deep.lang.preterm.Preterm;
import typed.ski.deep.lang.term.Term;
import typed.ski.deep.parser.Parser;
import typed.ski.deep.typechecker.TypeChecker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SKI {

    public static final String le = "Rec (K True) (K (S (K (Rec False))(S (K K))))";

    private static final List<String> reservedTokens = List.of("S", "K", "I", "False", "True", "ITE", "Rec", "Succ",
            "ZERO", "Bool", "Nat", "Str", "RecList", "Cons");

    public static void executeCode(String input) {
        String[] codeLines = input.split(";");
        Map<String, Preterm> definitions = new HashMap<>();

        Stream.of(codeLines).forEach(codeLine -> {
            String[] parts = codeLine.split("=");
            switch (parts.length) {
                case 2:
                    storeDefinition(parts[0], parts[1], definitions);
                    break;
                case 1:
                    Term result = executeCodeLine(parts[0], definitions);
                    System.out.println(parts[0] + " ==> " + result);
                    break;
                default:
                    throw new IllegalStateException("Invalid statement: " + codeLine);
            }
        });
    }

    public static Term executeCodeLine(String input, Map<String, Preterm> definitions) {
        return Evaluator.eval(TypeChecker.createWellTypedTree(Parser.createParseTree(input, definitions)));
    }

    private static void storeDefinition(String key, String statement, Map<String, Preterm> definitions) {
        if (!reservedTokens.contains(key) && key.matches("[^(:)\\]\\[<>{}=\\s-]+")) {
            definitions.put(key, Parser.createParseTree(statement, definitions));
            System.out.println(key + " = " + statement);
        }
        else {
            throw new IllegalStateException("Invalid definition key: " + key);
        }
    }
}
