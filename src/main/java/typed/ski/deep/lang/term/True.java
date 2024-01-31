package typed.ski.deep.lang.term;

public class True implements Term {

    @Override
    public String toString() {
        return "True";
    }

    @Override
    public void substituteUnknownTypes() {}
}
