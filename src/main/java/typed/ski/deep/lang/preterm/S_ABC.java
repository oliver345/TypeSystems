package typed.ski.deep.lang.preterm;

import typed.ski.deep.lang.type.PreType;

public class S_ABC implements Preterm {

    private final PreType a;

    private final PreType b;

    private final PreType c;

    public S_ABC(PreType a, PreType b, PreType c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public PreType getA() {
        return a;
    }

    public PreType getB() {
        return b;
    }

    public PreType getC() {
        return c;
    }

    @Override
    public String toString() {
        return "S{" + a + "}{" + b + "}{" + c + "}";
    }
}
