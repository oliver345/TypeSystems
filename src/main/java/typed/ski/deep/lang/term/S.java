package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;
import typed.ski.deep.lang.type.Unknown;

import java.util.Map;

public class S implements Term {

    private PreType x;

    private PreType y;

    private PreType z;

    public S(PreType x, PreType y, PreType z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PreType getX() {
        return x;
    }

    public PreType getY() {
        return y;
    }

    public PreType getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "S[" + x + ", " + y + ", " + z + "]";
    }

    @Override
    public void substituteUnknownTypes(Map<Integer, PreType> resolvedTypes) {
        if (x instanceof Unknown) {
            x = resolvedTypes.get(((Unknown) x).getTypeId());
        }

        if (y instanceof Unknown) {
            y = resolvedTypes.get(((Unknown) y).getTypeId());
        }

        if (z instanceof Unknown) {
            z = resolvedTypes.get(((Unknown) z).getTypeId());
        }
    }
}
