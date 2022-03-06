package typed.ski.deep.lang.type;

public class Function implements Ty {

    private final PreType inputType;

    private final PreType resultType;

    public Function(PreType inputType, PreType resultType) {
        this.inputType = inputType;
        this.resultType = resultType;
    }

    public PreType getInputType() {
        return inputType;
    }

    public PreType getResultType() {
        return resultType;
    }

    @Override
    public String toString() {
        return inputType + "->" + resultType;
    }
}
