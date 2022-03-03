package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.Function;
import typed.ski.deep.lang.type.Nat;
import typed.ski.deep.lang.type.Ty;

public class Application implements Term {

    private final Ty leftType;

    private final Ty rightType;

    private final Term leftTerm;

    private final Term rightTerm;

    public Application(Ty leftType, Ty rightType, Term leftTerm, Term rightTerm) {
        this.leftType = leftType;
        this.rightType = rightType;
        this.leftTerm = leftTerm;
        this.rightTerm = rightTerm;
    }

    public Ty getLeftType() {
        return leftType;
    }

    public Term apply() {

        if (leftTerm instanceof S || leftTerm instanceof K || leftTerm instanceof Literal || leftTerm instanceof True ||
                leftTerm instanceof False || leftTerm instanceof ITE || leftTerm instanceof Rec) {
            return this;
        }

        if (leftTerm instanceof  I) {
            return rightTerm;
        }

        if (leftTerm instanceof Succ) {
            return new Application(
                    new Function(new Nat(), new Nat()),
                    new Nat(),
                    leftTerm,
                    rightTerm); // apply() ---  , move to L31
        }

        if (leftTerm instanceof Application) {
            Application subApplication = (Application) leftTerm;

            if (subApplication.getLeftTerm() instanceof S) {
                return this;
            }

            if (subApplication.getLeftTerm() instanceof K) {
                return subApplication.getRightTerm();
            }

            if (subApplication.getLeftTerm() instanceof ITE) {
                return this;
            }

            if (subApplication.getLeftTerm() instanceof Rec) {
                return this;
            }

            if (subApplication.getLeftTerm() instanceof Application) {
                if (((Application) subApplication.getLeftTerm()).getLeftTerm() instanceof S) {
                    S termS = (S) ((Application) subApplication.getLeftTerm()).getLeftTerm();
                    return new Application(((Function) termS.getX()).getResultType(), ((Function) termS.getY()).getResultType(),
                            new Application(termS.getX(), termS.getZ(),
                                    ((Application) subApplication.getLeftTerm()).getRightTerm(), rightTerm).apply(),
                            new Application(termS.getY(), termS.getZ(), subApplication.getRightTerm(), rightTerm).apply()).apply();
                }

                if (((Application) subApplication.getLeftTerm()).getLeftTerm() instanceof ITE) {
                    return Boolean.parseBoolean(((Application) subApplication.getLeftTerm()).getRightTerm().toString()) ?
                            subApplication.getRightTerm() : rightTerm;
                }

                if (((Application) subApplication.getLeftTerm()).getLeftTerm() instanceof Rec) {
                    Term termZ = ((Application) subApplication.getLeftTerm()).getRightTerm();
                    if (rightTerm instanceof ZERO) {
                        return termZ;
                    }
                    //rightTerm == Succ n
                    else if (rightTerm instanceof Application) {
                        Rec termRec = (Rec) ((Application) subApplication.getLeftTerm()).getLeftTerm();
                        Ty recTypeParam = termRec.getX();
                        Ty typeRec = ((Application) subApplication.getLeftTerm()).getLeftType();
                        Term termS = subApplication.getRightTerm();
                        Ty typeS = subApplication.getRightType();
                        Application appSN = new Application(subApplication.getRightType(),
                                ((Application) rightTerm).getRightType(), termS, ((Application) rightTerm).getRightTerm());
                        Application appRec = new Application(
                                new Function(new Nat(), recTypeParam), new Nat(),
                                new Application(
                                        new Function(typeS, new Function(new Nat(), recTypeParam)), typeS,
                                        new Application(typeRec, recTypeParam, termRec, termZ),
                                        termS),
                                ((Application) rightTerm).getRightTerm());
                        return new Application(new Function(recTypeParam, recTypeParam), recTypeParam, appSN.apply(), appRec.apply()).apply();
                    }
                }
            }
        }

        throw new IllegalStateException();
    }

    public Ty getRightType() {
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
        return leftTerm + " " + rightTerm;
    }
}
