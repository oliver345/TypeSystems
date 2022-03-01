package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.Ty;

public class ITE implements Term {

    private final Ty type;

    public ITE(Ty type) {
        this.type = type;
    }

    public Ty getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ITE[" + type + "]";
    }
}
