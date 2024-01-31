package typed.ski.deep.lang.term;

import typed.ski.deep.lang.type.PreType;

import static typed.ski.deep.typechecker.TypeChecker.replaceTypeIfUnknown;

public class S implements Term {

    private PreType x;

    private PreType y;

    private PreType z;

    public S(PreType x, PreType y, PreType z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PreType getX() {
        return x;
    }

    public PreType getY() {
        return y;
    }

    public PreType getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "S{" + x + "}{" + y + "}{" + z + "}";
    }

    @Override
    public void substituteUnknownTypes() {
        x = replaceTypeIfUnknown(x);
        y = replaceTypeIfUnknown(y);
        z = replaceTypeIfUnknown(z);
    }
}
