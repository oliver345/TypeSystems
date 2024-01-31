package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;

import static typed.ski.deep.typechecker.TypeChecker.replaceTypeIfUnknown;

public class I implements Term {

    private PreType x;

    public I(PreType x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return "I{" + x + "}";
    }

    @Override
    public void substituteUnknownTypes() {
        x = replaceTypeIfUnknown(x);
    }
}
