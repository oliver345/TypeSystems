package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;

public class ITE implements Term {

    private final PreType type;

    public ITE(PreType type) {
        this.type = type;
    }

    public PreType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ITE[" + type + "]";
    }
}
