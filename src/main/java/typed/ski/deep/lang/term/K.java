package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;

import java.util.Map;

import static typed.ski.deep.typechecker.TypeChecker.replaceTypeIfUnknown;

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
        x = replaceTypeIfUnknown(x, resolvedTypes);
        y = replaceTypeIfUnknown(y, resolvedTypes);
    }
}
