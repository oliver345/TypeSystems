package typed.ski.deep.typechecker;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.Pair;
import typed.ski.deep.lang.preterm.*;
import typed.ski.deep.lang.term.Application;
import typed.ski.deep.lang.term.Literal;
import typed.ski.deep.lang.term.Term;
import typed.ski.deep.lang.type.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TypeChecker {

    private static final Map<Integer, PreType> unknownTypes = new HashMap<>();

    public static Term createWellTypedTree(Preterm parseTree) {
        unknownTypes.clear();

        Optional<Pair<Term, PreType>> resultOptional = inferWithPreType(parseTree);
        if (resultOptional.isPresent()) {
            System.out.println(resultOptional.get().getRight());
            System.out.println(resultOptional.get().getLeft());
            return resultOptional.get().getLeft();
        }
        return null;
    }

    private static Optional<Pair<Term, PreType>> inferWithPreType(Preterm parseTree) {
        int varCount = unknownTypes.size();

        if (parseTree instanceof S) {
            Unknown unknown1 = new Unknown(varCount++);
            Unknown unknown2 = new Unknown(varCount++);
            Unknown unknown3 = new Unknown(varCount);
            insertIntoUnknownTypes(unknown1, unknown2, unknown3);

            return Optional.of(Pair.of(new typed.ski.deep.lang.term.S(unknown1, unknown2, unknown3),
                    new Function(new Function(unknown1, new Function(unknown2, unknown3)),
                            new Function(new Function(unknown1, unknown2), new Function(unknown1, unknown3)))));
        }

        if (parseTree instanceof K) {
            Unknown unknown1 = new Unknown(varCount++);
            Unknown unknown2 = new Unknown(varCount);
            insertIntoUnknownTypes(unknown1, unknown2);

            return Optional.of(Pair.of(new typed.ski.deep.lang.term.K(unknown1, unknown2),
                    new Function(unknown1, new Function(unknown2, unknown1))));
        }

        if (parseTree instanceof I) {
            Unknown unknown1 = new Unknown(varCount);
            insertIntoUnknownTypes(unknown1);

            return Optional.of(Pair.of(new typed.ski.deep.lang.term.I(unknown1), new Function(unknown1, unknown1)));
        }

        if (parseTree instanceof True) {
            return Optional.of(Pair.of(new typed.ski.deep.lang.term.True(), new Bool()));
        }

        if (parseTree instanceof False) {
            return Optional.of(Pair.of(new typed.ski.deep.lang.term.False(), new Bool()));
        }

        if (parseTree instanceof App) {
            Pair<Term, PreType> left = inferWithPreType(((App) parseTree).getLeftTerm()).orElseThrow();
            Pair<Term, PreType> right = inferWithPreType(((App) parseTree).getRightTerm()).orElseThrow();

            List<Pair<PreType, PreType>> typeEquations = new ArrayList<>();

            if (left.getRight() instanceof Function) {
                typeEquations.add(Pair.of(((Function) left.getRight()).getInputType(), right.getRight()));
            }
            else {
                throw new IllegalStateException("Invalid types in application: " + left.getRight() + " " + right.getRight());
            }

            Map<Integer, PreType> resultTypes = unify(typeEquations, unknownTypes);

            //--- Do it on higher level?
            resultTypes.entrySet().stream()
                    .filter(entry -> entry.getValue() instanceof Unknown)
                    .findAny()
                    .ifPresent(entry -> {
                        throw new IllegalStateException("Could not resolve type of #" + entry.getKey() + ": " + entry.getValue());
                    });

            left.getLeft().substituteUnknownTypes(resultTypes);
            right.getLeft().substituteUnknownTypes(resultTypes);

            left = Pair.of(left.getLeft(), replaceTypeIfUnknown(left.getRight(), resultTypes));
            right = Pair.of(right.getLeft(), replaceTypeIfUnknown(right.getRight(), resultTypes));

            return Optional.of(Pair.of(new Application(left.getRight(), right.getRight(),
                    left.getLeft(), right.getLeft()), ((Function) left.getRight()).getResultType()));
        }

        return Optional.empty();
    }

    private static PreType replaceTypeIfUnknown(PreType preType, Map<Integer, PreType> resolvedTypes) {
        if (preType instanceof Unknown) {
            return resolvedTypes.get(((Unknown) preType).getTypeId());
        }
        else if (preType instanceof Function) {
            return new Function(replaceTypeIfUnknown(((Function) preType).getInputType(), resolvedTypes),
                    replaceTypeIfUnknown(((Function) preType).getResultType(), resolvedTypes));
        }
        else {
            return preType;
        }
    }

    private static Map<Integer, PreType> unify(List<Pair<PreType, PreType>> typeEquations, Map<Integer, PreType> unknownTypes) {
        Map<Integer, PreType> types = SerializationUtils.clone((HashMap<Integer, PreType>) unknownTypes);

        if (!typeEquations.isEmpty()) {
            Pair<PreType, PreType> pair = typeEquations.remove(0);

            if (pair.getLeft() instanceof Unknown && pair.getRight() instanceof Unknown) {

                if (((Unknown) pair.getLeft()).getTypeId() != ((Unknown) pair.getRight()).getTypeId()) {
                    typeEquations.add(pair);
                }

                return unify(typeEquations, types);
            }
            else if (pair.getLeft() instanceof Function && pair.getRight() instanceof Function) {
                typeEquations.add(Pair.of(((Function) pair.getLeft()).getInputType(), ((Function) pair.getRight()).getInputType()));
                typeEquations.add(Pair.of(((Function) pair.getLeft()).getResultType(), ((Function) pair.getRight()).getResultType()));

                return unify(typeEquations, types);
            }
            else if (pair.getLeft() instanceof Unknown || pair.getRight() instanceof Unknown) {
                final Unknown unknown;
                final Ty type;
                if (pair.getLeft() instanceof Unknown) {
                    unknown = (Unknown) pair.getLeft();
                    type = (Ty) pair.getRight();
                }
                else {
                    unknown = (Unknown) pair.getRight();
                    type = (Ty) pair.getLeft();
                }

                typeEquations = typeEquations.stream()
                        .map(entry -> {
                            PreType left = entry.getLeft();
                            PreType right = entry.getRight();

                            if (entry.getLeft() instanceof Unknown && ((Unknown) entry.getLeft()).getTypeId() == unknown.getTypeId()) {
                                left = type;
                            }

                            if (entry.getRight() instanceof Unknown && ((Unknown) entry.getRight()).getTypeId() == unknown.getTypeId()) {
                                right = type;
                            }

                            return Pair.of(left, right);
                        })
                        .collect(Collectors.toList());

                types.put(unknown.getTypeId(), type);

                return unify(typeEquations, types);
            }
            else {
                if (areTypesEqual(pair.getLeft(), pair.getRight())) {
                    return unify(typeEquations, types);
                }
                else {
                    throw new IllegalStateException("Not matching types: " + pair.getLeft() + ", " + pair.getRight());
                }
            }
        }

        return types;
    }

    private static void insertIntoUnknownTypes(Unknown... unknowns) {
        Stream.of(unknowns).forEach(unknown -> unknownTypes.put(unknown.getTypeId(), unknown));
    }

    public static Optional<Pair<Term, PreType>> infer(Preterm parseTree) {
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
            PreType recType = ((Rec) parseTree).getX();
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
            Optional<Pair<Term, PreType>> rightWttOpt = infer(((App) parseTree).getRightTerm());
            if (rightWttOpt.isEmpty()) {
                return Optional.empty();
            }
            Pair<Term, PreType> rightWtt = rightWttOpt.get();

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
                Optional<Pair<Term, PreType>> leftWttOpt = infer(((App) parseTree).getLeftTerm());

                if (leftWttOpt.isEmpty()) {
                    return Optional.empty();
                }
                Pair<Term, PreType> leftWtt = leftWttOpt.get();

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

    public static boolean areTypesEqual(PreType type, PreType otherType) {
        if (type == null || otherType == null) {
            return false;
        }

        return (type instanceof Function && otherType instanceof Function) ?
                compareFunctions((Function) type, (Function) otherType) : type.getClass().equals(otherType.getClass());
    }

    private static Optional<Term> check(Preterm parseTree, PreType type) {
        if (parseTree instanceof S) {
            if (type instanceof Function && ((Function) type).getInputType() instanceof Function && ((Function) type).getResultType() instanceof Function) {
                PreType A, B, C;
                // Extract types of S' first param (A->B-C)
                if (((Function) ((Function) type).getInputType()).getResultType() instanceof Function) {
                    A = ((Function) ((Function) type).getInputType()).getInputType();
                    B = ((Function) ((Function) ((Function) type).getInputType()).getResultType()).getInputType();
                    C = ((Function) ((Function) ((Function) type).getInputType()).getResultType()).getResultType();
                }
                else {
                    return Optional.empty();
                }

                PreType param2 = ((Function) ((Function) type).getResultType()).getInputType();
                PreType param3AndReturnType = ((Function) ((Function) type).getResultType()).getResultType();
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
                PreType A = ((Function) ((Function) type).getResultType()).getInputType();
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
            Optional<Pair<Term, PreType>> paramWttOpt = infer(((App) parseTree).getRightTerm());
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
            PreType recType = ((Rec) parseTree).getX();
            PreType expectedType = new Function(recType, new Function(new Function(new Nat(), new Function(recType, recType)),
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
