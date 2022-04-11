package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;

import java.util.Map;

import static typed.ski.deep.typechecker.TypeChecker.replaceTypeIfUnknown;

public class RecList implements Term {

    private PreType a;

    private PreType b;

    public RecList(PreType a, PreType b) {
        this.a = a;
        this.b = b;
    }

    public PreType getA() {
        return a;
    }

    public PreType getB() {
        return b;
    }

    @Override
    public String toString() {
        return "RecList[" + a + ", " + b + "]";
    }


    @Override
    public void substituteUnknownTypes(Map<Integer, PreType> resolvedTypes) {
        a = replaceTypeIfUnknown(a, resolvedTypes);
        b = replaceTypeIfUnknown(b, resolvedTypes);
    }
}
