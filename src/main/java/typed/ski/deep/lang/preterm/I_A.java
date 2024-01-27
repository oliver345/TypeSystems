package typed.ski.deep.lang.preterm;

import typed.ski.deep.lang.type.PreType;

public class I_A implements Preterm {

    private final PreType a;

    public I_A(PreType a) {
        this.a = a;
    }

    public PreType getA() {
        return a;
    }

    @Override
    public String toString() {
        return "I{" + a + "}";
    }
}
