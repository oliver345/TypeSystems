package typed.ski.deep.lang.preterm;

import typed.ski.deep.lang.type.Ty;

public class Rec implements Preterm {

    private final Ty x;

    public Rec(Ty x) {
        this.x = x;
    }

    public Ty getX() {
        return x;
    }

    @Override
    public String toString() {
        return "Rec{" + x + "}";
    }
}
