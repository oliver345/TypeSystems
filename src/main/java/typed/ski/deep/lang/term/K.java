package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;
import typed.ski.deep.lang.type.Unknown;

import java.util.Map;

public class K implements Term {

    private PreType x;

    private PreType y;

    public K(PreType x, PreType y) {
        this.x = x;
        this.y = y;
    }

    public PreType getX() {
        return x;
    }

    public PreType getY() {
        return y;
    }

    @Override
    public String toString() {
        return "K[" + x + ", " + y + "]";
    }

    @Override
    public void substituteUnknownTypes(Map<Integer, PreType> resolvedTypes) {
        if (x instanceof Unknown) {
            x = resolvedTypes.get(((Unknown) x).getTypeId());
        }

        if (y instanceof Unknown) {
            y = resolvedTypes.get(((Unknown) y).getTypeId());
        }
    }
}
