package typed.ski.deep.parser;

import typed.ski.deep.lang.preterm.*;
import typed.ski.deep.lang.type.*;

import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {

    private static final String TEXT_IN_BRACKETS_PATTERN = "\\([^)]*\\)";

    public static Preterm createParseTree(String input, Map<String, Preterm> definitions) {
        List<Preterm> preterms = new ArrayList<>();

        int pos = 0;
        int firstNotProcessedPos = 0;

        while (pos < input.length()) {
            if (input.charAt(pos) == '(') {
                if (isInAnnotatedTerm(input, pos)) {
                    appendToListAsPreterms(input.substring(firstNotProcessedPos, pos), preterms, definitions);
                    int closingPos = findClosingBracket(input, pos + 1);
                    Preterm term = createParseTree(input.substring(pos + 1, closingPos), definitions);

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
                    appendToListAsPreterms(input.substring(firstNotProcessedPos, pos), preterms, definitions);
                    preterms.add(createParseTree(input.substring(pos + 1, closingPos), definitions));
                    firstNotProcessedPos = closingPos + 1;
                    pos = closingPos + 1;
                }
                else {
                    ++pos;
                }
            }
            else if (input.charAt(pos) == '[') {
                appendToListAsPreterms(input.substring(firstNotProcessedPos, pos), preterms, definitions);
                int closingPos = findEndOfList(input, pos + 1);
                String listToBeParsed = input.substring(pos + 1, closingPos);
                Preterm parsedList = listToBeParsed.isEmpty() ? new EmptyListPre() : parseList(listToBeParsed, definitions);
                preterms.add(parsedList);
                firstNotProcessedPos = closingPos + 1;
                pos = closingPos + 1;
            }
            else {
                ++pos;
            }
        }

        if (firstNotProcessedPos < input.length()) {
            appendToListAsPreterms(input.substring(firstNotProcessedPos), preterms, definitions);
        }

        return preterms.size() == 1 ? preterms.get(0) : preterms.stream().reduce(App::new).orElseThrow();
    }

    private static int findEndOfList(String text, int pos) {
        int diff = 1;
        while (pos < text.length()) {
            if (text.charAt(pos) == ']') {
                --diff;
            }
            else if (text.charAt(pos) == '[') {
                ++diff;
            }

            if (diff == 0) {
                return pos;
            }
            ++pos;
        }
        throw new IllegalStateException("Invalid parentheses");
    }

    private static Preterm parseList(String input, Map<String, Preterm> definitions) {

        List<Preterm> listItems = new ArrayList<>();
        while (!input.isEmpty()) {
            if (input.startsWith("[")) {
                int closingPos = findEndOfList(input, 1);
                listItems.add(parseList(input.substring(1, closingPos), definitions));
                input = input.substring(closingPos + 1 == input.length() ? closingPos + 1 : closingPos + 2).strip();
            }
            else {
                int listItemSeparator = findListItemSeparator(input);
                listItems.add(createParseTree(input.substring(0, listItemSeparator), definitions));
                input = input.substring(listItemSeparator == input.length() ? listItemSeparator : listItemSeparator + 1).strip();
            }
        }

        listItems.add(new EmptyListPre());
        Collections.reverse(listItems);

        return listItems.stream()
                .reduce((list, item) -> new ListItemPre(item, (ListItemPre) list))
                .orElseThrow();
    }

    private static int findListItemSeparator(String input) {
        if (input.contains(",")) {
            int pos = 0;
            boolean notFound = true;
            while (pos < input.length() && notFound) {
                notFound = input.charAt(pos) != ',' || !areParenthesesValid(input.substring(0, pos), false);
                ++pos;
            }
            if (!notFound) {
                return pos - 1;
            } else {
                throw new IllegalStateException("Syntax error in list: " + input);
            }
        }
        else {
            return input.length();
        }
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
    private static void appendToListAsPreterms(String input, List<Preterm> preterms, Map<String, Preterm> definitions) {
        List<String> tokens = getTokens(input);
        tokens.forEach(token -> preterms.add(tokenToPreterm(token, definitions)));
    }

    private static List<String> getTokens(String input) {
        return Collections.list(new StringTokenizer(input, " ")).stream()
                .map(token -> (String) token)
                .collect(Collectors.toList());
    }

    private static Preterm tokenToPreterm(String token, Map<String, Preterm> definitions) {
        if (token.contains(":")) {
            int indexOfColon = token.indexOf(":");
            return new AnnotatedPreterm(tokenToPreterm(token.substring(0, indexOfColon), definitions), parseType(token.substring(indexOfColon + 1)));
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
        else if (token.equals("K") || (token.startsWith("K") && token.contains("}{"))) {
            if (token.equals("K")) {
                return new K();
            }
            else {
                String[] parts = token.split("}\\{");
                return new K_AB(parsePreType(parts[0].substring(2)), parsePreType(parts[1].substring(0, parts[1].length() - 1)));
            }
        }
        else if (token.equals("RecList") || (token.startsWith("RecList") && token.contains("}{"))) {
            if (token.equals("RecList")) {
                return new RecListPre();
            }
            else {
                String[] parts = token.split("}\\{");
                return new RecListPre_AB(parsePreType(parts[0].substring(8)), parsePreType(parts[1].substring(0, parts[1].length() - 1)));
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
        else if (token.equals("Cons")) {
            return new ConsPre();
        }
        else if (definitions != null && definitions.containsKey(token)) {
            return definitions.get(token);
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
                    && areParenthesesValid(input.substring(1, input.length() - 1), true)) {
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

    private static boolean areParenthesesValid(String input, boolean roundParentheses) {
        char opening, closing;
        if (roundParentheses) {
            opening = '(';
            closing = ')';
        }
        else {
            opening = '[';
            closing = ']';
        }

        int bracketOpened = 0;
        int bracketClosed = 0;
        int index = 0;
        boolean validParentheses = true;
        while (validParentheses && index < input.length()) {
            if (input.charAt(index) == opening) {
                ++bracketOpened;
            }
            else if (input.charAt(index) == closing){
                ++bracketClosed;
            }

            validParentheses = bracketOpened >= bracketClosed;
            ++index;
        }

        return validParentheses;
    }
}
