package lambda.data.term;

public class Var implements Term {

    //Todo: remove
    private Lam binder;

    private final char name;

    public Var(char name) {
        this(null, name);
    }

    public Var(Lam binder, char name) {
        this.binder = binder;
        this.name = name;
    }

    public Lam getBinder() {
        return binder;
    }

    public void setBinder(Lam binder) {
        this.binder = binder;
    }

    public char getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
