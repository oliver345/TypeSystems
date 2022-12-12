package typed.ski.deep.lang.type;

public class Unknown implements PreType {

    private int typeId;

    public Unknown() {
        typeId = -1;
    }

    public Unknown(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return "Unknown(" + typeId + ")";
    }
}
