package typed.ski.lang.term;

import typed.ski.lang.type.Function;
import typed.ski.lang.type.Ty;

public class Application implements Term {

    private final Ty leftType;

    private final Ty rightType;

    private final Term leftTerm;

    private final Term rightTerm;

    public Application(Ty leftType, Ty rightType, Term leftTerm, Term rightTerm) {
        this.leftType = leftType;
        this.rightType = rightType;
        this.leftTerm = leftTerm;
        this.rightTerm = rightTerm;
    }

    public Ty getLeftType() {
        return leftType;
    }

    public Term apply() {

        if (leftTerm instanceof S || leftTerm instanceof K || leftTerm instanceof Literal || leftTerm instanceof True ||
                leftTerm instanceof False || leftTerm instanceof ITE) {
            return this;
        }

        if (leftTerm instanceof  I) {
            return rightTerm;
        }

        if (leftTerm instanceof Application) {
            Application subApplication = (Application) leftTerm;

            if (subApplication.getLeftTerm() instanceof S) {
                return this;
            }

            if (subApplication.getLeftTerm() instanceof K) {
                return subApplication.getRightTerm();
            }

            if (subApplication.getLeftTerm() instanceof ITE) {
                return this;
            }

            if (subApplication.getLeftTerm() instanceof Application) {
                if (((Application) subApplication.getLeftTerm()).getLeftTerm() instanceof S) {
                    S termS = (S) ((Application) subApplication.getLeftTerm()).getLeftTerm();
                    return new Application(((Function) termS.getX()).getResultType(), ((Function) termS.getY()).getResultType(),
                            new Application(termS.getX(), termS.getZ(),
                                    ((Application) subApplication.getLeftTerm()).getRightTerm(), rightTerm).apply(),
                            new Application(termS.getY(), termS.getZ(), subApplication.getRightTerm(), rightTerm).apply()).apply();
                }

                if (((Application) subApplication.getLeftTerm()).getLeftTerm() instanceof ITE) {
                    return Boolean.parseBoolean(((Application) subApplication.getLeftTerm()).getRightTerm().toString()) ?
                            subApplication.getRightTerm() : rightTerm;
                }
            }
        }

        throw new IllegalStateException();
    }

    public Ty getRightType() {
        return rightType;
    }

    public Term getLeftTerm() {
        return leftTerm;
    }

    public Term getRightTerm() {
        return rightTerm;
    }

    @Override
    public String toString() {
        return leftTerm + " " + rightTerm;
    }
}
