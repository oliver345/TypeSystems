package typed.ski.evaluator;

import typed.ski.lang.term.*;

public class Evaluator {

    public static Term eval(Term wtt) {

        if (wtt instanceof S || wtt instanceof K || wtt instanceof I || wtt instanceof Literal ||
                wtt instanceof True || wtt instanceof False || wtt instanceof ITE) {
            return wtt;
        }
        else if (wtt instanceof Application) {
            return new Application(((Application) wtt).getLeftType(), ((Application) wtt).getRightType(),
                    eval(((Application) wtt).getLeftTerm()), eval(((Application) wtt).getRightTerm())).apply();
        }

        throw new IllegalStateException();
    }
}
