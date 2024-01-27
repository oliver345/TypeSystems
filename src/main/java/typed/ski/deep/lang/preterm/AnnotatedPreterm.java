package typed.ski.deep.lang.preterm;

import typed.ski.deep.lang.type.PreType;

public class AnnotatedPreterm implements Preterm {

    private final Preterm preterm;

    private final PreType type;

    public AnnotatedPreterm(Preterm preterm, PreType type) {
        this.preterm = preterm;
        this.type = type;
    }

    public Preterm getPreterm() {
        return preterm;
    }

    public PreType getType() {
        return type;
    }

    @Override
    public String toString() {
        return preterm + ":" + type;
    }
}
