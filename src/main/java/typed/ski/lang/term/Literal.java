package typed.ski.lang.term;

public class Literal implements Term {

    private final String name;

    public Literal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
