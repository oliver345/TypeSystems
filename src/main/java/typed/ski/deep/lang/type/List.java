package typed.ski.deep.lang.type;

public class List implements Ty {

    private final PreType a;

    public List(PreType a) {
        this.a = a;
    }

    public PreType getA() {
        return a;
    }

    @Override
    public String toString() {
        return "List{" + a + "}";
    }
}
