package typed.ski.lang.term;

import typed.ski.lang.type.Ty;

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
        return "ITE";
    }
}
