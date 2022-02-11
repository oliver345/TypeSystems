package typed.ski.typechecker.maybe;

import typed.ski.lang.term.Term;
import typed.ski.lang.type.Ty;

public class WellTypedTree implements Maybe {

    private final Term wellTypedTree;

    private final Ty type;

    public WellTypedTree(Term wellTypedTree, Ty type) {
        this.wellTypedTree = wellTypedTree;
        this.type = type;
    }

    public Term getWellTypedTree() {
        return wellTypedTree;
    }

    public Ty getType() {
        return type;
    }

    @Override
    public String toString() {
        return wellTypedTree + " ==> " + type;
    }
}
