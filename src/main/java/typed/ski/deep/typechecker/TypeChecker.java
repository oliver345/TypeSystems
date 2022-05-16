package typed.ski.deep.typechecker;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.Pair;
import typed.ski.deep.lang.preterm.*;
import typed.ski.deep.lang.preterm.False;
import typed.ski.deep.lang.preterm.I;
import typed.ski.deep.lang.preterm.ITE;
import typed.ski.deep.lang.preterm.K;
import typed.ski.deep.lang.preterm.Rec;
import typed.ski.deep.lang.preterm.S;
import typed.ski.deep.lang.preterm.Succ;
import typed.ski.deep.lang.preterm.True;
import typed.ski.deep.lang.preterm.ZERO;
import typed.ski.deep.lang.term.*;
import typed.ski.deep.lang.type.*;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TypeChecker {

    private static Map<Integer, PreType> unknownTypes = new HashMap<>();

    public static Term createWellTypedTree(Preterm parseTree) {
        unknownTypes.clear();

        Optional<Pair<Term, PreType>> resultOptional = inferWithPreType(parseTree);
        if (resultOptional.isPresent()) {

            String unknownIds = unknownTypes.entrySet().stream()
                    .filter(entry -> entry.getValue() instanceof Unknown)
                    .map(entry -> entry.getKey().toString())
                    .collect(Collectors.joining(", "));
            if (!unknownIds.isEmpty()) {
                throw new IllegalStateException("Could not resolve the type of type-ID(s): " + unknownIds);
            }

            return resultOptional.get().getLeft();
        }
        return null;

        //Throw exception if empty
    }

    public static Optional<Pair<Term, PreType>> infer(Preterm parseTree) {

        if (parseTree instanceof S) {
            PreType a = new Unknown();
            PreType b = new Unknown();
            PreType c = new Unknown();
            return Optional.of(Pair.of(new typed.ski.deep.lang.term.S(a, b, c),
                    new Function(new Function(a, new Function(b, c)),
                            new Function(new Function(a, b), new Function(a, c)))));
        }

        if (parseTree instanceof K) {
            PreType a = new Unknown();
            PreType b = new Unknown();
            return Optional.of(Pair.of(new typed.ski.deep.lang.term.K(a, b), new Function(a, new Function(b, a))));
        }

        if (parseTree instanceof I) {
            PreType a = new Unknown();
            return Optional.of(Pair.of(new typed.ski.deep.lang.term.I(a), new Function(a, a)));
        }

        if (parseTree instanceof ITE) {
            PreType a = new Unknown();
            return Optional.of(Pair.of(new typed.ski.deep.lang.term.ITE(a), new Function(new Bool(), new Function(a, new Function(a, a)))));
        }

        if (parseTree instanceof Rec) {
            PreType a = new Unknown();
            return Optional.of(Pair.of(new typed.ski.deep.lang.term.Rec(a),
                    new Function(a, new Function(new Function(new Nat(), new Function(a, a)), new Function(new Nat(), a)))));
        }

        if (parseTree instanceof S_ABC) {
            PreType a = ((S_ABC) parseTree).getA();
            PreType b = ((S_ABC) parseTree).getB();
            PreType c = ((S_ABC) parseTree).getC();

            return Optional.of(Pair.of(new typed.ski.deep.lang.term.S(a, b, c),
                    new Function(new Function(a, new Function(b, c)),
                            new Function(new Function(a, b), new Function(a, c)))));
        }

        if (parseTree instanceof K_AB) {
            PreType a = ((K_AB) parseTree).getA();
            PreType b = ((K_AB) parseTree).getB();

            return Optional.of(Pair.of(new typed.ski.deep.lang.term.K(a, b), new Function(a, new Function(b, a))));
        }

        if (parseTree instanceof I_A) {
            return Optional.of(Pair.of(new typed.ski.deep.lang.term.I(((I_A) parseTree).getA()), new Function(((I_A) parseTree).getA(), ((I_A) parseTree).getA())));
        }

        if (parseTree instanceof Rec_A) {
            PreType recType = ((Rec_A) parseTree).getA();
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

            Preterm leftPreterm = ((App) parseTree).getLeftTerm();

            if (leftPreterm instanceof Rec || (leftPreterm instanceof Rec_A && ((Rec_A) leftPreterm).getA() instanceof Unknown)) {
                leftPreterm = new Rec_A(rightWtt.getRight());
            }
            else if (leftPreterm instanceof K || (leftPreterm instanceof K_AB && ((K_AB) leftPreterm).getA() instanceof Unknown)) {
                leftPreterm = new K_AB(rightWtt.getRight(), leftPreterm instanceof K ? new Unknown() : ((K_AB) leftPreterm).getB());
            }
            else if ((leftPreterm instanceof S || (leftPreterm instanceof S_ABC && ((S_ABC) leftPreterm).getA() instanceof Unknown)) && rightWtt.getRight() instanceof Function) {
                PreType a, b, c;
                a = ((Function) rightWtt.getRight()).getInputType();
                if (leftPreterm instanceof S_ABC) {
                    b = ((S_ABC) leftPreterm).getB();
                    c = ((S_ABC) leftPreterm).getC();
                }
                else {
                    b = new Unknown();
                    c = new Unknown();
                }
                leftPreterm = new S_ABC(a, b, c);
            }
            else if (leftPreterm instanceof I || (leftPreterm instanceof I_A && ((I_A) leftPreterm).getA() instanceof Unknown)) {
                leftPreterm = new I_A(rightWtt.getRight());
            }
            else if (leftPreterm instanceof RecListPre || (leftPreterm instanceof RecListPre_AB && ((RecListPre_AB) leftPreterm).getB() instanceof Unknown)) {
                leftPreterm = new RecListPre_AB(leftPreterm instanceof RecListPre ? new Unknown() : ((RecListPre_AB) leftPreterm).getA(), rightWtt.getRight());
            }

            Optional<Pair<Term, PreType>> leftWttOpt = infer(leftPreterm);

            if (leftWttOpt.isEmpty()) {
                return Optional.empty();
            }
            Pair<Term, PreType> leftWtt = leftWttOpt.get();


            if (leftWtt.getRight() instanceof Function) {
                Function leftType = (Function) leftWtt.getRight();
                if (leftType.getInputType() instanceof Unknown) {
                    leftType = new Function(substituteUnknownInPretypeWithType(leftType.getInputType(), (Unknown) leftType.getInputType(), rightWtt.getRight()), leftType.getResultType());
                }
                if (leftType.getInputType() instanceof typed.ski.deep.lang.type.List &&
                        ((typed.ski.deep.lang.type.List) leftType.getInputType()).getA() instanceof Unknown &&
                        rightWtt.getRight() instanceof typed.ski.deep.lang.type.List) {
                    leftType = new Function(new typed.ski.deep.lang.type.List(((typed.ski.deep.lang.type.List) rightWtt.getRight()).getA()), leftType.getResultType());
                }

                if (areTypesEqual(leftType.getInputType(), rightWtt.getRight())) {
                    return Optional.of(Pair.of(new Application(leftType, rightWtt.getRight(), leftWtt.getLeft(), rightWtt.getLeft()), leftType.getResultType()));
                }
            }

            return Optional.empty();
        }

        if (parseTree instanceof Succ) {
            return Optional.of(Pair.of(new typed.ski.deep.lang.term.Succ(), new Function(new Nat(), new Nat())));
        }

        if (parseTree instanceof EmptyListPre) {
            return Optional.of(Pair.of(new EmptyList(), new typed.ski.deep.lang.type.List(null)));
        }

        if (parseTree instanceof ListItemPre) {
            Pair<Term, PreType> headWtt = infer(((ListItemPre) parseTree).getHead()).orElseThrow();
            Pair<Term, PreType> tailWtt = infer(((ListItemPre) parseTree).getTail()).orElseThrow();

            if (areTypesEqual(tailWtt.getRight(), new typed.ski.deep.lang.type.List(headWtt.getRight()))) {
                return Optional.of(Pair.of(new ListItem(headWtt.getLeft(), (ListItem) tailWtt.getLeft()), new typed.ski.deep.lang.type.List(headWtt.getRight())));
            }
            else {
                throw new IllegalStateException("Type of head and type parameter of tail don't match: " + headWtt.getRight() + ", " + tailWtt.getRight());
            }
        }

        if (parseTree instanceof RecListPre) {
            PreType a = new Unknown();
            PreType b = new Unknown();
            return Optional.of(Pair.of(new RecList(a, b),
                    new Function(b, new Function(new Function(a, new Function(new typed.ski.deep.lang.type.List(a),
                            new Function(b, b))), new Function(new typed.ski.deep.lang.type.List(a), b)))));
        }

        if (parseTree instanceof RecListPre_AB) {
            PreType a = ((RecListPre_AB) parseTree).getA();
            PreType b = ((RecListPre_AB) parseTree).getB();

            return Optional.of(Pair.of(new RecList(a, b),
                    new Function(b, new Function(new Function(a, new Function(new typed.ski.deep.lang.type.List(a),
                            new Function(b, b))), new Function(new typed.ski.deep.lang.type.List(a), b)))));
        }

        if (parseTree instanceof ConsPre) {
            PreType a = new Unknown();
            return Optional.of(Pair.of(new Cons(a), new Function(a, new Function(new typed.ski.deep.lang.type.List(a), new typed.ski.deep.lang.type.List(a)))));
        }

        if (parseTree instanceof Lit) {
            return Optional.of(Pair.of(new Literal(((Lit) parseTree).getName()), new Str()));
        }
        throw new IllegalStateException();
    }

    public static PreType replaceTypeIfUnknown(PreType preType, Map<Integer, PreType> resolvedTypes) {
        if (preType instanceof Unknown) {
            return resolvedTypes.get(((Unknown) preType).getTypeId());
        }
        else if (preType instanceof Function) {
            return new Function(replaceTypeIfUnknown(((Function) preType).getInputType(), resolvedTypes),
                    replaceTypeIfUnknown(((Function) preType).getResultType(), resolvedTypes));
        }
        else if (preType instanceof typed.ski.deep.lang.type.List) {
            return new typed.ski.deep.lang.type.List(replaceTypeIfUnknown(((typed.ski.deep.lang.type.List) preType).getA(), resolvedTypes));
        }
        else {
            return preType;
        }
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

        if (parseTree instanceof S_ABC) {
            List<Unknown> unknowns = new ArrayList<>();

            PreType a = ((S_ABC) parseTree).getA();
            PreType b = ((S_ABC) parseTree).getB();
            PreType c = ((S_ABC) parseTree).getC();

            if (a instanceof Unknown) {
                ((Unknown) a).setTypeId(varCount++);
                unknowns.add((Unknown) a);
            }

            if (b instanceof Unknown) {
                ((Unknown) b).setTypeId(varCount++);
                unknowns.add((Unknown) b);
            }

            if (c instanceof Unknown) {
                ((Unknown) c).setTypeId(varCount);
                unknowns.add((Unknown) c);
            }

            insertIntoUnknownTypes(unknowns.toArray(new Unknown[0]));

            return Optional.of(Pair.of(new typed.ski.deep.lang.term.S(a, b, c),
                    new Function(new Function(a, new Function(b, c)), new Function(new Function(a, b), new Function(a, c)))));
        }

        if (parseTree instanceof K) {
            Unknown unknown1 = new Unknown(varCount++);
            Unknown unknown2 = new Unknown(varCount);
            insertIntoUnknownTypes(unknown1, unknown2);

            return Optional.of(Pair.of(new typed.ski.deep.lang.term.K(unknown1, unknown2),
                    new Function(unknown1, new Function(unknown2, unknown1))));
        }

        if (parseTree instanceof K_AB) {
            List<Unknown> unknowns = new ArrayList<>();

            PreType a = ((K_AB) parseTree).getA();
            PreType b = ((K_AB) parseTree).getB();

            if (a instanceof Unknown) {
                ((Unknown) a).setTypeId(varCount++);
                unknowns.add((Unknown) a);
            }

            if (b instanceof Unknown) {
                ((Unknown) b).setTypeId(varCount);
                unknowns.add((Unknown) b);
            }

            insertIntoUnknownTypes(unknowns.toArray(new Unknown[0]));

            return Optional.of(Pair.of(new typed.ski.deep.lang.term.K(a, b),
                    new Function(a, new Function(b, a))));
        }

        if (parseTree instanceof I) {
            Unknown unknown = new Unknown(varCount);
            insertIntoUnknownTypes(unknown);

            return Optional.of(Pair.of(new typed.ski.deep.lang.term.I(unknown), new Function(unknown, unknown)));
        }

        if (parseTree instanceof I_A) {
            PreType type = ((I_A) parseTree).getA();
            if (type instanceof Unknown) {
                ((Unknown) type).setTypeId(varCount);
                unknownTypes.put(varCount, type);
            }

            return Optional.of(Pair.of(new typed.ski.deep.lang.term.I(type), new Function(type, type)));
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

            //The size of the list must be queried again, the inferWithPreType() calls could change it
            Unknown uknResultType = new Unknown(unknownTypes.size());
            insertIntoUnknownTypes(uknResultType);

            Function expectedFuncType = new Function(right.getRight(), uknResultType);
            typeEquations.add(Pair.of(left.getRight(), expectedFuncType));

            unknownTypes = unify(typeEquations, unknownTypes);

            left.getLeft().substituteUnknownTypes(unknownTypes);
            right.getLeft().substituteUnknownTypes(unknownTypes);

            left = Pair.of(left.getLeft(), replaceTypeIfUnknown(left.getRight(), unknownTypes));
            right = Pair.of(right.getLeft(), replaceTypeIfUnknown(right.getRight(), unknownTypes));

            return Optional.of(Pair.of(new Application(left.getRight(), right.getRight(),
                    left.getLeft(), right.getLeft()), replaceTypeIfUnknown(expectedFuncType.getResultType(), unknownTypes)));
        }

        if (parseTree instanceof AnnotatedPreterm) {

            Optional<Pair<Term, PreType>> inferredOptional = inferWithPreType(((AnnotatedPreterm) parseTree).getPreterm());
            if (inferredOptional.isPresent()) {
                Pair<Term, PreType> pair = inferredOptional.get();

                List<Pair<PreType, PreType>> typeEquations = new ArrayList<>();
                typeEquations.add(Pair.of(pair.getRight(), ((AnnotatedPreterm) parseTree).getType()));

                unknownTypes = unify(typeEquations, unknownTypes);
                pair.getLeft().substituteUnknownTypes(unknownTypes);

                return Optional.of(Pair.of(pair.getLeft(), replaceTypeIfUnknown(pair.getRight(), unknownTypes)));
            }

            throw new IllegalStateException("Could not infer term: " + ((AnnotatedPreterm) parseTree).getPreterm());
        }

        if (parseTree instanceof Lit) {
            return Optional.of(Pair.of(new Literal(((Lit) parseTree).getName()), new Str()));
        }

        if (parseTree instanceof ZERO) {
            return Optional.of(Pair.of(new typed.ski.deep.lang.term.ZERO(), new Nat()));
        }

        if (parseTree instanceof Succ) {
            return Optional.of(Pair.of(new typed.ski.deep.lang.term.Succ(), new Function(new Nat(), new Nat())));
        }

        if (parseTree instanceof ITE) {
            Unknown unknown = new Unknown(varCount);
            insertIntoUnknownTypes(unknown);

            return Optional.of(Pair.of(new typed.ski.deep.lang.term.ITE(unknown), new Function(new Bool(), new Function(unknown, new Function(unknown, unknown)))));
        }

        if (parseTree instanceof Rec) {
            Unknown unknown = new Unknown(varCount);
            insertIntoUnknownTypes(unknown);

            return Optional.of(Pair.of(new typed.ski.deep.lang.term.Rec(unknown),
                    new Function(unknown, new Function(new Function(new Nat(), new Function(unknown, unknown)), new Function(new Nat(), unknown)))));
        }

        if (parseTree instanceof Rec_A) {
            PreType type = ((Rec_A) parseTree).getA();
            if (type instanceof Unknown) {
                ((Unknown) type).setTypeId(varCount);
                unknownTypes.put(varCount, type);
            }

            return Optional.of(Pair.of(new typed.ski.deep.lang.term.Rec(type),
                    new Function(type, new Function(new Function(new Nat(), new Function(type, type)), new Function(new Nat(), type)))));
        }

        if (parseTree instanceof EmptyListPre) {
            return Optional.of(Pair.of(new EmptyList(), new typed.ski.deep.lang.type.List(null)));
        }

        if (parseTree instanceof ListItemPre) {
            Pair<Term, PreType> inferredHead = inferWithPreType(((ListItemPre) parseTree).getHead()).orElseThrow();
            Pair<Term, PreType> inferredTail = inferWithPreType(((ListItemPre) parseTree).getTail()).orElseThrow();

            if (areTypesEqual(inferredTail.getRight(), new typed.ski.deep.lang.type.List(inferredHead.getRight()))) {
                return Optional.of(Pair.of(new ListItem(inferredHead.getLeft(), (ListItem) inferredTail.getLeft()), new typed.ski.deep.lang.type.List(inferredHead.getRight())));
            }
            else {
                throw new IllegalStateException("Type of head and type parameter of tail don't match: " + inferredHead.getRight() + ", " + inferredTail.getRight());
            }
        }

        if (parseTree instanceof RecListPre) {
            Unknown a = new Unknown(varCount++);
            Unknown b = new Unknown(varCount);
            insertIntoUnknownTypes(a, b);

            return Optional.of(Pair.of(new RecList(a, b),
                    new Function(b, new Function(new Function(a, new Function(new typed.ski.deep.lang.type.List(a),
                            new Function(b, b))), new Function(new typed.ski.deep.lang.type.List(a), b)))));
        }

        if (parseTree instanceof RecListPre_AB) {
            List<Unknown> unknowns = new ArrayList<>();

            PreType a = ((RecListPre_AB) parseTree).getA();
            PreType b = ((RecListPre_AB) parseTree).getB();

            if (a instanceof Unknown) {
                ((Unknown) a).setTypeId(varCount++);
                unknowns.add((Unknown) a);
            }

            if (b instanceof Unknown) {
                ((Unknown) b).setTypeId(varCount);
                unknowns.add((Unknown) b);
            }

            insertIntoUnknownTypes(unknowns.toArray(new Unknown[0]));

            return Optional.of(Pair.of(new RecList(a, b),
                    new Function(b, new Function(new Function(a, new Function(new typed.ski.deep.lang.type.List(a),
                            new Function(b, b))), new Function(new typed.ski.deep.lang.type.List(a), b)))));
        }

        if (parseTree instanceof ConsPre) {
            Unknown unknown = new Unknown(varCount);
            insertIntoUnknownTypes(unknown);

            return Optional.of(Pair.of(new Cons(unknown),
                    new Function(unknown, new Function(new typed.ski.deep.lang.type.List(unknown), new typed.ski.deep.lang.type.List(unknown)))));
        }

        return Optional.empty();
    }

    private static Map<Integer, PreType> unify(List<Pair<PreType, PreType>> typeEquations, Map<Integer, PreType> unknownTypes) {
        Map<Integer, PreType> types = SerializationUtils.clone((HashMap<Integer, PreType>) unknownTypes);

        if (!typeEquations.isEmpty()) {
            Pair<PreType, PreType> pair = typeEquations.remove(typeEquations.size() - 1);

            if (pair.getLeft() instanceof Unknown || pair.getRight() instanceof Unknown) {
                final Unknown unknown;
                final PreType preType;
                if (pair.getLeft() instanceof Unknown) {
                    unknown = (Unknown) pair.getLeft();
                    preType = pair.getRight();
                }
                else {
                    unknown = (Unknown) pair.getRight();
                    preType = pair.getLeft();
                }

                if (!doesPretypeContainUnknown(preType, unknown)) {
                    typeEquations = typeEquations.stream()
                            .map(entry -> {
                                PreType left = entry.getLeft();
                                PreType right = entry.getRight();

                                if (doesPretypeContainUnknown(left, unknown)) {
                                    left = substituteUnknownInPretypeWithType(left, unknown, preType);
                                }
                                if (doesPretypeContainUnknown(right, unknown)) {
                                    right = substituteUnknownInPretypeWithType(right, unknown, preType);
                                }
                                return Pair.of(left, right);
                            })
                            .collect(Collectors.toList());
                }
                else {
                    throw new IllegalStateException("Could not apply unify rule, one type contains the unknown type\n" +
                            preType + ", " + unknown);
                }

                types.put(unknown.getTypeId(), preType);
                types = types.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> substituteUnknownInPretypeWithType(entry.getValue(), unknown, preType)));
                return unify(typeEquations, types);
            }
            else if (pair.getLeft() instanceof Function && pair.getRight() instanceof Function) {
                typeEquations.add(Pair.of(((Function) pair.getLeft()).getResultType(), ((Function) pair.getRight()).getResultType()));
                typeEquations.add(Pair.of(((Function) pair.getLeft()).getInputType(), ((Function) pair.getRight()).getInputType()));

                return unify(typeEquations, types);
            }
            else if (pair.getLeft() instanceof typed.ski.deep.lang.type.List && pair.getRight() instanceof typed.ski.deep.lang.type.List) {
                PreType type = ((typed.ski.deep.lang.type.List) pair.getLeft()).getA();
                PreType otherType = ((typed.ski.deep.lang.type.List) pair.getRight()).getA();

                if (type != null && otherType != null) {
                    typeEquations.add(Pair.of(type, otherType));
                }
                return unify(typeEquations, types);
            }
            else if (areTypesEqual(pair.getLeft(), pair.getRight())) {
                return unify(typeEquations, types);
            } else {
                throw new IllegalStateException("Not matching types: " + pair.getLeft() + ", " + pair.getRight());
            }
        }

        return types;
    }

    private static Optional<Term> check(Preterm parseTree, PreType type) {
        if (parseTree instanceof S || parseTree instanceof S_ABC) {
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
                    if (!areTypesEqual(((Function) param2).getInputType(), A) || !areTypesEqual(((Function) param2).getResultType(), B)) {
                        return Optional.empty();
                    }

                    // Compare type of the 3rd param (A) and the return type (C)
                    if (!areTypesEqual(((Function) param3AndReturnType).getInputType(), A) || !areTypesEqual(((Function) param3AndReturnType).getResultType(), C)) {
                        return Optional.empty();
                    }

                    if (parseTree instanceof S_ABC &&
                            ((!(((S_ABC) parseTree).getA() instanceof Unknown) && !areTypesEqual(((S_ABC) parseTree).getA(), A)) ||
                            (!(((S_ABC) parseTree).getB() instanceof Unknown) && !areTypesEqual(((S_ABC) parseTree).getB(), B)) ||
                            (!(((S_ABC) parseTree).getC() instanceof Unknown) && !areTypesEqual(((S_ABC) parseTree).getC(), C)))) {
                        return Optional.empty();
                    }

                    return Optional.of(new typed.ski.deep.lang.term.S(A, B, C));
                }
            }
        }
        else if (parseTree instanceof K) {
            if (type instanceof Function && ((Function) type).getResultType() instanceof Function && ((Function) type).getInputType().getClass().equals(((Function) ((Function) type).getResultType()).getResultType().getClass())) {
                return Optional.of(new typed.ski.deep.lang.term.K(((Function) type).getInputType(), ((Function) ((Function) type).getResultType()).getInputType()));
            }
        }
        else if (parseTree instanceof K_AB) {
            if (type instanceof Function && ((Function) type).getResultType() instanceof Function && ((Function) type).getInputType().getClass().equals(((Function) ((Function) type).getResultType()).getResultType().getClass())) {
                PreType a = ((K_AB) parseTree).getA() instanceof Unknown ? ((Function) type).getInputType() : ((K_AB) parseTree).getA();
                PreType b = ((K_AB) parseTree).getB() instanceof Unknown ? ((Function) ((Function) type).getResultType()).getInputType() : ((K_AB) parseTree).getB();

                return Optional.of(new typed.ski.deep.lang.term.K(a, b));
            }
        }
        else if (parseTree instanceof I) {
            if (type instanceof Function && ((Function) type).getInputType().getClass().equals(((Function) type).getResultType().getClass())) {
                return Optional.of(new typed.ski.deep.lang.term.I(((Function) type).getInputType()));
            }
        }
        else if (parseTree instanceof I_A) {
            if (type instanceof Function && areTypesEqual(((Function) type).getInputType(), ((Function) type).getResultType())) {
                PreType a = ((I_A) parseTree).getA() instanceof Unknown ? ((Function) type).getInputType() : ((I_A) parseTree).getA();
                return Optional.of(new typed.ski.deep.lang.term.I(a));
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
        else if (parseTree instanceof Rec_A) {
            PreType recType = ((Rec_A) parseTree).getA();
            PreType expectedType = new Function(recType, new Function(new Function(new Nat(), new Function(recType, recType)),
                    new Function(new Nat(), recType)));

            if (areTypesEqual(expectedType, type)) {
                return Optional.of(new typed.ski.deep.lang.term.Rec(recType));
            }
        }
        else if (parseTree instanceof RecListPre || parseTree instanceof RecListPre_AB) {

            PreType expectedTypeStructure = new Function(new Unknown(), new Function(new Function(new Unknown(), new Function(new typed.ski.deep.lang.type.List(new Unknown()),
                    new Function(new Unknown(), new Unknown()))), new Function(new typed.ski.deep.lang.type.List(new Unknown()), new Unknown())));

            if (checkTypeStructures(expectedTypeStructure, type)) {

                PreType A = ((Function) ((Function) ((Function) type).getResultType()).getInputType()).getInputType();
                PreType B = ((Function) type).getInputType();

                if (parseTree instanceof RecListPre_AB &&
                        ((!(((RecListPre_AB) parseTree).getA() instanceof Unknown) && !areTypesEqual(((RecListPre_AB) parseTree).getA(), A)) ||
                        (!(((RecListPre_AB) parseTree).getB() instanceof Unknown) && !areTypesEqual(((RecListPre_AB) parseTree).getB(), B)))) {
                    return Optional.empty();
                }

                if (A instanceof Unknown && parseTree instanceof RecListPre_AB) {
                    A = ((RecListPre_AB) parseTree).getA();
                }
                if (B instanceof Unknown && parseTree instanceof RecListPre_AB) {
                    B = ((RecListPre_AB) parseTree).getB();
                }

                return Optional.of(new RecList(A, B));
            }
        }
        else if (parseTree instanceof ConsPre) {
            if (type instanceof Function) {
                PreType inputType = ((Function) type).getInputType();
                PreType expectedType = new Function(inputType, new Function(new typed.ski.deep.lang.type.List(inputType), new typed.ski.deep.lang.type.List(inputType)));
                return areTypesEqual(type, expectedType) ? Optional.of(new Cons(inputType)) : Optional.empty();
            }
            return Optional.empty();
        }

        return Optional.empty();
    }

    private static boolean compareFunctions(Function fun1, Function fun2) {
        return areTypesEqual(fun1.getInputType(), fun2.getInputType()) && areTypesEqual(fun1.getResultType(), fun2.getResultType());
    }

    private static boolean areTypesEqual(PreType type, PreType otherType) {
        if (type == null || otherType == null) {
            return false;
        }

        if (type instanceof Function && otherType instanceof Function) {
            return compareFunctions((Function) type, (Function) otherType);
        }
        else if (type instanceof typed.ski.deep.lang.type.List && otherType instanceof typed.ski.deep.lang.type.List) {
            if (((typed.ski.deep.lang.type.List) type).getA() == null || ((typed.ski.deep.lang.type.List) otherType).getA() == null) {
                return true;
            }
            else {
                return areTypesEqual(((typed.ski.deep.lang.type.List) type).getA(), ((typed.ski.deep.lang.type.List) otherType).getA());
            }
        }
        else {
            return type.getClass().equals(otherType.getClass());
        }
    }

    /*
    Used in type chicking of ReclistPre and RecListPre_AB
    Works similarly to areTypesEqual function, but at this point we just want to check the structure of the two given
    types, in order to avoid stucking on Unknown types.
    Unknown types are going to be inferred later.
    E.g.
    A == B : True
    A -> B == A -> C : True
    A == A -> B : False
    List<A> == B : False
     */
    private static boolean checkTypeStructures(PreType type, PreType otherType) {
        if (type == null || otherType == null) {
            return false;
        }

        if (type instanceof Function && otherType instanceof Function) {
            return checkTypeStructures(((Function) type).getInputType(), ((Function) otherType).getInputType()) && checkTypeStructures(((Function) type).getResultType(), ((Function) otherType).getResultType());
        }
        else if (type instanceof typed.ski.deep.lang.type.List && otherType instanceof typed.ski.deep.lang.type.List) {
            if (((typed.ski.deep.lang.type.List) type).getA() == null || ((typed.ski.deep.lang.type.List) otherType).getA() == null) {
                return true;
            }
            else {
                return checkTypeStructures(((typed.ski.deep.lang.type.List) type).getA(), ((typed.ski.deep.lang.type.List) otherType).getA());
            }
        }
        else {
            return true;
        }
    }

    private static PreType substituteUnknownInPretypeWithType(PreType preType, Unknown unknown, PreType replaceWith) {
        if (preType instanceof Unknown && ((Unknown) preType).getTypeId() == unknown.getTypeId()) {
            return replaceWith;
        }
        else if (preType instanceof Function) {
            return new Function(substituteUnknownInPretypeWithType(((Function) preType).getInputType(), unknown, replaceWith),
                    substituteUnknownInPretypeWithType(((Function) preType).getResultType(), unknown, replaceWith));
        }
        else if (preType instanceof typed.ski.deep.lang.type.List) {
            return new typed.ski.deep.lang.type.List(substituteUnknownInPretypeWithType(((typed.ski.deep.lang.type.List) preType).getA(), unknown, replaceWith));
        }
        else {
            return preType;
        }
    }

    private static void insertIntoUnknownTypes(Unknown... unknowns) {
        Stream.of(unknowns).forEach(unknown -> unknownTypes.put(unknown.getTypeId(), unknown));
    }

    private static boolean doesPretypeContainUnknown(PreType preType, Unknown unknown) {
        if (preType instanceof Unknown) {
            return ((Unknown) preType).getTypeId() == unknown.getTypeId();
        }
        else if (preType instanceof Function) {
            return doesPretypeContainUnknown(((Function) preType).getInputType(), unknown) || doesPretypeContainUnknown(((Function) preType).getResultType(), unknown);
        }
        else if (preType instanceof typed.ski.deep.lang.type.List) {
            return doesPretypeContainUnknown(((typed.ski.deep.lang.type.List) preType).getA(), unknown);
        }
        return false;
    }
}
