package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;

import static typed.ski.deep.typechecker.TypeChecker.replaceTypeIfUnknown;

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
        return "Rec{" + x + "}";
    }

    @Override
    public void substituteUnknownTypes() {
        x = replaceTypeIfUnknown(x);
    }
}
