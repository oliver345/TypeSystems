package typed.ski.deep.lang.term;

public class EmptyList implements Term {

    @Override
    public String toString() {
        return "[]";
    }

    @Override
    public void substituteUnknownTypes() {}
}
