package typed.ski.deep.lang.term;

public class ZERO implements Term {

    @Override
    public String toString() {
        return "ZERO";
    }

    @Override
    public String toString(boolean prettyPrint) {
        return prettyPrint ? "0" : toString();
    }

    @Override
    public void substituteUnknownTypes() {}
}
