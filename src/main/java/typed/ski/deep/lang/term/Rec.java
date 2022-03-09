package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;
import typed.ski.deep.lang.type.Unknown;

import java.util.Map;

public class Rec implements Term {

    private PreType x;

    public Rec(PreType x) {
        this.x = x;
    }

    public PreType getX() {
        return x;
    }

    @Override
    public String toString() {
        return "Rec[" + x + "]";
    }

    @Override
    public void substituteUnknownTypes(Map<Integer, PreType> resolvedTypes) {
        if (x instanceof Unknown) {
            x = resolvedTypes.get(((Unknown) x).getTypeId());
        }
    }
}
