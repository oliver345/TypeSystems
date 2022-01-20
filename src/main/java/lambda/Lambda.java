package lambda;

import lambda.term.Application;
import lambda.term.Lam;
import lambda.term.Term;
import lambda.term.Var;
import ski.term.I;
import ski.term.K;
import ski.term.S;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Lambda {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final List<Character> ALL_CHARACTERS;

    private static final List<Character> AVAILABLE_CHARACTERS = new ArrayList<>();

    static {
        ALL_CHARACTERS = ALPHABET.chars()
                .mapToObj(character -> (char) character)
                .collect(Collectors.toList());
        AVAILABLE_CHARACTERS.addAll(ALL_CHARACTERS);
    }

    public static Term eval(Term term) {
        if (term instanceof Var) {
            return term;
        }

        if (term instanceof Lam) {
            return new Lam(((Lam) term).getVar(), eval(((Lam) term).getTerm()));
        }

        if (term instanceof Application) {
            return new Application(eval(((Application) term).getLeftTerm()), eval(((Application) term).getRightTerm())).apply();
        }

        throw new IllegalStateException();
    }

    public static Term evalExpression(Term term) {
        resetAvailableCharacters();
        removeUsedCharacters(term);

        return eval(term);
    }

    public static Term fromSKI(ski.term.Term ski) {
        if (ski instanceof ski.term.Var) {
            return new Var(((ski.term.Var) ski).getName());
        }

        if (ski instanceof S) {
            Var varX = new Var('x');
            Var varY = new Var('y');
            Var varZ = new Var('z');
            Lam lamZ = new Lam(varZ, new Application(new Application(varX, varZ), new Application(varY, varZ)));
            varZ.setBinder(lamZ);
            Lam lamY = new Lam(varY, lamZ);
            varY.setBinder(lamY);
            Lam lamX = new Lam(varX, lamY);
            varX.setBinder(lamX);
            return lamX;
        }

        if (ski instanceof K) {
            Var varX = new Var('x');
            Var varY = new Var('y');
            Lam lamY = new Lam(varY, varX);
            Lam lamX = new Lam(varX, lamY);
            varX.setBinder(lamX);
            return lamX;
        }

        if (ski instanceof I) {
            Var varX = new Var('x');
            Lam lam = new Lam(varX, varX);
            varX.setBinder(lam);
            return lam;
        }

        if (ski instanceof ski.term.Application) {
            return new Application(fromSKI(((ski.term.Application) ski).getLeftTerm()), fromSKI(((ski.term.Application) ski).getRightTerm()));
        }

        throw new IllegalStateException();
    }

    public static Term substitute(Term term, Var oldVar, Term newTerm) {
        if (term instanceof Var) {
            if (((Var) term).getName() == oldVar.getName()) {
                return newTerm;
            }
            else {
                return term;
            }
        }

        if (term instanceof Lam) {
            return new Lam(new Var(findUniqueVariableName()), substitute(((Lam) renameVariable(term, ((Lam) term).getVar(),
                    new Var(findUniqueVariableName()))).getTerm(), oldVar, newTerm));
        }

        if (term instanceof Application) {
            return new Application(substitute(((Application) term).getLeftTerm(), oldVar, newTerm),
                    substitute(((Application) term).getRightTerm(), oldVar, newTerm));
        }

        throw new IllegalStateException();
    }

    private static Term renameVariable(Term term, Var oldVar, Var newVar) {
        if (oldVar.getName() != newVar.getName()) {
            if (term instanceof Var) {
                if (((Var) term).getName() == oldVar.getName()) {
                    return newVar;
                }
                else {
                    return term;
                }
            }

            if (term instanceof Lam) {
                if (((Lam) term).getVar().getName() == oldVar.getName()) {
                    return new Lam(newVar, renameVariable(((Lam) term).getTerm(), oldVar, newVar));
                }
                else {
                    return new Lam(((Lam) term).getVar(), renameVariable(((Lam) term).getTerm(), oldVar, newVar));
                }
            }

            if (term instanceof Application) {
                return new Application(renameVariable(((Application) term).getLeftTerm(), oldVar, newVar),
                        renameVariable(((Application) term).getRightTerm(), oldVar, newVar));
            }

            throw new IllegalStateException();
        }
        return term;
    }

    private static void resetAvailableCharacters() {
        AVAILABLE_CHARACTERS.clear();
        AVAILABLE_CHARACTERS.addAll(ALL_CHARACTERS);
    }

    private static void removeUsedCharacters(Term term) {
        if (term instanceof Var) {
            AVAILABLE_CHARACTERS.remove(Character.valueOf(((Var) term).getName()));
        }

        if (term instanceof Lam) {
            AVAILABLE_CHARACTERS.remove(Character.valueOf(((Lam) term).getVar().getName()));
            removeUsedCharacters(((Lam) term).getTerm());
        }

        if (term instanceof Application) {
            removeUsedCharacters(((Application) term).getLeftTerm());
            removeUsedCharacters(((Application) term).getRightTerm());
        }
    }

    private static char findUniqueVariableName() {

        //Todo: ADD EXCEPTION!
        return AVAILABLE_CHARACTERS.get(0);
    }
}
