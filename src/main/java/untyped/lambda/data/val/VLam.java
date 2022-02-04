package untyped.lambda.data.val;

import untyped.lambda.data.Closure;

public class VLam implements Val {

    private final Closure closure;

    public VLam(Closure closure) {
        this.closure = closure;
    }

    public Closure getClosure() {
        return closure;
    }

    //Todo: toString
}
