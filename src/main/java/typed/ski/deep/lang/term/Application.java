package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.*;

import java.util.Map;

import static typed.ski.deep.typechecker.TypeChecker.replaceTypeIfUnknown;

public class Application implements Term {

    private PreType leftType;

    private PreType rightType;

    private final Term leftTerm;

    private final Term rightTerm;

    public Application(PreType leftType, PreType rightType, Term leftTerm, Term rightTerm) {
        this.leftType = leftType;
        this.rightType = rightType;
        this.leftTerm = leftTerm;
        this.rightTerm = rightTerm;
    }

    public PreType getLeftType() {
        return leftType;
    }

    public Term apply() {

        if (leftTerm instanceof Application) {
            Application subApplication = (Application) leftTerm;

            if (subApplication.getLeftTerm() instanceof Application) {

                if (((Application) subApplication.getLeftTerm()).getLeftTerm() instanceof Rec) {
                    Term termZ = ((Application) subApplication.getLeftTerm()).getRightTerm();
                    if (rightTerm instanceof ZERO) {
                        return termZ;
                    }
                    else if (rightTerm instanceof Application) {
                        Rec termRec = (Rec) ((Application) subApplication.getLeftTerm()).getLeftTerm();
                        PreType recTypeParam = termRec.getX();
                        PreType typeRec = ((Application) subApplication.getLeftTerm()).getLeftType();
                        Term termS = subApplication.getRightTerm();
                        PreType typeS = subApplication.getRightType();
                        Application appSN = new Application(subApplication.getRightType(),
                                ((Application) rightTerm).getRightType(), termS, ((Application) rightTerm).getRightTerm());
                        Application appRec = new Application(
                                new Function(new Nat(), recTypeParam), new Nat(),
                                new Application(
                                        new Function(typeS, new Function(new Nat(), recTypeParam)), typeS,
                                        new Application(typeRec, recTypeParam, termRec, termZ).apply(),
                                        termS).apply(),
                                ((Application) rightTerm).getRightTerm());
                        return new Application(new Function(recTypeParam, recTypeParam), recTypeParam, appSN.apply(), appRec.apply()).apply();
                    }
                }

                if (((Application) subApplication.getLeftTerm()).getLeftTerm() instanceof S) {
                    S termS = (S) ((Application) subApplication.getLeftTerm()).getLeftTerm();

                    return new Application(new Function(termS.getY(), termS.getZ()), termS.getY(),
                            new Application(((Application) subApplication.getLeftTerm()).getRightType(), rightType,
                                    ((Application) subApplication.getLeftTerm()).getRightTerm(), rightTerm).apply(),
                            new Application(subApplication.getRightType(), rightType,
                                    subApplication.getRightTerm(), rightTerm).apply()).apply();
                }

                if (((Application) subApplication.getLeftTerm()).getLeftTerm() instanceof RecList) {
                    Term termN = ((Application) subApplication.getLeftTerm()).getRightTerm();
                    if (rightTerm instanceof EmptyList) {
                        return termN;
                    }
                    else {
                        RecList termRecList = (RecList) ((Application) subApplication.getLeftTerm()).getLeftTerm();
                        PreType typeRecList = ((Application) subApplication.getLeftTerm()).getLeftType();
                        PreType recListTypeParam = termRecList.getB();
                        PreType listTypeParam = termRecList.getA();
                        Term termC = subApplication.getRightTerm();
                        PreType typeC = subApplication.getRightType();

                        Application appRecListTail = new Application(
                                new Function(new List(listTypeParam), recListTypeParam),
                                rightType,
                                new Application(
                                        new Function(typeC, new Function(new List(listTypeParam), recListTypeParam)),
                                        typeC,
                                        new Application(
                                                typeRecList,
                                                recListTypeParam,
                                                termRecList,
                                                termN
                                        ).apply(),
                                        termC
                                ).apply(),
                                ((Application) rightTerm).getRightTerm()
                        );

                        return new Application(
                                new Function(recListTypeParam, recListTypeParam),
                                recListTypeParam,
                                new Application(
                                        new Function(new List(listTypeParam), new Function(recListTypeParam, recListTypeParam)),
                                        rightType,
                                        new Application(
                                                typeC,
                                                listTypeParam,
                                                termC,
                                                ((Application) ((Application) rightTerm).getLeftTerm()).getRightTerm()
                                        ).apply(),
                                        ((Application) rightTerm).getRightTerm()
                                ).apply(),
                                appRecListTail.apply()
                        ).apply();
                    }
                }

                if (((Application) subApplication.getLeftTerm()).getLeftTerm() instanceof ITE) {
                    return Boolean.parseBoolean(((Application) subApplication.getLeftTerm()).getRightTerm().toString()) ?
                            subApplication.getRightTerm() : rightTerm;
                }
            }

            if (subApplication.getLeftTerm() instanceof Rec) {
                return this;
            }

            if (subApplication.getLeftTerm() instanceof K) {
                return subApplication.getRightTerm();
            }

            if (subApplication.getLeftTerm() instanceof S) {
                return this;
            }

            if (subApplication.getLeftTerm() instanceof Cons) {
                return this;
            }

            if (subApplication.getLeftTerm() instanceof RecList) {
                return this;
            }

            if (subApplication.getLeftTerm() instanceof ITE) {
                return this;
            }
        }

        if (leftTerm instanceof Succ || leftTerm instanceof K || leftTerm instanceof S || leftTerm instanceof True ||
                leftTerm instanceof False ||  leftTerm instanceof Rec || leftTerm instanceof Cons || leftTerm instanceof RecList ||
                leftTerm instanceof ITE || leftTerm instanceof Literal) {
            return this;
        }

        if (leftTerm instanceof  I) {
            return rightTerm;
        }

        throw new IllegalStateException();
    }

    public PreType getRightType() {
        return rightType;
    }

    public Term getLeftTerm() {
        return leftTerm;
    }

    public Term getRightTerm() {
        return rightTerm;
    }

    @Override
    public String toString() {
        return "(" + leftTerm + " " + rightTerm + ")";
    }

    @Override
    public String toString(boolean prettyPrint) {
        if (prettyPrint) {
            if (isItAList()) {
                return toListFormat();
            }
            else if (isItANat()) {
                return toNatFormat();
            }
        }
        return toString();
    }

    @Override
    public void substituteUnknownTypes(Map<Integer, PreType> resolvedTypes) {
        leftType = replaceTypeIfUnknown(leftType, resolvedTypes);
        rightType = replaceTypeIfUnknown(rightType, resolvedTypes);
        leftTerm.substituteUnknownTypes(resolvedTypes);
        rightTerm.substituteUnknownTypes(resolvedTypes);
    }

    private boolean isItAList() {
        return leftTerm instanceof Application && ((Application) leftTerm).getLeftTerm() instanceof Cons;
    }

    private boolean isItANat() {
        return leftTerm instanceof Succ;
    }

    private String toListFormat() {
        return "[" + ((Application) leftTerm).getRightTerm().toString(true) + "," + rightTerm.toString(true) + "]";
    }

    private String toNatFormat() {
        return String.valueOf(Integer.parseInt(rightTerm.toString(true)) + 1);
    }
}
