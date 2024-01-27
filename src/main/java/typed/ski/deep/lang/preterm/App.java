package typed.ski.deep.lang.preterm;

public class App implements Preterm {

    private final Preterm leftTerm;

    private final Preterm rightTerm;

    public App(Preterm leftTerm, Preterm rightTerm) {
        this.leftTerm = leftTerm;
        this.rightTerm = rightTerm;
    }

    public Preterm getLeftTerm() {
        return leftTerm;
    }

    public Preterm getRightTerm() {
        return rightTerm;
    }

    @Override
    public String toString() {
        return "(" + leftTerm + " " + rightTerm + ")";
    }
}
