package typed.ski.deep.lang.preterm;

import typed.ski.deep.lang.type.PreType;

public class Rec_A implements Preterm {

    private final PreType a;

    public Rec_A(PreType a) {
        this.a = a;
    }

    public PreType getA() {
        return a;
    }

    @Override
    public String toString() {
        return "Rec{" + a + "}";
    }
}
