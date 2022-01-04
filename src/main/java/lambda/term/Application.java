package lambda.term;

public class Application implements Term {

    private final Term leftTerm;

    private final Term rightTerm;

    public Application(Term leftTerm, Term rightTerm) {
        this.leftTerm = leftTerm;
        this.rightTerm = rightTerm;
    }

    public Term apply() {
        return this;
    }

    public Term getLeftTerm() {
        return leftTerm;
    }

    public Term getRightTerm() {
        return rightTerm;
    }

    @Override
    public String toString() {
        return "(" + leftTerm + ":" + rightTerm + ")";
    }
}
