package lambda.data.val;

public class VApp implements Val {

    private final Val leftVal;

    private final Val rightVal;

    public VApp(Val leftVal, Val rightVal) {
        this.leftVal = leftVal;
        this.rightVal = rightVal;
    }

    public Val getLeftVal() {
        return leftVal;
    }

    public Val getRightVal() {
        return rightVal;
    }

    @Override
    public String toString() {
        return "(" + leftVal + ":" + rightVal + ")";
    }
}
