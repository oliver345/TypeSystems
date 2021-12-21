package ski.term;

public class Application implements Term {

    private Term leftTerm;

    private final Term rightTerm;

    public Application(Term leftTerm, Term rightTerm) {
        this.leftTerm = leftTerm;
        this.rightTerm = rightTerm;
    }

    public Term apply() {

        if (leftTerm instanceof S || leftTerm instanceof K) {
            return this;
        }

        if (leftTerm instanceof I) {
            return rightTerm;
        }

        if (leftTerm instanceof Application) {

            Application subApplication = (Application) leftTerm;
            if (subApplication.getLeftTerm() instanceof K) {
                return subApplication.getRightTerm();
            }

            if (subApplication.getLeftTerm() instanceof S) {
                return this;
            }

            if (subApplication.getLeftTerm() instanceof I) {
                return new Application(subApplication.getRightTerm(), rightTerm);
            }

            if (subApplication.getLeftTerm() instanceof Application) {
                Application subSubApplication = (Application) subApplication.getLeftTerm();
                if (subSubApplication.getLeftTerm() instanceof S) {
                    return new Application(new Application(subSubApplication.getRightTerm(), rightTerm),
                            new Application(subApplication.getRightTerm(), rightTerm));
                }
                else {
                    return new Application(subApplication.apply(), rightTerm);
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

    public void setLeftTerm(Term term) {
        leftTerm = term;
    }

    @Override
    public String toString() {
        return "(" + leftTerm + "$" + rightTerm + ")";
    }
}
