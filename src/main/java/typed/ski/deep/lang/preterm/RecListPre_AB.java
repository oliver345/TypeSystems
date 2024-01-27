package typed.ski.deep.lang.preterm;

import typed.ski.deep.lang.type.PreType;

public class RecListPre_AB implements Preterm {

    private final PreType a;

    private final PreType b;

    public RecListPre_AB(PreType a, PreType b) {
        this.a = a;
        this.b = b;
    }

    public PreType getA() {
        return a;
    }

    public PreType getB() {
        return b;
    }

    @Override
    public String toString() {
        return "RecList{" + a + "}{" + b + "}";
    }
}
