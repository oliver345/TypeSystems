package lambda.term;

public class Var implements Term {

    private Bound bound;

    private final char name;

    public Var(char name) {
        this(null, name);
    }

    public Var(Bound bound, char name) {
        this.bound = bound;
        this.name = name;
    }

    public Bound getBound() {
        return bound;
    }

    public void setBound(Bound bound) {
        this.bound = bound;
    }

    public char getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
