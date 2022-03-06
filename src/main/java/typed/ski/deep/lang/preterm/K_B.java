package typed.ski.deep.lang.preterm;

import typed.ski.deep.lang.type.PreType;

public class K_B implements Preterm {

    private final PreType b;

    public K_B(PreType b) {
        this.b = b;
    }

    public PreType getB() {
        return b;
    }

    @Override
    public String toString() {
        return "K{}{" + b + "}";
    }
}
