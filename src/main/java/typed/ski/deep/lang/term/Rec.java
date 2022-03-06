package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;

public class Rec implements Term {

    private final PreType x;

    public Rec(PreType x) {
        this.x = x;
    }

    public PreType getX() {
        return x;
    }

    @Override
    public String toString() {
        return "Rec[" + x + "]";
    }
}
