package typed.ski.parser;

import typed.ski.lang.preterm.*;
import typed.ski.lang.type.Bool;
import typed.ski.lang.type.Function;
import typed.ski.lang.type.Str;
import typed.ski.lang.type.Ty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {

    private static final String TEXT_IN_BRACKETS_PATTERN = "\\([^)]*\\)";

    public static Preterm createParseTree(String input) {
        List<Preterm> preterms = new ArrayList<>();

        int pos = 0;
        int firstNotProcessedPos = 0;
        while (pos < input.length()) {
            if (input.charAt(pos) == '(' && !isItInAFunction(input, pos)) {
                int closingPos = findClosingBracket(input, pos + 1);
                appendToListAsPreterms(input.substring(firstNotProcessedPos, pos), preterms);
                preterms.add(createParseTree(input.substring(pos + 1, closingPos)));
                firstNotProcessedPos = closingPos + 1;
                pos = closingPos + 1;
            }
            else {
                ++pos;
            }
        }

        if (firstNotProcessedPos < input.length()) {
            appendToListAsPreterms(input.substring(firstNotProcessedPos), preterms);
        }

        return preterms.size() == 1 ? preterms.get(0) : preterms.stream().reduce(App::new).orElseThrow();
    }

    private static boolean isItInAFunction(String input, int posOfBracket) {
        String afterBracket = input.substring(posOfBracket + 1);
        return Stream.of(Ty.TypeImplementationEnum.values())
                .map(Ty.TypeImplementationEnum::getTypeName)
                .anyMatch(afterBracket::startsWith) ||
                (input.substring(0, posOfBracket).contains("->") && !input.substring(0, posOfBracket).contains(" "));
    }

    private static int findClosingBracket(String text, int pos) {
        int diff = 1;
        while (pos < text.length()) {
            if (text.charAt(pos) == ')') {
                --diff;
            }
            else if (text.charAt(pos) == '(') {
                ++diff;
            }

            if (diff == 0) {
                return pos;
            }
            ++pos;
        }
        throw new IllegalStateException("Invalid parentheses");
    }

    //String without brackets -> tokens -> Preterm -> append one by one
    private static void appendToListAsPreterms(String input, List<Preterm> preterms) {
        List<String> tokens = getTokens(input);
        tokens.forEach(token -> preterms.add(tokenToPreterm(token)));
    }

    private static List<String> getTokens(String input) {
        return Collections.list(new StringTokenizer(input, " ")).stream()
                .map(token -> (String) token)
                .collect(Collectors.toList());
    }

    private static Preterm tokenToPreterm(String token) {
        if (token.equals("S")) {
            return new S();
        }
        else if (token.equals("K")) {
            return new K();
        }
        else if (token.equals("I")) {
            return new I();
        }
        else if (token.equals("True")) {
            return new True();
        }
        else if (token.equals("False")) {
            return new False();
        }
        else if (token.equals("ITE")) {
            return new ITE();
        }
        else if (token.contains(":")) {
            int indexOfColon = token.indexOf(":");
            return new PtmTy(tokenToPreterm(token.substring(0, indexOfColon)), parseType(token.substring(indexOfColon + 1)));
        }
        else {
            return new Lit(token);
        }
    }

    private static Ty parseType(String input) {
        if (input.equals("Bool")) {
            return new Bool();
        }
        else if (input.equals("Str")) {
            return new Str();
        }
        else if (input.contains("->")) {
            //Remove outer brackets
            if (input.charAt(0) == '(' && input.charAt(input.length() - 1) == ')'
                    && areParenthesesValid(input.substring(1, input.length() - 1))) {
                input = input.substring(1, input.length() -1);
            }

            int indexOfArrow = input.indexOf("->");

            Matcher matcher = Pattern.compile(TEXT_IN_BRACKETS_PATTERN).matcher(input);
            boolean hasBrackets = matcher.find();

            if (!hasBrackets || !(indexOfArrow > matcher.start() && indexOfArrow < matcher.end())) {
                return new Function(parseType(input.substring(0, indexOfArrow)), parseType(input.substring(indexOfArrow + 2)));
            }
            else {
                if (input.length() > matcher.end()) {
                    return new Function(parseType(input.substring(matcher.start() + 1, matcher.end() - 1)), parseType(input.substring(matcher.end() + 2)));
                }
                else {
                    return parseType(input.substring(matcher.start() + 1, matcher.end() - 1));
                }
            }
        }
        throw new IllegalStateException("Could not parse to Ty, invalid token: " + input);
    }

    private static boolean areParenthesesValid(String input) {
        int bracketOpened = 0;
        int bracketClosed = 0;
        int index = 0;
        boolean validParentheses = true;
        while (validParentheses && index < input.length()) {
            if (input.charAt(index) == '(') {
                ++bracketOpened;
            }
            else if (input.charAt(index) == ')'){
                ++bracketClosed;
            }

            validParentheses = bracketOpened >= bracketClosed;
            ++index;
        }

        return validParentheses;
    }
}
