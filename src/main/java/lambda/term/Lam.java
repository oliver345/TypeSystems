package lambda.term;

public class Lam implements Term {

    private final Var var;

    private final Term term;

    public Lam(Var var, Term term) {
        this.var = var;
        this.term = term;
    }

    public Var getVar() {
        return var;
    }

    public Term getTerm() {
        return term;
    }

    @Override
    public String toString() {
        return "(L" + var + "." + term + ")";
    }
}
