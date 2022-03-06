package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;

public class K implements Term {

    private final PreType x;

    private final PreType y;

    public K(PreType x, PreType y) {
        this.x = x;
        this.y = y;
    }

    public PreType getX() {
        return x;
    }

    public PreType getY() {
        return y;
    }

    @Override
    public String toString() {
        return "K[" + x + ", " + y + "]";
    }
}
