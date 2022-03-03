package typed.ski.deep.lang.preterm;

import typed.ski.deep.lang.type.Ty;

public class K_AB implements Preterm {

    private final Ty a;

    private final Ty b;

    public K_AB(Ty a, Ty b) {
        this.a = a;
        this.b = b;
    }

    public Ty getA() {
        return a;
    }

    public Ty getB() {
        return b;
    }

    @Override
    public String toString() {
        return "K{" + a + "}{" + b + "}";
    }
}
