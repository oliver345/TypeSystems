package untyped.lambda.data.val;

public class VVar implements Val {

    private final char name;

    public VVar(char name) {
        this.name = name;
    }

    public char getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
