package typed.ski.lang.preterm;

public class Lit implements Preterm {

    private final String name;

    public Lit(String name) {
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
