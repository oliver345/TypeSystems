package lambda;

import lambda.term.Application;
import lambda.term.Bound;
import lambda.term.Term;
import lambda.term.Var;
import ski.term.I;
import ski.term.K;
import ski.term.S;

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

    public static Term fromSKI(ski.term.Term ski) {
        if (ski instanceof ski.term.Var) {
            return new Var(((ski.term.Var) ski).getName());
        }

        if (ski instanceof S) {
            Var varX = new Var('x');
            Var varY = new Var('y');
            Var varZ = new Var('z');
            Bound boundZ = new Bound(varZ, new Application(new Application(varX, varZ), new Application(varY, varZ)));
            varZ.setBound(boundZ);
            Bound boundY = new Bound(varY, boundZ);
            varY.setBound(boundY);
            Bound boundX = new Bound(varX, boundY);
            varX.setBound(boundX);
            return boundX;
        }

        if (ski instanceof K) {
            Var varX = new Var('x');
            Var varY = new Var('y');
            Bound boundY = new Bound(varY, varX);
            Bound boundX = new Bound(varX, boundY);
            varX.setBound(boundX);
            return boundX;
        }

        if (ski instanceof I) {
            Var varX = new Var('x');
            Bound bound = new Bound(varX, varX);
            varX.setBound(bound);
            return bound;
        }

        if (ski instanceof ski.term.Application) {
            return new Application(fromSKI(((ski.term.Application) ski).getLeftTerm()), fromSKI(((ski.term.Application) ski).getRightTerm()));
        }

        throw new IllegalStateException();
    }
}
