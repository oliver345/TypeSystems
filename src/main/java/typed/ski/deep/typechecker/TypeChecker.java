package typed.ski.deep.typechecker;

import org.apache.commons.lang3.tuple.Pair;
import typed.ski.deep.lang.preterm.*;
import typed.ski.deep.lang.term.Application;
import typed.ski.deep.lang.term.Literal;
import typed.ski.deep.lang.term.Term;
import typed.ski.deep.lang.type.*;

import java.util.Optional;

public class TypeChecker {

    public static Optional<Pair<Term, Ty>> infer(Preterm parseTree) {
        if (parseTree instanceof S || parseTree instanceof K || parseTree instanceof I || parseTree instanceof ITE ||
                parseTree instanceof K_A || parseTree instanceof K_B || parseTree instanceof Succ) {
            return Optional.empty();
        }

        if (parseTree instanceof K_AB) {
            K_AB kAB = (K_AB) parseTree;
            return Optional.of(Pair.of(new typed.ski.deep.lang.term.K(kAB.getA(), kAB.getB()),
                    new Function(kAB.getA(), new Function(kAB.getB(), kAB.getA()))));
        }

        if (parseTree instanceof Rec) {
            Ty recType = ((Rec) parseTree).getX();
            return Optional.of(Pair.of(new typed.ski.deep.lang.term.Rec(recType),
                    new Function(recType, new Function(new Function(new Nat(), new Function(recType, recType)),
                            new Function(new Nat(), recType)))));
        }

        if (parseTree instanceof True) {
            return Optional.of(Pair.of(new typed.ski.deep.lang.term.True(), new Bool()));
        }

        if (parseTree instanceof False) {
            return Optional.of(Pair.of(new typed.ski.deep.lang.term.False(), new Bool()));
        }

        if (parseTree instanceof ZERO) {
            return Optional.of(Pair.of(new typed.ski.deep.lang.term.ZERO(), new Nat()));
        }

        if (parseTree instanceof AnnotatedPreterm) {
            Optional<Term> termOptional = check(((AnnotatedPreterm) parseTree).getPreterm(), ((AnnotatedPreterm) parseTree).getType());
            return termOptional.map(term -> Pair.of(term, ((AnnotatedPreterm) parseTree).getType()));
        }

        if (parseTree instanceof App) {
            Optional<Pair<Term, Ty>> rightWttOpt = infer(((App) parseTree).getRightTerm());
            if (rightWttOpt.isEmpty()) {
                return Optional.empty();
            }
            Pair<Term, Ty> rightWtt = rightWttOpt.get();

            if (((App) parseTree).getLeftTerm() instanceof K_B) {
                return Optional.of(Pair.of(new Application(
                        new Function(rightWtt.getRight(), new Function(((K_B) ((App) parseTree).getLeftTerm()).getB(), rightWtt.getRight())),
                        rightWtt.getRight(),
                        new typed.ski.deep.lang.term.K(rightWtt.getRight(), ((K_B) ((App) parseTree).getLeftTerm()).getB()),
                        rightWtt.getLeft()),
                        new Function(((K_B) ((App) parseTree).getLeftTerm()).getB(), rightWtt.getRight())
                ));
            }
            /*else if (((App) parseTree).getLeftTerm() instanceof K_A) { ---
            }*/
            else if (((App) parseTree).getLeftTerm() instanceof K_AB) {
                K_AB kAB = (K_AB) ((App) parseTree).getLeftTerm();
                if (areTypesEqual(kAB.getA(), rightWtt.getRight())) {
                    return Optional.of(Pair.of(new Application(
                            new Function(kAB.getA(), new Function(kAB.getB(), kAB.getA())),
                            rightWtt.getRight(),
                            new typed.ski.deep.lang.term.K(kAB.getA(), kAB.getB()),
                            rightWtt.getLeft()),
                            new Function(kAB.getB(), kAB.getA())
                    ));
                }
            }
            else {
                Optional<Pair<Term, Ty>> leftWttOpt = infer(((App) parseTree).getLeftTerm());

                if (leftWttOpt.isEmpty()) {
                    return Optional.empty();
                }
                Pair<Term, Ty> leftWtt = leftWttOpt.get();

                // ((C,c),(D,d)) -> if (C = D->E) then (E, c'd) else Error,, areFunctionsEquel? ---
                if (leftWtt.getRight() instanceof Function &&
                        ((Function) leftWtt.getRight()).getInputType().getClass().equals(rightWtt.getRight().getClass())) {
                    return Optional.of(Pair.of(new Application(leftWtt.getRight(), rightWtt.getRight(), leftWtt.getLeft(),
                            rightWtt.getLeft()), ((Function) leftWtt.getRight()).getResultType()));
                }
            }
            return Optional.empty();
        }

        //Check ---
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

                    //return new WellTypedTree(new typed.ski.deep.lang.term.S(A, B, C), null);
                    return Optional.of(new typed.ski.deep.lang.term.S(new Function(A, new Function(B, C)), new Function(A, B), C));
                }
            }
        }
        else if (parseTree instanceof K) {
            if (type instanceof Function && ((Function) type).getResultType() instanceof Function && ((Function) type).getInputType().getClass().equals(((Function) ((Function) type).getResultType()).getResultType().getClass())) {
                return Optional.of(new typed.ski.deep.lang.term.K(((Function) type).getInputType(), ((Function) ((Function) type).getResultType()).getInputType()));
            }
        }
        else if (parseTree instanceof I) {
            if (type instanceof Function && ((Function) type).getInputType().getClass().equals(((Function) type).getResultType().getClass())) {
                return Optional.of(new typed.ski.deep.lang.term.I(((Function) type).getInputType()));
            }
        }
        else if (parseTree instanceof True && type instanceof Bool) {
            return Optional.of(new typed.ski.deep.lang.term.True());
        }
        else if (parseTree instanceof False && type instanceof Bool) {
            return Optional.of(new typed.ski.deep.lang.term.False());
        }
        else if (parseTree instanceof ZERO && type instanceof Nat) {
            return Optional.of(new typed.ski.deep.lang.term.ZERO());
        }
        else if (parseTree instanceof ITE && type instanceof Function) {
            if (((Function) type).getInputType() instanceof Bool && ((Function) type).getResultType() instanceof Function) {
                Ty A = ((Function) ((Function) type).getResultType()).getInputType();
                if (((Function) ((Function) type).getResultType()).getResultType() instanceof Function &&
                        ((Function) ((Function) ((Function) type).getResultType()).getResultType()).getInputType().getClass().equals(A.getClass()) &&
                        ((Function) ((Function) ((Function) type).getResultType()).getResultType()).getResultType().getClass().equals(A.getClass())) {
                    return Optional.of(new typed.ski.deep.lang.term.ITE(A));
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
        else if (parseTree instanceof Succ && type instanceof Function) {
            if (areTypesEqual(type, new Function(new Nat(), new Nat()))) {
                return Optional.of(new typed.ski.deep.lang.term.Succ());
            }
        }
        else if (parseTree instanceof Rec) {
            Ty recType = ((Rec) parseTree).getX();
            Ty expectedType = new Function(recType, new Function(new Function(new Nat(), new Function(recType, recType)),
                    new Function(new Nat(), recType)));

            if (areTypesEqual(expectedType, type)) {
                return Optional.of(new typed.ski.deep.lang.term.Rec(recType));
            }
        }

        return Optional.empty();
    }

    private static boolean compareFunctions(Function fun1, Function fun2) {
        return areTypesEqual(fun1.getInputType(), fun2.getInputType()) && areTypesEqual(fun1.getResultType(), fun2.getResultType());
    }
}
