package typed.ski.deep.lang.type;

public class Unknown implements PreType {

    private final int typeId;

    public Unknown(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }

    @Override
    public String toString() {
        return "Unknown{" + typeId + "}";
    }
}
