package ski;

import org.apache.commons.lang3.tuple.Pair;
import ski.term.*;

import java.util.InputMismatchException;

public class SKI {

    public static Term eval(Term term) {
        if (term instanceof S || term instanceof K || term instanceof I) {
            return term;
        }
        else if (term instanceof Application) {
            return ((Application) term).apply();
        }
        throw new IllegalStateException();
    }

    public static Term parseFromString(String expression) {

        if (areParenthesesValid(expression)) {
            expression = cleanExpression(expression);
            return buildTermFromExpression(expression);
        }
        else {
            throw new InputMismatchException("Input text contains invalid or empty parentheses");
        }
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

    //Removes closing brackets, dollar signs and the unnecessary other brackets if exists
    private static String cleanExpression(String expression) {

        if (expression.charAt(0) == '(' && expression.charAt(expression.length() - 1) == ')'
                && areParenthesesValid(expression.substring(1, expression.length() - 1))) {
            expression = expression.substring(1, expression.length() -1);
        }

        expression = expression.replaceAll("\\((?=.\\))", "");

        return expression.replaceAll("[$)]", "");
    }

    private static Term buildTermFromExpression(String expression) {

        //Initial term, will be removed at the end
        Application syntaxTree = new Application(null, null);

        while (!expression.isEmpty()) {

            Pair<Term, String> termAndLeftover = readTerm(expression);
            syntaxTree = new Application(syntaxTree, termAndLeftover.getLeft());
            expression = termAndLeftover.getRight();
        }

        return removeInitialApplication(syntaxTree);
    }

    private static Term removeInitialApplication(Application syntaxTree) {

        if (((Application) syntaxTree.getLeftTerm()).getLeftTerm() == null) {
            return syntaxTree.getRightTerm();
        }
        else {
            if (((Application) ((Application) syntaxTree.getLeftTerm()).getLeftTerm()).getLeftTerm() == null){
                syntaxTree.setLeftTerm(((Application) syntaxTree.getLeftTerm()).getRightTerm());
            }
            else {
                Term leftTerm = syntaxTree.getLeftTerm();
                syntaxTree.setLeftTerm(removeInitialApplication((Application) leftTerm));
            }
            return syntaxTree;
        }
    }

    private static Pair<Term, String> readTerm(String expression) {

        if (expression.startsWith("(")) {

            Pair<Term, String> leftResult = readTerm(expression.substring(1));
            Pair<Term, String> rightResult = readTerm(leftResult.getRight());
            return Pair.of(new Application(leftResult.getLeft(), rightResult.getLeft()), rightResult.getRight());
        }
        else {
            return Pair.of(parseTermFromChar(expression.charAt(0)), expression.substring(1));
        }
    }

    private static Term parseTermFromChar(char letter) {
        switch (letter) {
            case 'S':
                return new S();
            case 'K':
                return new K();
            case 'I':
                return new I();
        }
        throw new IllegalArgumentException();
    }
}
