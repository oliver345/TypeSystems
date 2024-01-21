package typed.ski.shallow;

import typed.ski.deep.lang.type.*;
import typed.ski.deep.parser.ParserException;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShallowParser {
    
    //TODO: definitions map should be removed from the parameters, since it is now supported here
    public static Object parseAndEvalWithShallow(String input, Map<String, String> definitions) throws ParserException {
        List<Object> terms = new ArrayList<>();

        int pos = 0;
        int firstNotProcessedPos = 0;

        while (pos < input.length()) {
            if (input.charAt(pos) == '(') {
                if (isInAnnotatedTerm(input, pos)) {
                    appendToListAsPreterms(input.substring(firstNotProcessedPos, pos), terms, definitions);
                    int closingPos = findClosingBracket(input, pos + 1);
                    Object term = parseAndEvalWithShallow(input.substring(pos + 1, closingPos), definitions);

                    int spaceDistance = input.substring(closingPos + 2).indexOf(" ");
                    if (spaceDistance > -1) {
                        firstNotProcessedPos = closingPos + spaceDistance + 3;
                        pos = closingPos + spaceDistance + 3;
                    }
                    else {
                        firstNotProcessedPos = input.length();
                        pos = input.length();
                    }
                    terms.add(term);
                }
                else if(!isItInAFunction(input, pos)) {
                    int closingPos = findClosingBracket(input, pos + 1);
                    appendToListAsPreterms(input.substring(firstNotProcessedPos, pos), terms, definitions);
                    terms.add(parseAndEvalWithShallow(input.substring(pos + 1, closingPos), definitions));
                    firstNotProcessedPos = closingPos + 1;
                    pos = closingPos + 1;
                }
                else {
                    ++pos;
                }
            }
            else if (input.charAt(pos) == '[') {
                appendToListAsPreterms(input.substring(firstNotProcessedPos, pos), terms, definitions);
                int closingPos = findEndOfList(input, pos + 1);
                String listToBeParsed = input.substring(pos + 1, closingPos);
                Object parsedList = listToBeParsed.isEmpty() ? new ArrayList<>() : parseList(listToBeParsed, definitions);
                terms.add(parsedList);
                int annotationLength = typeAnnotationLength(input, closingPos);
                //It is just a list
                if (annotationLength < 0) {
                    firstNotProcessedPos = closingPos + 1;
                    pos = closingPos + 1;
                }
                else {
                    //It is a list in an AnnotatedPreterm
                    PreType annotationType = parseType(input.substring(closingPos + 2, closingPos + 2 + annotationLength));
                    firstNotProcessedPos = closingPos + 2 + annotationLength;
                    pos = closingPos + 2 + annotationLength;
                }
            }
            else {
                ++pos;
            }
        }

        if (firstNotProcessedPos < input.length()) {
            appendToListAsPreterms(input.substring(firstNotProcessedPos), terms, definitions);
        }

        return terms.size() == 1 ? terms.get(0) : terms.stream()
                .reduce((leftTerm, rightTerm) -> ((java.util.function.Function) leftTerm).apply(rightTerm))
                .orElseThrow(() -> new ParserException("No preterm found after parsing"));
    }

    private static boolean isInAnnotatedTerm(String input, int posOfOpeningBracket) throws ParserException {
        int closingPos = findClosingBracket(input, posOfOpeningBracket + 1);
        return closingPos < input.length() - 1 && input.charAt(closingPos + 1) == ':';
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
    private static void appendToListAsPreterms(String input, List<Object> terms, Map<String, String> definitions) throws ParserException {
        List<String> tokens = getTokens(input);
        try {
            tokens.forEach(token -> {
                try {
                    terms.add(tokenToTerms(token, definitions));
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

    private static Object tokenToTerms(String token, Map<String, String> definitions) throws ParserException {
        if (token.contains(":")) {
            int indexOfColon = token.indexOf(":");
            return tokenToTerms(token.substring(0, indexOfColon), definitions);
        }
        else if (token.equals("S") || (token.startsWith("S{") && token.endsWith("}"))) {
            return ShallowSKI.s();
        }
        else if (token.equals("K") || (token.startsWith("K{") && token.endsWith("}"))) {
            return ShallowSKI.k();
        }
        else if (token.equals("RecList") || (token.startsWith("RecList{") && token.endsWith("}"))) {
            return ShallowSKI.recList();
        }
        else if (token.equals("Rec") || (token.startsWith("Rec{") && token.endsWith("}"))) {
            return ShallowSKI.rec();
        }
        else if (token.equals("I") || (token.startsWith("I{") && token.endsWith("}"))) {
            return ShallowSKI.i();
        }
        else if (token.equals("True")) {
            return true;
        }
        else if (token.equals("False")) {
            return false;
        }
        else if (token.equals("ITE")) {
            return ShallowSKI.ITE();
        }
        else if (token.equals("Succ")) {
            return ShallowSKI.succ();
        }
        else if (token.equals("ZERO")) {
            return 0;
        }
        else if (token.equals("Cons")) {
            return ShallowSKI.cons();
        }
        else if (isTokenInteger(token)) {
            int intValue = Integer.parseInt(token);
            if (intValue == 0) {
                return 0;
            }
            else {
                Object result = 0;
                for (int i = 0; i < intValue; i++) {
                    result = ShallowSKI.succ().apply((Integer) result);
                }
                return result;
            }
        }
        else if (definitions != null && definitions.containsKey(token)) {
            return parseAndEvalWithShallow(definitions.get(token), definitions);
        }
        else {
            return token;
        }
    }

    private static boolean isTokenInteger(String token) {
        try {
            return Integer.parseInt(token) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isItInAFunction(String input, int posOfBracket) {
        String afterBracket = input.substring(posOfBracket + 1);
        return Stream.of(Ty.TypeImplementationEnum.values())
                .map(Ty.TypeImplementationEnum::getTypeName)
                .anyMatch(afterBracket::startsWith) ||
                ((input.substring(0, posOfBracket).contains("->") || input.substring(0, posOfBracket).contains(":")) && !input.substring(0, posOfBracket).contains(" "));
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

    private static Object parseList(String input, Map<String, String> definitions) throws ParserException {

        List<Object> listItems = new ArrayList<>();
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
                        listItems.add(parseAndEvalWithShallow(input.substring(firstNotProcessedPos, pos).strip(), definitions));
                        firstNotProcessedPos = pos + 1;
                    }
                    ++pos;
                }
                default -> ++pos;
            }
        }

        String lastListItem = input.substring(firstNotProcessedPos);
        if (lastListItem.length() > 0) {
            listItems.add(parseAndEvalWithShallow(lastListItem.strip(), definitions));
        }

        listItems.add(new ArrayList<>());
        Collections.reverse(listItems);

        final String finalInput = input;
        return listItems.stream()
                .reduce((list, item) -> (ShallowSKI.cons().apply(item)).apply((List<Object>) list))
                .orElseThrow(() -> new ParserException("No list item found after parsing list for input \"" + finalInput + "\""));
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

    private static PreType parsePreType(String input) throws ParserException {
        return input.isEmpty() ? new Unknown() : parseType(input);
    }
}
