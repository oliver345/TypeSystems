package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;

import static typed.ski.deep.typechecker.TypeChecker.replaceTypeIfUnknown;

public class Cons implements Term{

    private PreType listType;

    public Cons(PreType listType) {
        this.listType = listType;
    }

    @Override
    public String toString() {
        return "Cons";
    }

    @Override
    public void substituteUnknownTypes() {
        listType = replaceTypeIfUnknown(listType);
    }
}
