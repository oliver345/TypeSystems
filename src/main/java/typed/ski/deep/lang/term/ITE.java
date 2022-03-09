package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;
import typed.ski.deep.lang.type.Unknown;

import java.util.Map;

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
        return "ITE[" + type + "]";
    }

    @Override
    public void substituteUnknownTypes(Map<Integer, PreType> resolvedTypes) {
        if (type instanceof Unknown) {
            type = resolvedTypes.get(((Unknown) type).getTypeId());
        }
    }
}
