package typed.ski.typechecker;

import typed.ski.lang.preterm.*;
import typed.ski.lang.term.Application;
import typed.ski.lang.term.Literal;
import typed.ski.lang.type.Bool;
import typed.ski.lang.type.Function;
import typed.ski.lang.type.Str;
import typed.ski.lang.type.Ty;
import typed.ski.typechecker.maybe.Error;
import typed.ski.typechecker.maybe.Maybe;
import typed.ski.typechecker.maybe.WellTypedTree;

public class TypeChecker {

    public static Maybe infer(Preterm parseTree) {
        if (parseTree instanceof S || parseTree instanceof K || parseTree instanceof I || parseTree instanceof ITE) {
            return new Error();
        }

        if (parseTree instanceof True) {
            return new WellTypedTree(new typed.ski.lang.term.True(), new Bool());
        }

        if (parseTree instanceof False) {
            return new WellTypedTree(new typed.ski.lang.term.False(), new Bool());
        }

        if (parseTree instanceof PtmTy) {
            Maybe maybe = check(((PtmTy) parseTree).getPreterm(), ((PtmTy) parseTree).getType());
            if (maybe instanceof WellTypedTree) {
                return new WellTypedTree(((WellTypedTree) maybe).getWellTypedTree(), ((PtmTy) parseTree).getType());
            }
            return new Error();
        }

        if (parseTree instanceof App) {
            Maybe left = infer(((App) parseTree).getLeftTerm());
            Maybe right = infer(((App) parseTree).getRightTerm());
            if (left instanceof Error || right instanceof Error) {
                return new Error();
            }
            WellTypedTree leftResult = (WellTypedTree) left;
            WellTypedTree rightResult = (WellTypedTree) right;
            if (leftResult.getType() instanceof Function &&
                    ((Function) leftResult.getType()).getInputType().getClass().equals(rightResult.getType().getClass())) {
                return new WellTypedTree(new Application(leftResult.getType(), rightResult.getType(),
                        leftResult.getWellTypedTree(), rightResult.getWellTypedTree()), ((Function) leftResult.getType()).getResultType());
            }
            return new Error();

        }

        if (parseTree instanceof Lit) {
            return new WellTypedTree(new Literal(((Lit) parseTree).getName()), new Str());
        }
        throw new IllegalStateException();
    }

    private static Maybe check(Preterm parseTree, Ty type) {
        if (parseTree instanceof S) {
            if (type instanceof Function && ((Function) type).getInputType() instanceof Function && ((Function) type).getResultType() instanceof Function) {
                Ty A, B, C;
                // Extract types of S' first param (A->B-C)
                if (((Function) ((Function) type).getInputType()).getResultType() instanceof Function) {
                    A = ((Function) ((Function) type).getInputType()).getInputType();
                    B = ((Function) ((Function) ((Function) type).getInputType()).getResultType()).getInputType();
                    C = ((Function) ((Function) ((Function) type).getInputType()).getResultType()).getResultType();
                }
                else {
                    return new Error();
                }

                Ty param2 = ((Function) ((Function) type).getResultType()).getInputType();
                Ty param3AndReturnType = ((Function) ((Function) type).getResultType()).getResultType();
                if (param2 instanceof Function && param3AndReturnType instanceof Function) {
                    // Compare types of the 2nd param (A->B)
                    if (!((Function) param2).getInputType().getClass().equals(A.getClass()) || !((Function) param2).getResultType().getClass().equals(B.getClass())){
                        return new Error();
                    }

                    // Compare type of the 3rd param (A) and the return type (C)
                    if (!((Function) param3AndReturnType).getInputType().getClass().equals(A.getClass()) || !((Function) param3AndReturnType).getResultType().getClass().equals(C.getClass())) {
                        return new Error();
                    }

                    return new WellTypedTree(new typed.ski.lang.term.S(A, B, C), null);
                }
            }
            return new Error();
        }

        if (parseTree instanceof K) {
            if (type instanceof Function && ((Function) type).getResultType() instanceof Function && ((Function) type).getInputType().getClass().equals(((Function) ((Function) type).getResultType()).getResultType().getClass())) {
                return new WellTypedTree(new typed.ski.lang.term.K(((Function) type).getInputType(), ((Function) ((Function) type).getResultType()).getInputType()), null);
            }
            return new Error();
        }

        if (parseTree instanceof I) {
            if (type instanceof Function && ((Function) type).getInputType().getClass().equals(((Function) type).getResultType().getClass())) {
                return new WellTypedTree(new typed.ski.lang.term.I(((Function) type).getInputType()), null);
            }
        }

        if (parseTree instanceof True && type instanceof Bool) {
            return new WellTypedTree(new typed.ski.lang.term.True(), null);
        }

        if (parseTree instanceof False && type instanceof Bool) {
            return new WellTypedTree(new typed.ski.lang.term.False(), null);
        }
        return new Error();
    }
}
