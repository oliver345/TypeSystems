package typed.ski.deep.lang.term;

public class Literal implements Term {

    private final String text;

    public Literal(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "\"" + text + "\"";
    }

    @Override
    public void substituteUnknownTypes() {}
}
