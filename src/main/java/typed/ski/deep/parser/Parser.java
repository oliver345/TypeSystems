package typed.ski.deep.parser;

import typed.ski.deep.lang.preterm.*;
import typed.ski.deep.lang.type.*;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {

    private static final String TEXT_IN_BRACKETS_PATTERN = "\\([^)]*\\)";

    public static Preterm createParseTree(String input, Map<String, Preterm> definitions) throws ParserException {
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

                int annotationLength = typeAnnotationLength(input, closingPos);
                //It is just a list
                if (annotationLength < 0) {
                    preterms.add(parsedList);
                    firstNotProcessedPos = closingPos + 1;
                    pos = closingPos + 1;
                }
                else {
                    //It is a list in an AnnotatedPreterm
                    PreType annotationType = parseType(input.substring(closingPos + 2, closingPos + 2 + annotationLength));
                    preterms.add(new AnnotatedPreterm(parsedList, annotationType));
                    firstNotProcessedPos = closingPos + 2 + annotationLength;
                    pos = closingPos + 2 + annotationLength;
                }
            }
            else {
                ++pos;
            }
        }

        if (firstNotProcessedPos < input.length()) {
            appendToListAsPreterms(input.substring(firstNotProcessedPos), preterms, definitions);
        }

        return preterms.size() == 1 ? preterms.get(0) : preterms.stream()
                .reduce(App::new)
                .orElseThrow(() -> new ParserException("No preterm found after parsing"));
    }

    //Checks if the list is a part of an AnnotatedPreterm
    //It is if there is a ":<Type> part after the list closing bracket
    private static int typeAnnotationLength(String input, int posOfListClosing) {
        if (input.length() > posOfListClosing + 2 && input.charAt(posOfListClosing + 1) == ':') {
            input = input.substring(posOfListClosing + 2);
            int posOfSpace = input.indexOf(" ");
            input = input.substring(0, posOfSpace < 0 ? input.length() : posOfSpace);
            return input.length();
        }
        return -1;
    }

    private static int findEndOfList(String text, int pos) throws ParserException {
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
        throw new ParserException("Incorrect list syntax \"" + text + "\"");
    }

    private static Preterm parseList(String input, Map<String, Preterm> definitions) throws ParserException {

        List<Preterm> listItems = new ArrayList<>();
        int pos = 0;
        int bracketCount = 0;
        int firstNotProcessedPos = 0;

        while(pos < input.length()) {
            switch (input.charAt(pos)) {
                case '[', '(' -> {
                    ++bracketCount;
                    ++pos;
                }
                case ']', ')' -> {
                    --bracketCount;
                    ++pos;
                }
                case ',' -> {
                    if (bracketCount == 0) {
                        listItems.add(createParseTree(input.substring(firstNotProcessedPos, pos).strip(), definitions));
                        firstNotProcessedPos = pos + 1;
                    }
                    ++pos;
                }
                default -> ++pos;
            }
        }

        String lastListItem = input.substring(firstNotProcessedPos);
        if (lastListItem.length() > 0) {
            listItems.add(createParseTree(lastListItem.strip(), definitions));
        }

        listItems.add(new EmptyListPre());
        Collections.reverse(listItems);

        final String finalInput = input;
        return listItems.stream()
                .reduce((list, item) -> new App(new App(new ConsPre(), item), list))
                .orElseThrow(() -> new ParserException("No list item found after parsing list for input \"" + finalInput + "\""));
    }

    private static boolean isInAnnotatedTerm(String input, int posOfOpeningBracket) throws ParserException {
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

    private static int findClosingBracket(String text, int pos) throws ParserException {
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
        throw new ParserException("Invalid parentheses in \"" + text + "\"");
    }

    //String without brackets -> tokens -> Preterm -> append one by one
    private static void appendToListAsPreterms(String input, List<Preterm> preterms, Map<String, Preterm> definitions) throws ParserException {
        List<String> tokens = getTokens(input);
        try {
            tokens.forEach(token -> {
                try {
                    preterms.add(tokenToPreterm(token, definitions));
                } catch (ParserException parserException) {
                    throw new RuntimeException(parserException);
                }
            });
        }
        catch (Exception exception) {
            throw new ParserException(exception);
        }
    }

    private static List<String> getTokens(String input) {
        return Collections.list(new StringTokenizer(input, " ")).stream()
                .map(token -> (String) token)
                .collect(Collectors.toList());
    }

    private static Preterm tokenToPreterm(String token, Map<String, Preterm> definitions) throws ParserException {
        if (token.contains(":")) {
            int indexOfColon = token.indexOf(":");
            return new AnnotatedPreterm(tokenToPreterm(token.substring(0, indexOfColon), definitions), parseType(token.substring(indexOfColon + 1)));
        }
        else if (token.equals("S") || (token.startsWith("S{") && token.endsWith("}"))) {
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
        else if (token.equals("K") || (token.startsWith("K{") && token.endsWith("}"))) {
            if (token.equals("K")) {
                return new K();
            }
            else {
                String[] parts = token.split("}\\{");
                return new K_AB(parsePreType(parts[0].substring(2)), parsePreType(parts[1].substring(0, parts[1].length() - 1)));
            }
        }
        else if (token.equals("RecList") || (token.startsWith("RecList{") && token.endsWith("}"))) {
            if (token.equals("RecList")) {
                return new RecListPre();
            }
            else {
                String[] parts = token.split("}\\{");
                return new RecListPre_AB(parsePreType(parts[0].substring(8)), parsePreType(parts[1].substring(0, parts[1].length() - 1)));
            }
        }
        else if (token.equals("Rec") || (token.startsWith("Rec{") && token.endsWith("}"))) {
            if (token.equals("Rec")) {
                return new Rec();
            }
            else {
                return new Rec_A(parsePreType(token.substring(token.indexOf("{") + 1, token.indexOf("}"))));
            }
        }
        else if (token.equals("I") || (token.startsWith("I{") && token.endsWith("}"))) {
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
        else if (isTokenInteger(token)) {
            int intValue = Integer.parseInt(token);
            if (intValue == 0) {
                return new ZERO();
            }
            else {
                Preterm result = new ZERO();
                for (int i = 0; i < intValue; i++) {
                    result = new App(new Succ(), result);
                }
                return result;
            }
        }
        else if (definitions != null && definitions.containsKey(token)) {
            return definitions.get(token);
        }
        else {
            return new Lit(token);
        }
    }

    private static boolean isTokenInteger(String token) {
        try {
            return Integer.parseInt(token) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static PreType parsePreType(String input) throws ParserException {
        return input.isEmpty() ? new Unknown() : parseType(input);
    }

    private static PreType parseType(String input) throws ParserException {
        if (input.equals("Bool")) {
            return new Bool();
        }
        else if (input.equals("Nat")) {
            return new Nat();
        }
        else if (input.equals("Str")) {
            return new Str();
        }
        else if (input.contains("->")) {
            //Remove outer brackets
            if (input.charAt(0) == '(' && input.charAt(input.length() - 1) == ')'
                    && areParenthesesValid(input.substring(1, input.length() - 1), true)) {
                input = input.substring(1, input.length() -1);
            }

            int indexOfArrow = input.indexOf("->");
            int indexOfBracketOpen = input.indexOf('(');
            int indexOfBracketClose = -1;
            boolean hasBrackets = false;
            if (indexOfBracketOpen >= 0) {
                hasBrackets = true;
                indexOfBracketClose = findClosingBracket(input, indexOfBracketOpen + 1);
            }

            if (!hasBrackets || !(indexOfArrow > indexOfBracketOpen && indexOfArrow < indexOfBracketClose)) {
                return new Function(parseType(input.substring(0, indexOfArrow)), parseType(input.substring(indexOfArrow + 2)));
            }
            else {
                if (input.length() > indexOfBracketClose + 1) {
                    return new Function(parseType(input.substring(indexOfBracketOpen + 1, indexOfBracketClose)), parseType(input.substring(indexOfBracketClose + 3)));
                }
                else {
                    return parseType(input.substring(indexOfBracketOpen + 1, indexOfBracketClose));
                }
            }
        }
        else if (input.startsWith("List") && input.endsWith("}")) {
            PreType typeParam = parsePreType(input.substring(5, input.length() - 1));
            return new typed.ski.deep.lang.type.List(typeParam);
        }

        throw new ParserException("\"" + input + "\" can not be parsed to any type");
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
