package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.Ty;

public class K implements Term {

    private final Ty x;

    private final Ty y;

    public K(Ty x, Ty y) {
        this.x = x;
        this.y = y;
    }

    public Ty getX() {
        return x;
    }

    public Ty getY() {
        return y;
    }

    @Override
    public String toString() {
        return "K[" + x + ", " + y + "]";
    }
}
