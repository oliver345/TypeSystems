package typed.ski.deep.lang.type;

import typed.ski.deep.lang.preterm.Preterm;

public class Unknown implements Preterm {

    private final int typeId;

    public Unknown(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }
}
