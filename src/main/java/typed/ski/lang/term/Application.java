package typed.ski.lang.term;

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
