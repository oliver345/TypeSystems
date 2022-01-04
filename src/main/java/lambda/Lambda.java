package lambda;

import lambda.term.Application;
import lambda.term.Bound;
import lambda.term.Term;
import lambda.term.Var;

public class Lambda {

    public static Term eval(Term term) {

        if (term instanceof Var || term instanceof Bound) {
            return term;
        }

        if (term instanceof Application) {
            return ((Application) term).apply();
        }

        throw new IllegalStateException();
    }
}
