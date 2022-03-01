package typed.ski.deep.lang.type;

public class Function implements Ty {

    private final Ty inputType;

    private final Ty resultType;

    public Function(Ty inputType, Ty resultType) {
        this.inputType = inputType;
        this.resultType = resultType;
    }

    public Ty getInputType() {
        return inputType;
    }

    public Ty getResultType() {
        return resultType;
    }

    @Override
    public String toString() {
        return inputType + "->" + resultType;
    }
}
