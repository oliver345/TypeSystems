package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.Ty;

public class Rec implements Term {

    private final Ty x;

    public Rec(Ty x) {
        this.x = x;
    }

    public Ty getX() {
        return x;
    }

    @Override
    public String toString() {
        return "Rec[" + x + "]";
    }
}
