package typed.ski.lang.preterm;

import typed.ski.lang.type.Ty;

public class PtmTy implements Preterm {

    private final Preterm preterm;

    private final Ty type;

    public PtmTy(Preterm preterm, Ty type) {
        this.preterm = preterm;
        this.type = type;
    }

    public Preterm getPreterm() {
        return preterm;
    }

    public Ty getType() {
        return type;
    }

    @Override
    public String toString() {
        return preterm + ":" + type;
    }
}
