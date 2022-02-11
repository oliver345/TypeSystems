package typed.ski.lang.term;

import typed.ski.lang.type.Ty;

public class S implements Term {

    private final Ty x;

    private final Ty y;

    private final Ty z;

    public S(Ty x, Ty y, Ty z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Ty getX() {
        return x;
    }

    public Ty getY() {
        return y;
    }

    public Ty getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "S";
    }
}
