package typed.ski.deep.lang.preterm;

import typed.ski.deep.lang.type.Ty;

public class AnnotatedPreterm implements Preterm {

    private final Preterm preterm;

    private final Ty type;

    public AnnotatedPreterm(Preterm preterm, Ty type) {
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
