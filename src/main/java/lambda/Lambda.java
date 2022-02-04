package lambda;

import lambda.data.Closure;
import lambda.data.term.Application;
import lambda.data.term.Lam;
import lambda.data.term.Term;
import lambda.data.term.Var;
import lambda.data.val.VApp;
import lambda.data.val.VLam;
import lambda.data.val.VVar;
import lambda.data.val.Val;
import org.apache.commons.lang3.tuple.Pair;
import ski.term.I;
import ski.term.K;
import ski.term.S;

import java.util.*;
import java.util.stream.Collectors;

public class Lambda {

    public static final Term NUM_0;

    private static final Term SUCC;

    private static final Term ADD;

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final List<Character> ALL_CHARACTERS;

    private static final List<Character> AVAILABLE_CHARACTERS = new ArrayList<>();

    static {
        NUM_0 = new Lam(new Var('f'), new Lam(new Var('x'), new Var('x')));

        SUCC = new Lam(new Var('n'),
                new Lam(new Var('f'),
                  new Lam(new Var('x'),
                new Application(
                        new Var('f'), new Application(
                                new Application(new Var('n'), new Var('f')), new Var('x'))))));

        /*
        ADD = new Lam(new Var('m'), new Lam(new Var('n'), new Lam(new Var('f'), new Lam(new Var('x'),
                new Application(new Var('m'), new Application(new Var('f'), new Application(new Var('n'),
                        new Application(new Var('f'), new Var('x')))))))));
        */

        ADD = new Lam(new Var('m'),
                new Lam(new Var('n'),
                  new Lam(new Var('f'),
                    new Lam(new Var('x'),
                new Application(
                        new Application(new Var('m'), new Var('f')),
                        new Application(
                                new Application(new Var('n'), new Var('f')), new Var('x')))))));

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

    public static Term evalUntilFinal(Term term) {
        StringJoiner stringJoiner = new StringJoiner(" ==>\n");
        stringJoiner.add(term.toString());
        Term previous = term;
        Term result = evalExpression(term);
        while (!equals(previous, result)) {
            stringJoiner.add(result.toString());
            previous = result;
            result = eval(result);
        }
        System.out.println(stringJoiner.toString());
        return result;
    }

    public static Val advancedEval(Map<Character, Val> env, Term term) {
        if (term instanceof Var) {
            return env.get(((Var) term).getName());
        }

        if (term instanceof Lam) {
            return new VLam(new Closure(((Lam) term).getVar().getName(), env, ((Lam) term).getTerm()));
        }

        /*
         App t u   -> case (eval env t, eval env u) of
                 (VLam cl, u) -> appCl cl u       <--- u Term?  &&    eval env u =>  Val  ?
                 (t      , u) -> VApp t u

         */
        if (term instanceof Application) {
            Val leftEvaluated = advancedEval(env, ((Application) term).getLeftTerm());
            Val rightEvaluated = advancedEval(env, ((Application) term).getRightTerm());
            if (leftEvaluated instanceof VLam) {
                return appCl(((VLam) leftEvaluated).getClosure(), rightEvaluated);
            }
            else {
                return new VApp(leftEvaluated, rightEvaluated);
            }
        }
        throw new IllegalStateException();
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
            Var newVar = new Var(findUniqueVariableName());
            return new Lam(newVar,
                    substitute(renameVariable(((Lam) term).getTerm(), ((Lam) term).getVar(), newVar), oldVar, newTerm));
        }

        if (term instanceof Application) {
            return new Application(substitute(((Application) term).getLeftTerm(), oldVar, newTerm),
                    substitute(((Application) term).getRightTerm(), oldVar, newTerm));
        }

        throw new IllegalStateException();
    }

    public static Term succ(Term term) {
        return new Lam(new Var('f'), new Lam(new Var('x'), new Application(new Var('f'),
                new Application(new Application(term, new Var('f')), new Var('x')))));
    }

    public static Term succ2(Term term) {
        return new Application(SUCC, term);
    }

    public static Term add(Term term, Term otherTerm) {
        return new Application(new Application(ADD, term), otherTerm);
    }

    public static Term add2(Term term, Term otherTerm) {
        return new Lam(new Var('f'), new Lam(new Var('x'), new Application(new Application(term,
                new Var('f')), new Application(new Application(otherTerm, new Var('f')), new Var('x')))));
    }

    public static boolean equals(Term term, Term otherTerm) {
        if (term.getClass().equals(otherTerm.getClass())) {
            if (term instanceof Var) {
                return ((Var) term).getName() == ((Var) otherTerm).getName();
            }

            if (term instanceof Application) {
                return equals(((Application) term).getLeftTerm(), ((Application) otherTerm).getLeftTerm()) &&
                        equals(((Application) term).getRightTerm(), ((Application) otherTerm).getRightTerm());
            }

            if (term instanceof Lam) {
                if (((Lam) term).getVar().getName() != ((Lam) otherTerm).getVar().getName()) {
                    otherTerm = renameVariable(otherTerm, ((Lam) otherTerm).getVar(), ((Lam) term).getVar());
                }

                return equals(((Lam) term).getTerm(), ((Lam) otherTerm).getTerm());
            }

            throw new IllegalStateException();
        }
        return false;
    }

    private static Val appCl(Closure closure, Val val) {
        Map<Character, Val> updatedEnv = new HashMap<>(closure.getEnv());
        updatedEnv.put(closure.getName(), val);
        return advancedEval(updatedEnv, closure.getTerm());
    }

    private static Term quote(List<Character> names, Val val) {
        if (val instanceof VVar) {
            return new Var(((VVar) val).getName());
        }

        if (val instanceof VApp) {
            return new Application(quote(names, ((VApp) val).getLeftVal()), quote(names, ((VApp) val).getRightVal()));
        }

        if (val instanceof VLam) {
            // VLam (freshCl ns -> (x, cl)) -> Lam x (quote (x:ns) (appCl cl (VVar x)))
            Pair<Character, Closure> freshCl = freshCl(names, ((VLam) val).getClosure());
            List<Character> updatedNames = new ArrayList<>(names);
            updatedNames.add(freshCl.getLeft());
            return new Lam(new Var(freshCl.getLeft()), quote(updatedNames, appCl(freshCl.getRight(), new VVar(freshCl.getLeft()))));
        }

        throw new IllegalStateException();
    }

    private static Pair<Character, Closure> freshCl(List<Character> names, Closure closure) {
        return Pair.of(fresh(names, closure.getName()), closure);
    }

    private static Character fresh(List<Character> names, char name) {

        return null;
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

        if (AVAILABLE_CHARACTERS.size() > 0) {
            return AVAILABLE_CHARACTERS.remove(0);
        }
        throw new IllegalStateException("Application ran out of free Var names");
    }
}
