package ski.term;

public class Application extends Term {

    private final Term leftTerm;

    private final Term rightTerm;

    public Application(Term leftTerm, Term rightTerm) {
        this.leftTerm = leftTerm;
        this.rightTerm = rightTerm;
    }

    public Term apply() {

        if (leftTerm instanceof S || leftTerm instanceof K || leftTerm instanceof Var) {
            return this;
        }

        if (leftTerm instanceof I) {
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

            if (subApplication.getLeftTerm() instanceof Var) {
                return this;
            }

            if (subApplication.getLeftTerm() instanceof Application) {
                if (((Application) subApplication.getLeftTerm()).getLeftTerm() instanceof S) {
                    return new Application(new Application(((Application) subApplication.getLeftTerm()).getRightTerm(),
                            rightTerm).apply(), new Application(subApplication.getRightTerm(), rightTerm).apply()).apply();
                }
            }
        }

        throw new IllegalStateException();
    }

    public Term getLeftTerm() {
        return leftTerm;
    }

    public Term getRightTerm() {
        return rightTerm;
    }

    @Override
    public String toString() {
        return "(" + leftTerm + "$" + rightTerm + ")";
    }
}
