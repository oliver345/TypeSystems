package typed.ski.deep.lang.preterm;

import typed.ski.deep.lang.type.PreType;

public class K_A implements Preterm {

    private final PreType a;

    public K_A(PreType a) {
        this.a = a;
    }

    public PreType getA() {
        return a;
    }

    @Override
    public String toString() {
        return "K{" + a + "}{}";
    }
}
