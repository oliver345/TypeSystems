package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;

public class S implements Term {

    private final PreType x;

    private final PreType y;

    private final PreType z;

    public S(PreType x, PreType y, PreType z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PreType getX() {
        return x;
    }

    public PreType getY() {
        return y;
    }

    public PreType getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "S[" + x + ", " + y + ", " + z + "]";
    }
}
