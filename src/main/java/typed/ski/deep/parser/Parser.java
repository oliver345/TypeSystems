package typed.ski.deep.parser;

import typed.ski.deep.lang.preterm.*;
import typed.ski.deep.lang.type.*;

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
            if (input.charAt(pos) == '(') {
                if (isInAnnotatedTerm(input, pos)) {
                    appendToListAsPreterms(input.substring(firstNotProcessedPos, pos), preterms);
                    int closingPos = findClosingBracket(input, pos + 1);
                    Preterm term = createParseTree(input.substring(pos + 1, closingPos));

                    int spaceDistance = input.substring(closingPos + 2).indexOf(" ");
                    String type;
                    if (spaceDistance > -1) {
                        type = input.substring(closingPos + 2, closingPos + 2 + spaceDistance);
                        firstNotProcessedPos = closingPos + spaceDistance + 3;
                        pos = closingPos + spaceDistance + 3;
                    }
                    else {
                        type = input.substring(closingPos + 2);
                        firstNotProcessedPos = input.length();
                        pos = input.length();
                    }
                    preterms.add(new AnnotatedPreterm(term, parseType(type)));
                }
                else if(!isItInAFunction(input, pos)) {
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
            else {
                ++pos;
            }
        }

        if (firstNotProcessedPos < input.length()) {
            appendToListAsPreterms(input.substring(firstNotProcessedPos), preterms);
        }

        return preterms.size() == 1 ? preterms.get(0) : preterms.stream().reduce(App::new).orElseThrow();
    }

    private static boolean isInAnnotatedTerm(String input, int posOfOpeningBracket) {
        int closingPos = findClosingBracket(input, posOfOpeningBracket + 1);
        return closingPos < input.length() - 1 && input.charAt(closingPos + 1) == ':';
    }

    private static boolean isItInAFunction(String input, int posOfBracket) {
        String afterBracket = input.substring(posOfBracket + 1);
        return Stream.of(Ty.TypeImplementationEnum.values())
                .map(Ty.TypeImplementationEnum::getTypeName)
                .anyMatch(afterBracket::startsWith) ||
                ((input.substring(0, posOfBracket).contains("->") || input.substring(0, posOfBracket).contains(":")) && !input.substring(0, posOfBracket).contains(" "));
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
        if (token.contains(":")) {
            int indexOfColon = token.indexOf(":");
            return new AnnotatedPreterm(tokenToPreterm(token.substring(0, indexOfColon)), parseType(token.substring(indexOfColon + 1)));
        }
        else if (token.equals("S") || (token.startsWith("S") && token.contains("}{"))) {
            if (token.equals("S")) {
                return new S();
            }
            else {
                String[] parts = token.split("}\\{");
                return new S_ABC(
                        parsePreType(parts[0].substring(2)),
                        parsePreType(parts[1]),
                        parsePreType(parts[2].substring(0, parts[2].length() - 1)));
            }
        }
        /*else if (token.startsWith("S")) {
            if (token.equals("S")) {
                return new S();
            }
            else if (token.contains("}{")){
                String[] parts = token.split("}\\{");
                return new S_ABC(
                        parsePreType(parts[0].substring(2)),
                        parsePreType(parts[1]),
                        parsePreType(parts[2].substring(0, parts[2].length() - 1)));
            }
            else {
                throw new IllegalStateException("Token could not be parsed as any kind of S preterm: " + token);
            }
        }*/
        /*else if (token.startsWith("K")) {
            if (token.equals("K")) {
                return new K();
            }
            else if (token.contains("}{")){
                String[] parts = token.split("}\\{");
                String part1 = parts[0].substring(2);
                String part2 = parts[1].substring(0, parts[1].length() - 1);
                if (!part1.isEmpty() && !part2.isEmpty()) {
                    return new K_AB(parseType(part1), parseType(part2));
                }
                else if (!part1.isEmpty()) {
                    return new K_A(parseType(part1));
                }
                else if (!part2.isEmpty()) {
                    return new K_B(parseType(part2));
                }
                throw new IllegalStateException("No implicit types were given to K: " + token);
            }
            else {
                throw new IllegalStateException("Token could not be parsed as any kind of K preterm: " + token);
            }
        }*/
        else if (token.equals("K") || (token.startsWith("K") && token.contains("}{"))) {
            if (token.equals("K")) {
                return new K();
            }
            else {
                String[] parts = token.split("}\\{");
                return new K_AB(parsePreType(parts[0].substring(2)), parsePreType(parts[1].substring(0, parts[1].length() - 1)));
            }
        }
        else if (token.equals("Rec") || (token.startsWith("Rec") && token.endsWith("}"))) {
            if (token.equals("Rec")) {
                return new Rec();
            }
            else {
                return new Rec_A(parsePreType(token.substring(token.indexOf("{") + 1, token.indexOf("}"))));
            }
        }
        else if (token.equals("I") || (token.startsWith("I") && token.contains("{"))) {
            if (token.equals("I")) {
                return new I();
            }
            else {
                return new I_A(parsePreType(token.substring(2, token.length() - 1)));
            }
        }
        /*else if (token.startsWith("I")) {
            if (token.equals("I")) {
                return new I();
            }
            else if (token.contains("{")) {
                return new I_A(parsePreType(token.substring(2, token.length() - 1)));
            }
            else {
                throw new IllegalStateException("Token could not be parsed as any kind of I preterm: " + token);
            }
        }*/
        else if (token.equals("True")) {
            return new True();
        }
        else if (token.equals("False")) {
            return new False();
        }
        else if (token.equals("ITE")) {
            return new ITE();
        }
        else if (token.equals("Succ")) {
            return new Succ();
        }
        else if (token.equals("ZERO")) {
            return new ZERO();
        }
        else {
            return new Lit(token);
        }
    }

    private static PreType parsePreType(String input) {
        return input.isEmpty() ? new Unknown() : parseType(input);
    }

    private static PreType parseType(String input) {
        if (input.equals("Bool")) {
            return new Bool();
        }
        else if (input.equals("Str")) {
            return new Str();
        }
        else if (input.equals("Nat")) {
            return new Nat();
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
