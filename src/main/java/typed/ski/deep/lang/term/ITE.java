package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;

import static typed.ski.deep.typechecker.TypeChecker.replaceTypeIfUnknown;

public class ITE implements Term {

    private PreType type;

    public ITE(PreType type) {
        this.type = type;
    }

    public PreType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ITE{" + type + "}";
    }

    @Override
    public void substituteUnknownTypes() {
        type = replaceTypeIfUnknown(type);
    }
}
