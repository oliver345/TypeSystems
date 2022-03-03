package typed.ski.deep.lang.preterm;

import typed.ski.deep.lang.type.Ty;

public class K_B implements Preterm {

    private final Ty b;

    public K_B(Ty b) {
        this.b = b;
    }

    public Ty getB() {
        return b;
    }

    @Override
    public String toString() {
        return "K{}{" + b + "}";
    }
}
