package typed.ski.deep.lang.preterm;

public class Lit implements Preterm {

    private final String text;

    public Lit(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
