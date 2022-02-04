package untyped.ski.term;

public class Var extends Term {

    private final char name;

    public Var(char name) {
        this.name = name;
    }

    public char getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
