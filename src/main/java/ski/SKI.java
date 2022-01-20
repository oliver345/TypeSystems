package ski;

import lambda.term.Lam;
import ski.term.*;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Stack;

public class SKI {

    public static Term eval(Term term) {
        if (term instanceof S || term instanceof K || term instanceof I || term instanceof Var) {
            return term;
        }
        else if (term instanceof Application) {
            return new Application(eval(((Application) term).getLeftTerm()), eval(((Application) term).getRightTerm())).apply();
        }
        throw new IllegalStateException();
    }

    public static Term parseFromString(String expression) {
        if (containsValidCharacters(expression)) {
            if (areParenthesesValid(expression)) {
                expression = cleanExpression(expression);
                return buildTermFromExpression(expression);
            }
            else {
                throw new InputMismatchException("Input text contains invalid or empty parentheses");
            }
        }
        else {
            throw new InputMismatchException("Input text contains invalid characters");
        }
    }

    public static Term fromLambda(lambda.term.Term lambda) {
        if (lambda instanceof lambda.term.Var) {
            return new Var(((lambda.term.Var) lambda).getName());
        }

        if (lambda instanceof lambda.term.Application) {
            return new Application(fromLambda(((lambda.term.Application) lambda).getLeftTerm()), fromLambda(((lambda.term.Application) lambda).getRightTerm()));
        }

        if (lambda instanceof Lam) {
            return convertLambda(((Lam) lambda).getVar(), fromLambda(((Lam) lambda).getTerm()));
        }
        throw new IllegalStateException();
    }

    private static Term convertLambda(lambda.term.Var var, Term term) {
        if (term instanceof Var) {
            if (var.getName() == ((Var) term).getName()) {
                return new I();
            }
            else {
                return new Application(new K(), term);
            }
        }

        if (term instanceof S || term instanceof K) {
            return new Application(new K(), term);
        }

        if (term instanceof Application) {
            return new Application(new Application(new S(), convertLambda(var, ((Application) term).getLeftTerm())),
                    convertLambda(var, ((Application) term).getRightTerm()));
        }

        //Todo: check
        if (term instanceof I) {
            return new I();
        }
        throw new IllegalStateException();
    }

    private static boolean areParenthesesValid(String expression) {
        int bracketOpened = 0;
        int bracketClosed = 0;
        int index = 0;
        boolean validParentheses = true;
        while (validParentheses && index < expression.length()) {
            if (expression.charAt(index) == '(') {
                ++bracketOpened;
            }
            else if (expression.charAt(index) == ')'){
                ++bracketClosed;
            }

            validParentheses = bracketOpened >= bracketClosed;

            //Checks for empty brackets
            if (index < expression.length() - 1 && expression.charAt(index) == '(' && expression.charAt(index + 1) == ')') {
                validParentheses = false;
            }

            ++index;
        }

        return validParentheses;
    }

    private static boolean containsValidCharacters(String expression) {
        return expression.matches("[a-zA-Z()$]*");
    }

    //Removes dollar signs and the unnecessary brackets
    private static String cleanExpression(String expression) {
        if (expression.charAt(0) == '(' && expression.charAt(expression.length() - 1) == ')'
                && areParenthesesValid(expression.substring(1, expression.length() - 1))) {
            expression = expression.substring(1, expression.length() -1);
        }

        return expression.replaceAll("\\$", "");
    }

    private static Term buildTermFromExpression(String expression) {
        String polishNotation = toPolishForm(expression);
        List<Term> terms = new ArrayList<>();
        terms.add(new Application(null, null));

        int position = 0;
        while (position < polishNotation.length()) {
            if (polishNotation.charAt(position) == '$') {
                Application application = new Application(terms.get(terms.size() - 2), terms.get(terms.size() -1));
                terms = terms.subList(0, terms.size() - 2);
                terms.add(application);
            }
            else {
                terms.add(parseTermFromChar(polishNotation.charAt(position)));
            }
            ++position;
        }

        terms.remove(0);
        return terms.size() == 1 ? terms.get(0) : terms.stream().reduce(Application::new).orElseThrow();
    }

    private static Term parseTermFromChar(char character) {
        switch (character) {
            case 'S':
                return new S();
            case 'K':
                return new K();
            case 'I':
                return new I();
            default:
                return new Var(character);
        }
    }

    private static String toPolishForm(String expression) {
        Stack<Character> stack = new Stack<>();
        StringBuilder polishNotation = new StringBuilder();
        int position = 0;
        while (position < expression.length()) {

            char character = expression.charAt(position);
            switch (character) {
                case '(':
                    if (position - 1 >= 0 && expression.charAt(position - 1) != '(') {
                        addApplicationSymbol(stack, polishNotation);
                    }
                    stack.push(character);
                    break;
                case ')':
                    while (!stack.peek().equals('(')) {
                        polishNotation.append(stack.pop());
                    }
                    stack.pop();
                    break;
                default:
                    if (position - 1 >= 0 && expression.charAt(position - 1) != '(') {
                        addApplicationSymbol(stack, polishNotation);
                    }
                    polishNotation.append(character);
                    break;
            }
            ++position;
        }

        return polishNotation.toString();
    }

    private static void addApplicationSymbol(Stack<Character> stack, StringBuilder polishNotation) {
        while (!stack.isEmpty() && !stack.peek().equals('(')) {
            polishNotation.append(stack.pop());
        }
        stack.push('$');
    }
}
