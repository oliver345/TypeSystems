package typed.ski.typechecker;

import org.apache.commons.lang3.tuple.Pair;
import typed.ski.lang.preterm.*;
import typed.ski.lang.term.Application;
import typed.ski.lang.term.Literal;
import typed.ski.lang.term.Term;
import typed.ski.lang.type.Bool;
import typed.ski.lang.type.Function;
import typed.ski.lang.type.Str;
import typed.ski.lang.type.Ty;

import java.util.Optional;

public class TypeChecker {

    public static Optional<Pair<Term, Ty>> infer(Preterm parseTree) {
        if (parseTree instanceof S || parseTree instanceof K || parseTree instanceof I || parseTree instanceof ITE) {
            return Optional.empty();
        }

        if (parseTree instanceof True) {
            return Optional.of(Pair.of(new typed.ski.lang.term.True(), new Bool()));
        }

        if (parseTree instanceof False) {
            return Optional.of(Pair.of(new typed.ski.lang.term.False(), new Bool()));
        }

        if (parseTree instanceof AnnotatedPreterm) {
            Optional<Term> termOptional = check(((AnnotatedPreterm) parseTree).getPreterm(), ((AnnotatedPreterm) parseTree).getType());
            return termOptional.map(term -> Pair.of(term, ((AnnotatedPreterm) parseTree).getType()));
        }

        if (parseTree instanceof App) {
            Optional<Pair<Term, Ty>> leftWttOpt = infer(((App) parseTree).getLeftTerm());
            Optional<Pair<Term, Ty>> rightWttOpt = infer(((App) parseTree).getRightTerm());

            if (leftWttOpt.isEmpty() || rightWttOpt.isEmpty()) {
                return Optional.empty();
            }

            Pair<Term, Ty> leftWtt = leftWttOpt.get();
            Pair<Term, Ty> rightWtt = rightWttOpt.get();

            // ((C,c),(D,d)) -> if (C = D->E) then (E, c'd) else Error,, alaposabb típus összehasonlítás kell?
            if (leftWtt.getRight() instanceof Function &&
                    ((Function) leftWtt.getRight()).getInputType().getClass().equals(rightWtt.getRight().getClass())) {
                return Optional.of(Pair.of(new Application(leftWtt.getRight(), rightWtt.getRight(), leftWtt.getLeft(),
                        rightWtt.getLeft()), ((Function) leftWtt.getRight()).getResultType()));
            }
            return Optional.empty();
        }

        //Check???
        if (parseTree instanceof Lit) {
            return Optional.of(Pair.of(new Literal(((Lit) parseTree).getName()), new Str()));
        }
        throw new IllegalStateException();
    }

    public static boolean areTypesEqual(Ty type, Ty otherType) {
        if (type == null || otherType == null) {
            return false;
        }

        return (type instanceof Function && otherType instanceof Function) ?
                compareFunctions((Function) type, (Function) otherType) : type.getClass().equals(otherType.getClass());
    }

    private static Optional<Term> check(Preterm parseTree, Ty type) {
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
                    return Optional.empty();
                }

                Ty param2 = ((Function) ((Function) type).getResultType()).getInputType();
                Ty param3AndReturnType = ((Function) ((Function) type).getResultType()).getResultType();
                if (param2 instanceof Function && param3AndReturnType instanceof Function) {
                    // Compare types of the 2nd param (A->B)
                    if (!((Function) param2).getInputType().getClass().equals(A.getClass()) || !((Function) param2).getResultType().getClass().equals(B.getClass())){
                        return Optional.empty();
                    }

                    // Compare type of the 3rd param (A) and the return type (C)
                    if (!((Function) param3AndReturnType).getInputType().getClass().equals(A.getClass()) || !((Function) param3AndReturnType).getResultType().getClass().equals(C.getClass())) {
                        return Optional.empty();
                    }

                    //return new WellTypedTree(new typed.ski.lang.term.S(A, B, C), null);
                    return Optional.of(new typed.ski.lang.term.S(new Function(A, new Function(B, C)), new Function(A, B), C));
                }
            }
        }
        else if (parseTree instanceof K) {
            if (type instanceof Function && ((Function) type).getResultType() instanceof Function && ((Function) type).getInputType().getClass().equals(((Function) ((Function) type).getResultType()).getResultType().getClass())) {
                return Optional.of(new typed.ski.lang.term.K(((Function) type).getInputType(), ((Function) ((Function) type).getResultType()).getInputType()));
            }
        }
        else if (parseTree instanceof I) {
            if (type instanceof Function && ((Function) type).getInputType().getClass().equals(((Function) type).getResultType().getClass())) {
                return Optional.of(new typed.ski.lang.term.I(((Function) type).getInputType()));
            }
        }
        else if (parseTree instanceof True && type instanceof Bool) {
            return Optional.of(new typed.ski.lang.term.True());
        }
        else if (parseTree instanceof False && type instanceof Bool) {
            return Optional.of(new typed.ski.lang.term.False());
        }
        else if (parseTree instanceof ITE && type instanceof Function) {
            if (((Function) type).getInputType() instanceof Bool && ((Function) type).getResultType() instanceof Function) {
                Ty A = ((Function) ((Function) type).getResultType()).getInputType();
                if (((Function) ((Function) type).getResultType()).getResultType() instanceof Function &&
                        ((Function) ((Function) ((Function) type).getResultType()).getResultType()).getInputType().getClass().equals(A.getClass()) &&
                        ((Function) ((Function) ((Function) type).getResultType()).getResultType()).getResultType().getClass().equals(A.getClass())) {
                    return Optional.of(new typed.ski.lang.term.ITE(A));
                }
            }
        }
        else if (parseTree instanceof AnnotatedPreterm) {
            if (areTypesEqual(((AnnotatedPreterm) parseTree).getType(), type)) {
                return check(((AnnotatedPreterm) parseTree).getPreterm(), ((AnnotatedPreterm) parseTree).getType());
            }

        }
        else if (parseTree instanceof App) {
            Optional<Pair<Term, Ty>> paramWttOpt = infer(((App) parseTree).getRightTerm());
            if (paramWttOpt.isPresent()) {
                Optional<Term> functionWttOpt = check(((App) parseTree).getLeftTerm(), new Function(paramWttOpt.get().getRight(), type));
                return functionWttOpt.map(functionWtt -> new Application(new Function(
                        paramWttOpt.get().getRight(), type),
                        paramWttOpt.get().getRight(),
                        functionWtt,
                        paramWttOpt.get().getLeft()));
            }
        }

        return Optional.empty();
    }

    private static boolean compareFunctions(Function fun1, Function fun2) {
        return areTypesEqual(fun1.getInputType(), fun2.getInputType()) && areTypesEqual(fun1.getResultType(), fun2.getResultType());
    }
}
