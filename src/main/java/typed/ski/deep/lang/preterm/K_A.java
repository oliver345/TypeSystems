package typed.ski.deep.lang.preterm;

import typed.ski.deep.lang.type.Ty;

public class K_A implements Preterm {

    private final Ty a;

    public K_A(Ty a) {
        this.a = a;
    }

    public Ty getA() {
        return a;
    }

    @Override
    public String toString() {
        return "K{" + a + "}{}";
    }
}
